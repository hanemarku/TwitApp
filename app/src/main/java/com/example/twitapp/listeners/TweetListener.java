package com.example.twitapp.listeners;

import com.example.twitapp.util.models.Tweet;
import com.example.twitapp.util.models.User;

public interface TweetListener {
    void onLayoutClick(Tweet tweet);
    void onLike(Tweet tweet);
    void onRetweet(Tweet tweet);
    void setUser(User user);

    void goToUserProfile(Object userId);
//    void goToUserProfile(String userId);
}
