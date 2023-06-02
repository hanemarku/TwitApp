package com.example.twitapp.fragments;

import static com.example.twitapp.util.Constants.DATA_TWEETS;
import static com.example.twitapp.util.Constants.DATA_TWEET_HASHTAGS;
import static com.example.twitapp.util.Constants.DATA_USERS;
import static com.example.twitapp.util.Constants.DATA_USER_HASHTAGS;

import android.content.Context;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.twitapp.R;
import com.example.twitapp.adapters.TweetAdapter;
import com.example.twitapp.adapters.TweetListAdapter;
import com.example.twitapp.listeners.HomeCallback;
import com.example.twitapp.listeners.TweetListener;
import com.example.twitapp.listeners.TwitterListenerImpl;
import com.example.twitapp.util.models.Tweet;
import com.example.twitapp.util.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class SearchFragment extends Fragment {

    private String currentHashtag = "";
    private TweetListAdapter tweetsAdapter;
    private User currentUser;
    private FirebaseFirestore firebaseDB;
    private RecyclerView tweetList;

    private boolean hashtagFollowed = false;
    private ImageView followHashtag;
    private TweetListener listener;
    private SwipeRefreshLayout swipeRefresh;
    private FirebaseAuth auth;
    private String userId = null;
    private HomeCallback callback = null;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        followHashtag = view.findViewById(R.id.followHashtag);
        firebaseDB = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        currentUser = new User();
        userId = auth.getCurrentUser().getUid();
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        tweetList = view.findViewById(R.id.tweetList);


        firebaseDB.collection("Users").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            currentUser = document.toObject(User.class);
                            updateList();
                        } else {
                            Log.i("MySearchFragment", "No such document");
                        }
                    } else {
                        Exception exception = task.getException();
                        if (exception != null) {
                            exception.printStackTrace();
                        }
                    }
                });

        listener = new TwitterListenerImpl(tweetList, currentUser, callback);

        tweetsAdapter = new TweetListAdapter(userId, new ArrayList<>());
        tweetsAdapter.setListener(listener);

        tweetList.setLayoutManager(new LinearLayoutManager(getContext()));
        tweetList.setAdapter(tweetsAdapter);
        tweetList.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        swipeRefresh.setOnRefreshListener(() -> {
            swipeRefresh.setRefreshing(false);
            updateList();
        });

        view.findViewById(R.id.followHashtag).setOnClickListener(v -> {
            v.setClickable(false);
            List<String> followed = currentUser.getFollowHashtags();
            if (hashtagFollowed) {
                followed.remove(currentHashtag);
            } else {
                followed.add(currentHashtag);
            }
            firebaseDB.collection(DATA_USERS).document(userId)
                    .update(DATA_USER_HASHTAGS, followed)
                    .addOnSuccessListener(aVoid -> {
                        if (callback != null) {
                            callback.onUserUpdated();
                        }
                        v.setClickable(true);
                        updateFollowDrawable();
                    })
                    .addOnFailureListener(e -> {
                        e.printStackTrace();
                        v.setClickable(true);
                    });
        });
    }

    public void newHashtag(String term) {
        currentHashtag = term;
//        Log.i("SearchFragment", "New hashtag: " + currentHashtag);
        followHashtag.setVisibility(View.VISIBLE);
        updateList();
    }



    public void updateList() {
        tweetList.setVisibility(View.GONE);
        List<Tweet> tweets = new ArrayList<>();
        FirebaseFirestore.getInstance()
                .collection(DATA_TWEETS)
                .whereArrayContains(DATA_TWEET_HASHTAGS, currentHashtag)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    Log.i("SearchFragment", "Tweets: " + DATA_TWEETS);
                    Log.i("SearchFragment", "Tweets: " + currentHashtag + " " + querySnapshot.size());
//                    ArrayList<Tweet> tweets = new ArrayList<>();
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


//                    sortTweetsByTimestamp(tweets);
                    tweetsAdapter.updateTweets(tweets);
                    tweetList.setVisibility(View.VISIBLE);

                    // Print the retrieved tweets
                    for (Tweet tweet : tweets) {
                        Log.d("FirebaseData", "Tweet: " + tweet.getText());
                    }

                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    // Print the error message
                    Log.e("SearchFragment", "Error retrieving tweets: " + e.getMessage());
                });
        updateFollowDrawable();
    }


    private ArrayList<Tweet> sortTweetsByTimestamp(ArrayList<Tweet> tweets) {
        Collections.sort(tweets, new Comparator<Tweet>() {
            @Override
            public int compare(Tweet t1, Tweet t2) {
                return t2.getTimestamp().compareTo(t1.getTimestamp());
            }
        });
        return tweets;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof HomeCallback) {
            callback = (HomeCallback) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement HomeCallback");
        }
    }


    private void updateFollowDrawable() {
        hashtagFollowed = currentUser != null && currentUser.getFollowHashtags() != null && currentUser.getFollowHashtags().contains(currentHashtag);
        Context context = getContext();
        if (context != null) {
            if (hashtagFollowed) {
                followHashtag.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.follow));
            } else {
                followHashtag.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.follow_inactive));
            }
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        updateList();
    }
}