package com.example.twitapp.listeners;


import android.app.AlertDialog;
import android.content.Intent;
import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

import com.example.twitapp.activities.OtherUserProfileActivity;
import com.example.twitapp.util.Constants;
import com.example.twitapp.util.models.Tweet;
import com.example.twitapp.util.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class TwitterListenerImpl implements TweetListener {

    private RecyclerView tweetList;
    private User user;
    private HomeCallback callback;
    private FirebaseFirestore firebaseDB;
    private String userId;

    public TwitterListenerImpl(RecyclerView tweetList, User user, HomeCallback callback) {
        this.tweetList = tweetList;
        this.user = user;
        this.callback = callback;
        firebaseDB = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    @Override
    public void onLayoutClick(Tweet tweet) {
        if (tweet != null) {
            String owner = tweet.getUserIds().get(0);
            if (!owner.equals(userId)) {
                //find user with userId
                firebaseDB.collection(Constants.DATA_USERS)
                        .document(userId)
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            user = documentSnapshot.toObject(User.class);

                        })
                        .addOnFailureListener(e -> Log.e("ERROR", e.getLocalizedMessage()));
                Log.i("INFO", "user: " + user + " owner: " + owner);
                Log.i("INFO", "user.getFollowUsers(): " + user.getFollowUsers());
                //print in log all user.getFollowUsers()
                if (user.getFollowUsers() != null) {
                    for (String followedUser : user.getFollowUsers()) {
                        Log.i("INFO", "followedUser: " + followedUser);
                    }
                }
                // if user.getFollowUsers() is null make the list empty
                if (user.getFollowUsers() == null) {
                    user.setFollowUsers(new ArrayList<>());
                }
                if (user.getFollowUsers().contains(owner)) {
                    new AlertDialog.Builder(tweetList.getContext())
                            .setTitle("Unfollow " + tweet.getUsername() + "?")
                            .setPositiveButton("yes", (dialog, which) -> {
                                tweetList.setClickable(false);
                                ArrayList<String> followedUsers = user.getFollowUsers();
                                if (followedUsers == null) {
                                    followedUsers = new ArrayList<>();
                                }
                                followedUsers.remove(owner);
                                firebaseDB.collection(Constants.DATA_USERS)
                                        .document(userId)
                                        .update(Constants.DATA_USER_FOLLOW, followedUsers)
                                        .addOnSuccessListener(aVoid -> {
                                            tweetList.setClickable(true);
                                            callback.onUserUpdated();
                                        })
                                        .addOnFailureListener(e -> tweetList.setClickable(true));
                            })
                            .setNegativeButton("cancel", (dialog, which) -> {})
                            .show();
                } else {
                    new AlertDialog.Builder(tweetList.getContext())
                            .setTitle("Follow " + tweet.getUsername() + "?")
                            .setPositiveButton("yes", (dialog, which) -> {
                                tweetList.setClickable(false);
                                ArrayList<String> followedUsers = user.getFollowUsers();
                                if (followedUsers == null) {
                                    followedUsers = new ArrayList<>();
                                }
                                followedUsers.add(owner);
                                firebaseDB.collection(Constants.DATA_USERS)
                                        .document(userId)
                                        .update(Constants.DATA_USER_FOLLOW, followedUsers)
                                        .addOnSuccessListener(aVoid -> {
                                            tweetList.setClickable(true);
                                            callback.onUserUpdated();
                                        })
                                        .addOnFailureListener(e -> tweetList.setClickable(true));
                            })
                            .setNegativeButton("cancel", (dialog, which) -> {})
                            .show();
                }
            }
        }
    }

    @Override
    public void onLike(Tweet tweet) {
        if (tweet != null) {
            tweetList.setClickable(false);
            ArrayList<String> likes = tweet.getLikes();
            if (likes.contains(userId)) {
                likes.remove(userId);
            } else {
                likes.add(userId);
            }
            firebaseDB.collection(Constants.DATA_TWEETS)
                    .document(tweet.getTweetId())
                    .update(Constants.DATA_TWEETS_LIKES, likes)
                    .addOnSuccessListener(aVoid -> {
                        tweetList.setClickable(true);
                        callback.onRefresh();
                    })
                    .addOnFailureListener(e -> tweetList.setClickable(true));
        }
    }

    @Override
    public void onRetweet(Tweet tweet) {
        if (tweet != null) {
            tweetList.setClickable(false);
            ArrayList<String> retweets = tweet.getUserIds();
            if (retweets.contains(userId)) {
                retweets.remove(userId);
            } else {
                retweets.add(userId);
            }
            firebaseDB.collection(Constants.DATA_TWEETS)
                    .document(tweet.getTweetId())
                    .update(Constants.DATA_TWEET_USER_IDS, retweets)
                    .addOnSuccessListener(aVoid -> {
                        tweetList.setClickable(true);
                        callback.onRefresh();
                    })
                    .addOnFailureListener(e -> tweetList.setClickable(true));
        }
    }

    @Override
    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public void goToUserProfile(Object userId) {
        if (userId != null) {
            Intent intent = new Intent(tweetList.getContext(), OtherUserProfileActivity.class);
            intent.putExtra(Constants.EXTRA_ID, userId.toString());
            tweetList.getContext().startActivity(intent);
        }
    }
}
