const fs = require('fs')
const path = require('path')
const Twit = require('twit')
const config = require('config')
const rp = require('request-promise')
const cheerio = require('cheerio')
const { fork } = require('child_process')

const T = new Twit(config.get('twitter'))
const tweetProcessor = fork(`${__dirname}/lib/tweetProcessor.js`)

const linkRegex = /https?:\/\/[\-a-zA-Z\.?0-9@:%._\+~#=\/&]+/
let fileNumber = 0
let outputFile = fs.createWriteStream(getOutputFilename())
const pendingQueue = []
let pendingRequests = 0
let writeSize = 0

function getOutputFilename() {
  return path.join(config.get('dataDir'), `tweets-${fileNumber}.json`)
}

async function grabLinkData (tweet) {
  if (pendingRequests >= config.get('requestLimit')) {
    pendingQueue.push(tweet)
    return
  }
  const link = linkRegex.exec(tweet.text)[0]
  console.log('Got link:', link)
  const res = await rp.get(link)
  const $ = cheerio.load(res)
  const title = $('title').text()
  console.log('Got title: ', title)
  tweet.title = title
  writeToFile(tweet)
  pendingRequests--
  if(pendingQueue.length > 0) {
    grabLinkData(pendingQueue.pop())
  }
}

function writeToFile(tweet) {
  const tweetString = JSON.stringify(tweet) + '\n'
  writeSize += tweetString.length
  outputFile.write(tweetString)
  console.log('Output buffer size:', writeSize)
  if(writeSize > 10000000) {
    writeSize = 0
    outputFile.close()
    fileNumber++
    outputFile = fs.createWriteStream(getOutputFilename())
  }
}

const sanFrancisco = [ '-122.75', '36.8', '-121.75', '37.8' ]

const stream = T.stream('statuses/filter', { locations: sanFrancisco, tweet_mode: 'extended' })
console.log('Starting Twitter Streaming API')

stream.on('tweet', (tweet) => {
  if (linkRegex.test(tweet.text)) {
    grabLinkData(tweet)
  } else {
    writeToFile(tweet)
  }
})

// let stream = T.stream('statuses/sample')

// stream.on('tweet', function (tweet) {
//   console.log(tweet)
// })
