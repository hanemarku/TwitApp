package com.example.twitapp.fragments;

import static com.example.twitapp.util.Constants.DATA_TWEETS;
import static com.example.twitapp.util.Constants.DATA_TWEET_HASHTAGS;
import static com.example.twitapp.util.Constants.DATA_TWEET_USER_IDS;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;


public class HomePageFragment extends Fragment {

    private RecyclerView tweetList;
    private TweetListAdapter tweetsAdapter;
    private TweetListener listener;
    private User currentUser;
    private FirebaseFirestore firebaseDB;
    protected HomeCallback callback = null;
    protected String userId = null;
    private SwipeRefreshLayout swipeRefresh;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home_page, container, false);
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tweetList = view.findViewById(R.id.tweetList);
        currentUser = new User();
        firebaseDB = FirebaseFirestore.getInstance();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //find current user
//        currentUser = firebaseDB.collection("users").document(userId).get().getResult().toObject(User.class);
        firebaseDB.collection("Users").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            currentUser = document.toObject(User.class);
                            updateList(); // Call the updateList method after retrieving the current user
                        } else {
                            // Document does not exist
                        }
                    } else {
                        // Task failed with an exception
                        Exception exception = task.getException();
                        if (exception != null) {
                            exception.printStackTrace();
                        }
                    }
                });

        listener = new TwitterListenerImpl(tweetList, currentUser, callback);

        tweetsAdapter = new TweetListAdapter(userId, new ArrayList<>());
        tweetsAdapter.setListener(listener);
        tweetList = view.findViewById(R.id.tweetList);
        tweetList.setLayoutManager(new LinearLayoutManager(getContext()));
        tweetList.setAdapter(tweetsAdapter);
        tweetList.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        swipeRefresh = view.findViewById(R.id.swipeRefresh);

        swipeRefresh.setOnRefreshListener(() -> {
            swipeRefresh.setRefreshing(false);
            updateList();
        });
    }

//    public void setUser(User user) {
//        this.currentUser = user;
//        if (listener != null) {
//            listener.setUser(user);
//        }
//    }


    public void updateList() {
        if (tweetList != null) {
            tweetList.setVisibility(View.GONE);
        }
        if (currentUser != null) {
            List<Tweet> tweets = new ArrayList<>();

            if (currentUser.getFollowHashtags() != null) {
                for (String hashtag : currentUser.getFollowHashtags()) {
                    firebaseDB.collection(DATA_TWEETS).whereArrayContains(DATA_TWEET_HASHTAGS, hashtag).get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                for (DocumentSnapshot document : queryDocumentSnapshots) {
                                    Tweet tweet = document.toObject(Tweet.class);
                                    if (tweet != null) {
                                        tweets.add(tweet);
                                    }
                                }
                                updateAdapter(tweets);
                                if (tweetList != null) {
                                    tweetList.setVisibility(View.VISIBLE);
                                }
                            })
                            .addOnFailureListener(e -> {
                                e.printStackTrace();
                                if (tweetList != null) {
                                    tweetList.setVisibility(View.VISIBLE);
                                }
                            });
                }
            }

            if (currentUser.getFollowUsers() != null) {
                for (String followedUser : currentUser.getFollowUsers()) {
                    firebaseDB.collection(DATA_TWEETS).whereArrayContains(DATA_TWEET_USER_IDS, followedUser).get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                for (DocumentSnapshot document : queryDocumentSnapshots) {
                                    Tweet tweet = document.toObject(Tweet.class);
                                    if (tweet != null) {
                                        tweets.add(tweet);
                                    }
                                }
                                updateAdapter(tweets);
                                if (tweetList != null) {
                                    tweetList.setVisibility(View.VISIBLE);
                                }
                            })
                            .addOnFailureListener(e -> {
                                e.printStackTrace();
                                if (tweetList != null) {
                                    tweetList.setVisibility(View.VISIBLE);
                                }
                            });
                }
            }
        }
    }

    private void updateAdapter(List<Tweet> tweets) {
        List<Tweet> sortedTweets = new ArrayList<>(tweets);
        sortedTweets.sort((o1, o2) -> Long.compare(o2.getTimestamp(), o1.getTimestamp()));
        tweetsAdapter.updateTweets(removeDuplicates(sortedTweets));
    }

    private List<Tweet> removeDuplicates(List<Tweet> originalList) {
        return new ArrayList<>(new LinkedHashSet<>(originalList));
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

//    public void setUser(User user) {
//        this.currentUser = user;
//        if (listener != null) {
//            listener.setUser(user);
//        }
//    }

    @Override
    public void onResume() {
        super.onResume();
        updateList();
    }

//    public void updateList() {
//
//    }
}