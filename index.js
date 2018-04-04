const fs = require('fs')
const path = require('path')
const debug = require('debug')('birdy')
const Twit = require('twit')
const config = require('config')
const rp = require('request-promise')
const cheerio = require('cheerio')

const T = new Twit(config.get('twitter'))

const pendingQueue = []
let fileNumber = 0
let pendingRequests = 0
let writeSize = 0
const linkRegex = /https?:\/\/[-a-zA-Z.?0-9@:%._+~#=/&]+/

try {
  while (fs.statSync(getOutputFilename())) {
    fileNumber++
  }
} catch (err) {
  if (err.code === 'ENOENT') {
    console.log('Set output file to', getOutputFilename())
  } else {
    debug('Unknown error:', err)
  }
}

let outputFile = fs.createWriteStream(getOutputFilename())

function getOutputFilename () {
  return path.join(config.get('dataDir'), `tweets-${fileNumber}.json`)
}

async function grabLinkData (tweet) {
  if (pendingRequests >= config.get('requestLimit')) {
    pendingQueue.push(tweet)
    return
  }
  try {
    const link = linkRegex.exec(tweet.text)[0]
    debug('Got link:', link)
    const res = await rp.get(link)
    const $ = cheerio.load(res)
    const title = $('title').text()
    debug('Got title: ', title)
    tweet.title = title
    writeToFile(tweet)
    pendingRequests--
    if (pendingQueue.length > 0) {
      setImmediate(() => grabLinkData(pendingQueue.pop()))
    }
  } catch (err) {
    console.error('Failed to get tweet: ', tweet.id, tweet.text)
    console.error(err)
  }
}

function writeToFile (tweet) {
  const tweetString = JSON.stringify(tweet) + '\n'
  writeSize += tweetString.length
  outputFile.write(tweetString)
  debug('Output buffer size:', writeSize)
  if (writeSize > 10000000) {
    writeSize = 0
    outputFile.close()
    fileNumber++
    outputFile = fs.createWriteStream(getOutputFilename())
  }
}

const stream = T.stream('statuses/sample', { tweet_mode: 'extended' })
console.log('Starting Twitter Streaming API')

stream.on('tweet', (tweet) => {
  if (linkRegex.test(tweet.text)) {
    grabLinkData(tweet)
  } else {
    writeToFile(tweet)
  }
})
