package com.example.twitapp.util.models;

import java.util.ArrayList;

public class User {
    private String email;
    private String username;
    private String imageUrl;
    private ArrayList<String> followHashtags;
    private ArrayList<String> followUsers;


    public User() {
    }

    public User(String email, String username, String imageUrl, ArrayList<String> followHashtags, ArrayList<String> followUsers) {
        this.email = email;
        this.username = username;
        this.imageUrl = imageUrl;
        this.followHashtags = followHashtags;
        this.followUsers = followUsers;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public ArrayList<String> getFollowHashtags() {
        return followHashtags;
    }

    public void setFollowHashtags(ArrayList<String> followHashtags) {
        this.followHashtags = followHashtags;
    }

    public ArrayList<String> getFollowUsers() {
        return followUsers;
    }

    public void setFollowUsers(ArrayList<String> followUsers) {
        this.followUsers = followUsers;
    }
}
