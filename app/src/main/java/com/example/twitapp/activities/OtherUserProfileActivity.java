package com.example.twitapp.activities;

import static com.example.twitapp.util.Constants.DATA_TWEETS;
import static com.example.twitapp.util.Constants.DATA_TWEET_USER_IDS;
import static java.security.AccessController.getContext;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.twitapp.R;
import com.example.twitapp.adapters.TweetListAdapter;
import com.example.twitapp.listeners.HomeCallback;
import com.example.twitapp.listeners.TweetListener;
import com.example.twitapp.listeners.TwitterListenerImpl;
import com.example.twitapp.util.Constants;
import com.example.twitapp.util.ImageUtil;
import com.example.twitapp.util.models.Tweet;
import com.example.twitapp.util.models.User;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OtherUserProfileActivity extends AppCompatActivity {
    private FirebaseFirestore firebaseDB;
    private ImageView profileImageView;
    private TextView usernameTextView;
    private RecyclerView tweetList;
    private String imageUrl = null;
    private User user;
    private TweetListAdapter tweetsAdapter;
    private TweetListener listener;
    protected HomeCallback callback = null;
    String userId = null;;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_user_profile);
        firebaseDB = FirebaseFirestore.getInstance();
        usernameTextView = findViewById(R.id.usernameTextView);
        profileImageView = findViewById(R.id.profileImageView);
        tweetList = findViewById(R.id.tweetList);



        Intent intent = getIntent();
        userId = intent.getStringExtra(Constants.EXTRA_ID);

        firebaseDB.collection(Constants.DATA_USERS)
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        user = documentSnapshot.toObject(User.class);
                        displayUserData(user);
                        Log.i("USER", user.toString());
                    } else {
                        Log.e("ERROR", "User not found");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ERROR", e.getLocalizedMessage());
                });


        if (intent != null && intent.hasExtra(Constants.EXTRA_ID)) {
            fetchUserData(userId);
            listener = new TwitterListenerImpl(tweetList, user, callback);

            tweetsAdapter = new TweetListAdapter(userId, new ArrayList<>());
            tweetsAdapter.setListener(listener);
            tweetList.setLayoutManager(new LinearLayoutManager(this));
            tweetList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

            tweetList.setAdapter(tweetsAdapter);


        } else {
            Log.i("ERROR", "No user found");
        }
    }

    private void fetchUserData(String userId) {
        firebaseDB.collection(Constants.DATA_USERS)
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        user = documentSnapshot.toObject(User.class);
                        displayUserData(user);
                        Log.i("USER", user.toString());
                    } else {
                        Log.e("ERROR", "User not found");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("ERROR", e.getLocalizedMessage());
                });

    }

    private void displayUserData(User user) {
        usernameTextView.setText(user.getUsername());
        imageUrl = user != null ? user.getImageUrl() : null;

        if (imageUrl != null) {
            ImageUtil.loadUrl(profileImageView, imageUrl, R.drawable.logo);
        }


        List<Tweet> tweets = new ArrayList<>();

        FirebaseFirestore.getInstance()
                .collection(DATA_TWEETS)
                .whereArrayContains(DATA_TWEET_USER_IDS, userId)
                .get()
                .addOnSuccessListener((QuerySnapshot querySnapshot) -> {
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        Tweet tweet = document.toObject(Tweet.class);
                        if (tweet != null) {
                            tweets.add(tweet);
                        }
                    }

                    Collections.sort(tweets, (o1, o2) -> o2.getTimestamp().compareTo(o1.getTimestamp()));

                    if (tweetsAdapter != null) {
                        tweetsAdapter.updateTweets(tweets);
                    } else {
                        tweetsAdapter = new TweetListAdapter(userId, (ArrayList<Tweet>) tweets);
                        tweetList.setAdapter(tweetsAdapter);
                    }

                    for (Tweet tweet : tweets) {
                        Log.d("FirebaseData", "Tweet: " + tweet.getText());
                    }
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                });
    }

}