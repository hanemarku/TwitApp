package com.example.twitapp.fragments;

import static com.example.twitapp.util.Constants.DATA_TWEETS;
import static com.example.twitapp.util.Constants.DATA_TWEET_HASHTAGS;

import android.os.Bundle;

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
import com.example.twitapp.listeners.TweetListener;
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
    private String userId;
    private RecyclerView tweetList;
    private boolean hashtagFollowed = false;
    private ImageView followHashtag;
    private TweetListener listener = null;

    private SwipeRefreshLayout swipeRefresh;
    private FirebaseAuth auth;

    private List<Tweet> tweetListArray;



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
        userId = auth.getCurrentUser().getUid();
        swipeRefresh = view.findViewById(R.id.swipeRefresh);

        tweetList = view.findViewById(R.id.tweetList);

        tweetListArray = new ArrayList<>();

        tweetsAdapter = new TweetListAdapter(userId, new ArrayList<>());
        tweetsAdapter.setListener(listener);

        tweetList.setLayoutManager(new LinearLayoutManager(getContext()));
        tweetList.setAdapter(tweetsAdapter);
        tweetList.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        swipeRefresh.setOnRefreshListener(() -> {
            swipeRefresh.setRefreshing(false);
            updateList();
        });




    }

    public void newHashtag(String term) {
        currentHashtag = term;
        followHashtag.setVisibility(View.VISIBLE);
        updateList();
    }



    public void updateList() {
        tweetList.setVisibility(View.GONE);
        firebaseDB.collection(DATA_TWEETS)
                .whereArrayContains(DATA_TWEET_HASHTAGS, currentHashtag)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    tweetList.setVisibility(View.VISIBLE);
                    ArrayList<Tweet> tweets = new ArrayList<>();
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        Tweet tweet = document.toObject(Tweet.class);
                        if (tweet != null) {
                            tweets.add(tweet);
                        }
                    }
                    ArrayList<Tweet> sortedTweets = sortTweetsByTimestamp(tweets);
                    tweetsAdapter.updateTweets(sortedTweets);
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    // Print the error message
                    Log.e("SearchFragment", "Error retrieving tweets: " + e.getMessage());
                });
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
}