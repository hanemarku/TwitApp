package com.example.twitapp.fragments;

import static com.example.twitapp.util.Constants.DATA_TWEETS;
import static com.example.twitapp.util.Constants.DATA_TWEET_USER_IDS;

import android.content.Context;
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
import com.example.twitapp.R;
import com.example.twitapp.adapters.TweetListAdapter;
import com.example.twitapp.listeners.HomeCallback;
import com.example.twitapp.listeners.TweetListener;
import com.example.twitapp.listeners.TwitterListenerImpl;
import com.example.twitapp.util.models.Tweet;
import com.example.twitapp.util.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MyActivityFragment extends Fragment {
    private RecyclerView tweetList;
    private SwipeRefreshLayout swipeRefresh;
    private TweetListAdapter tweetsAdapter;
    private TweetListener listener;
    private User currentUser;
    private FirebaseFirestore firebaseDB;
    protected HomeCallback callback = null;
    protected String userId = null;
;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_activity, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tweetList = view.findViewById(R.id.tweetList);
        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        currentUser = new User();
        firebaseDB = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        firebaseDB.collection("Users").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            currentUser = document.toObject(User.class);
                            updateList();
                        } else {
                            Log.i("MyActivityFragment", "No such document");
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
    }


    public void updateList() {
        tweetList.setVisibility(View.GONE);
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

                    tweetList.setVisibility(View.VISIBLE);

                    // Print the retrieved tweets
                    for (Tweet tweet : tweets) {
                        Log.d("FirebaseData", "Tweet: " + tweet.getText());
                    }
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    tweetList.setVisibility(View.VISIBLE);
                });
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
}
