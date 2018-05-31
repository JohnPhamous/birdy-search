# Birdy

> A web app to index and search Twitter Tweets 

[Demo Link](http://birdysearch.com/)

## Architecture

Birdy is split into 3 parts:

* Birdy Gather
* Birdy Frontend
* Birdy Lucene

## Birdy Gather

Birdy Gather is a Node program that uses the Twitter streaming API and stores geotagged Tweets. If a Tweet contains a URL, then Birdy Gather will visit the page and fetch the metadata of that page. To ensure speed, we use a Master/Slave architecture. The master gathers the Tweets and will either write the Tweet directly to disk or pass on the Tweet to a slave for the slave to gather the URLs metadata.


To use Birdy Gather, go inside `gather-tweets` and type `npm install`. Afterward, type in `npm run prod`.

## Birdy Frontend

Birdy Frontend is built using Vue.js. It supports searching for Tweets around a location, highlighting matched terms, and hyperlinking URLs and user references. 

## Birdy Lucene
