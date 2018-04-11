const rp = require('request-promise')
const config = require('config')
const cheerio = require('cheerio')
const debug = require('debug')('birdy:worker')

const pendingQueue = [] // queue for tweets that need to get links fetched
let pendingRequests = 0 // number of pending requests (used to limit # of requests)
const linkRegex = config.get('linkRegex')

/**
 * Given a tweet with a link, fetch the contents of the link and add it to
 *  the tweet JSON. If there are too many pending requests, add it to the
 *  queue instead.
 * @param   {Twit.Tweet}  tweet  The tweet object
 */
async function grabLinkData (tweet) {
  if (pendingRequests >= config.get('requestLimit')) {
    if (pendingQueue.length > 100) {
      console.log(
        'Warning: Request queue too large; at size:',
        pendingQueue.length
      )
    }
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
    process.send(tweet)
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
    console.error(
      err.message.length > 150
        ? err.message.substring(0, 147) + '...'
        : err.message
    )
  }
}

process.on('message', tweet => {
  if (tweet.id) {
    grabLinkData(tweet)
  }
})

process.on('warning', e => console.warn(e.stack))
