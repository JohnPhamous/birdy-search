let fs = require('fs')
let Twit = require('twit')
let twitterAuth = require('./config/twitterAuth')

let T = new Twit(twitterAuth)

let sanFrancisco = [ '-122.75', '36.8', '-121.75', '37.8' ]

let stream = T.stream('statuses/filter', { locations: sanFrancisco })

stream.on('tweet', function (tweet) {
  console.log(tweet)
})
