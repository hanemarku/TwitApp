package com.example.twitapp.util.models;

import java.util.ArrayList;
import java.util.Collections;

public class Tweet {
    private String tweetId;
    private ArrayList<String> userIds;
    private String username;
    private String text;
    private String imageUrl;
    private Long timestamp;
    private ArrayList<String> hashtags;
    private ArrayList<String> likes;

    public Tweet() {
    }

    public Tweet(String tweetId, String username, String text){
        this.tweetId = tweetId;
        this.username = username;
        this.text = text;
    }

    public Tweet(String tweetId, ArrayList<String> userIds, String username, String text, String imageUrl, Long timestamp, ArrayList<String> hashtags, ArrayList<String> likes) {
        this.tweetId = tweetId;
        this.userIds = userIds;
        this.username = username;
        this.text = text;
        this.imageUrl = imageUrl;
        this.timestamp = timestamp;
        this.hashtags = hashtags;
        this.likes = likes;
    }

    public Tweet(String tweetId, String userId, String userName, String tweetText, ArrayList<String> hashTags, String imageUrl, long time) {
        this.tweetId = tweetId;
        this.userIds = new ArrayList<>(Collections.singleton(userId));
        this.username = userName;
        this.text = tweetText;
        this.hashtags = hashTags;
        this.imageUrl = imageUrl;
        this.timestamp = time;
    }



    public String getTweetId() {
        return tweetId;
    }

    public void setTweetId(String tweetId) {
        this.tweetId = tweetId;
    }

    public ArrayList<String> getUserIds() {
        if (userIds == null) {
            userIds = new ArrayList<>();
        }
        return userIds;
    }

    public void setUserIds(ArrayList<String> userIds) {
        this.userIds = userIds;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public ArrayList<String> getHashtags() {
        return hashtags;
    }

    public void setHashtags(ArrayList<String> hashtags) {
        this.hashtags = hashtags;
    }

    public ArrayList<String> getLikes() {
        if (likes == null) {
            likes = new ArrayList<>();
        }
        return likes;
    }

    public void setLikes(ArrayList<String> likes) {
        this.likes = likes;
    }

    public Object getUserId() {
        return userIds.get(0);
    }
}
