package com.birdy;

import java.util.List;

public class TweetResponse {
    public List<Tweet> tweets;
    public long numResponses;

    public TweetResponse(List<Tweet> tweets) {
        this.tweets = tweets;
    }

    public TweetResponse(List<Tweet> tweets, long numResponses) {
        this.tweets = tweets;
        this.numResponses = numResponses;
    }
}
