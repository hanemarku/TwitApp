package com.example.twitapp.listeners;

import com.example.twitapp.util.models.Tweet;

public interface TweetListener {
    void onLayoutClick(Tweet tweet);
    void onLike(Tweet tweet);
    void onRetweet(Tweet tweet);
}
