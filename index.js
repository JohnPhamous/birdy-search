// Import dependencies
const fs = require('fs')
const path = require('path')
const debug = require('debug')('birdy:main')
const Twit = require('twit')
const config = require('config')
const { fork } = require('child_process')

const workers = []
const numWorkers = config.get('numWorkers')
let currentWorker = 0

// creates numWorkers of workers
for (let i = 0; i < numWorkers; i++) {
  workers.push(fork('./lib/urlWorker.js'))
  // once worker grabs the link metadata, write to file
  workers[i].on('message', processedTweet => {
    writeToFile(processedTweet)
  })
}

// Instantiate the Twit object
const T = new Twit(config.get('twitter'))

// Instantiate constants
let fileNumber = 0 // number of output files, used in file name
let writeSize = 0 // number of characters that have been written to the current file
let totalSize = 0 // total number of characters written
const linkRegex = config.get('linkRegex')

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
  if (
    writeSize > config.get('fileSizeLimit') ||
        totalSize >= config.get('totalSizeLimit')
  ) {
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

// Creating a bounding box for the world
// We want to gather all geotagged tweets
const world = ['-180', '-90', '180', '90']
// const world = [ '-122.75', '36.8', '-121.75', '37.8' ]
// Create the tweet stream
const tweetStream = T.stream('statuses/filter', { locations: world })
console.log('Starting Twitter Streaming API')

tweetStream.on('tweet', tweet => {
  // On a tweet, test if it contains a link
  if (linkRegex.test(tweet.text)) {
    // If it contains a link, use grabLinkData() to process it
    workers[currentWorker].send(tweet)
    currentWorker = (currentWorker + 1) % numWorkers
  } else {
    // Otherwise, write it directly to a file
    writeToFile(tweet)
  }
})

process.on('warning', e => console.warn(e.stack))
