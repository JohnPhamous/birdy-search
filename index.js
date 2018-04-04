// Import dependencies
const fs = require('fs')
const path = require('path')
const debug = require('debug')('birdy')
const Twit = require('twit')
const config = require('config')
const rp = require('request-promise')
const cheerio = require('cheerio')

// Instantiate the Twit object
const T = new Twit(config.get('twitter'))

// Instantiate constants
const pendingQueue = [] // queue for tweets that need to get links fetched
let fileNumber = 0 // number of output files, used in file name
let pendingRequests = 0 // number of pending requests (used to limit # of requests)
let writeSize = 0 // number of characters that have been written to the current file
let totalSize = 0 // total number of characters written
const linkRegex = /https?:\/\/[-a-zA-Z.?0-9@:%._+~#=/&]+/ // A regex used to find links

// Find the last file number to make sure we don't accidentally overwrite previously saved data
try {
  while (fs.statSync(getOutputFilename())) {
    // If an ENOENT error is not thrown, the file exists
    fileNumber++ // Check the next file
  }
} catch (err) {
  if (err.code === 'ENOENT') {
    console.log('Set output file to', getOutputFilename())
  } else {
    debug('Unknown error:', err)
  }
}

// Open a write stream to the output file
let outputFile = fs.createWriteStream(getOutputFilename())

/**
 * Convinience function to return the path to the output file
 * @return   {String}  The output file path
 */
function getOutputFilename () {
  return path.join(config.get('dataDir'), `tweets-${fileNumber}.json`)
}

/**
 * Given a tweet with a link, fetch the contents of the link and add it to
 *  the tweet JSON. If there are too many pending requests, add it to the
 *  queue instead.
 * @param   {Twit.Tweet}  tweet  The tweet object
 */
async function grabLinkData (tweet) {
  if (pendingRequests >= config.get('requestLimit')) {
    if (pendingRequests > 100) console.log('Warning: Request queue too large; at size:', pendingRequests)
    // If there are too many pending requests, add it to the queue
    pendingQueue.push(tweet)
    return
  }
  // Otherwise, fetch the title from the tweet
  pendingRequests++
  try {
    const link = linkRegex.exec(tweet.text)[0] // extract the link using the regex
    debug('Got link:', link)
    const res = await rp.get(link) // Get the contents of the page
    const $ = cheerio.load(res) // Load the document into the cheerio HTML parser
    const title = $('title').text() // Get the title of the page
    debug('Got title: ', title)
    tweet.title = title // Update the tweet object with the title
    writeToFile(tweet)
    // Lower the number of pending requests (since the current request is already processed)
    pendingRequests--
    if (pendingQueue.length > 0) {
      // If the queue is not empty, process the next item.
      //  Note that we use the setImmediate function to avoid blocking the event loop,
      //  which would result in new tweets being processed slowly.
      setImmediate(() => grabLinkData(pendingQueue.pop()))
    }
  } catch (err) {
    pendingRequests--
    // If we can't get the tweet's link, log it and ignore it,
    //  since the site probably doesn't like automated scrapers.
    console.error('Failed to get tweet: ', tweet.id, tweet.text)
    console.error(err.name)
    // Print error message and truncate it if it is too long
    console.error(err.message.length > 150 ? err.message.substring(0, 147) + '...' : err.message)
  }
}

/**
 * Given a tweet object, write it to the file.
 *  Also rotates files if the current file is too large.
 * @param   {Twit.Tweet}  tweet  The tweet to write to a file
 */
function writeToFile (tweet) {
  // Convert the tweet to JSON
  const tweetString = JSON.stringify(tweet) + '\n'
  writeSize += tweetString.length
  totalSize += tweetString.length
  outputFile.write(tweetString)
  debug('Output buffer size:', writeSize)

  // Check if the number of characters exceeds the file character size limit
  //  which, by default, is equal to ~10 MB of text
  if (writeSize > config.get('fileSizeLimit') || totalSize >= config.get('totalSizeLimit')) {
    writeSize = 0
    outputFile.close()
    // Rotate file streams to the next one
    fileNumber++
    outputFile = fs.createWriteStream(getOutputFilename())
    if (totalSize >= config.get('totalSizeLimit')) {
      console.log('Reached the total size limit. Exiting...')
      process.exit(0)
    }
  }
}

// Create the tweet stream
const tweetStream = T.stream('statuses/sample', { tweet_mode: 'extended' })
console.log('Starting Twitter Streaming API')

tweetStream.on('tweet', (tweet) => {
  // On a tweet, test if it contains a link
  if (linkRegex.test(tweet.text)) {
    // If it contains a link, use grabLinkData() to process it
    grabLinkData(tweet)
  } else {
    // Otherwise, write it directly to a file
    writeToFile(tweet)
  }
})
