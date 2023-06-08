package com.example.twitapp.adapters;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.twitapp.R;
import com.example.twitapp.listeners.TweetListener;
import com.example.twitapp.util.ImageUtil;
import com.example.twitapp.util.models.Tweet;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TweetListAdapter extends RecyclerView.Adapter<TweetListAdapter.TweetViewHolder> {
    private String userId;
    private ArrayList<Tweet> tweets;
    private TweetListener listener;


    public TweetListAdapter(String userId, ArrayList<Tweet> tweets) {
        this.userId = userId;
        this.tweets = tweets;
    }

    public void setListener(TweetListener listener) {
        this.listener = listener;
    }

    public void updateTweets(List<Tweet> newTweets) {
        tweets.clear();
        if (newTweets != null && !newTweets.isEmpty()) {
            tweets.addAll(newTweets);
        }
        notifyDataSetChanged();
    }



    @NonNull
    @Override
    public TweetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tweet, parent, false);
        return new TweetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TweetViewHolder holder, int position) {
        holder.bind(userId, tweets.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return tweets.size();
    }


    public class TweetViewHolder extends RecyclerView.ViewHolder{

        private ViewGroup layout;
        private TextView username;
        private TextView text;
        private ImageView image;
        private TextView date;
        private ImageView like;
        private TextView likeCount;
        private ImageView retweet;
        private TextView retweetCount;

        public TweetViewHolder(View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.tweetLayout);
            username = itemView.findViewById(R.id.tweetUsername);
            text = itemView.findViewById(R.id.tweetText);
            image = itemView.findViewById(R.id.tweetImage);
            date = itemView.findViewById(R.id.tweetDate);
            like = itemView.findViewById(R.id.tweetLike);
            likeCount = itemView.findViewById(R.id.tweetLikeCount);
            retweet = itemView.findViewById(R.id.tweetRetweet);
            retweetCount = itemView.findViewById(R.id.tweetRetweetCount);
        }

        public void bind(String userId, Tweet tweet, TweetListener listener) {
            Context context = itemView.getContext();

            username.setText(tweet.getUsername());
            text.setText(tweet.getText());

            if (tweet.getImageUrl() == null || tweet.getImageUrl().isEmpty()) {
                image.setVisibility(View.GONE);
            } else {
                image.setVisibility(View.VISIBLE);
                ImageUtil.loadUrl(image, tweet.getImageUrl(), R.drawable.empty);
            }

            date.setText(ImageUtil.getDate(tweet.getTimestamp()));
//            likeCount.setText(String.valueOf(tweet.getLikes().size()));
//            retweetCount.setText(String.valueOf(tweet.getUserIds().size() - 1));
            // Set initial like state and count
            updateLikeButton(like ,context, tweet);
            updateLikeCount(likeCount ,tweet);

            // Set initial retweet state and count
            updateRetweetButton(retweet ,context, tweet);
            updateRetweetCount(retweetCount ,tweet);

            layout.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onLayoutClick(tweet);
                }
            });

            like.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onLike(tweet);
                    updateLikeButton(like, context, tweet);
                    updateLikeCount(likeCount ,tweet);
                }
            });

            retweet.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRetweet(tweet);
                    updateRetweetButton(retweet, context, tweet);
                    updateRetweetCount(retweetCount, tweet);
                }
            });

            username.setOnClickListener(v -> {
                if (listener != null) {
                    listener.goToUserProfile(tweet.getUserId());
                }
            });


        }
        }
        private void updateLikeButton(ImageView like,Context context,Tweet tweet) {
            if (tweet.getLikes().contains(userId)) {
                like.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.like));
            } else {
                like.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.like_inactive));
            }
        }

        private void updateLikeCount(TextView likeCount, Tweet tweet) {
            likeCount.setText(String.valueOf(tweet.getLikes().size()));
        }

        private void updateRetweetButton(ImageView retweet, Context context , Tweet tweet) {
            if (!tweet.getUserIds().isEmpty() && tweet.getUserIds().get(0).equals(userId)) {
                        retweet.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.original));
                        retweet.setClickable(false);
            }
            else if (tweet.getUserIds().contains(userId)) {
                retweet.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.retweet));
            } else {
                retweet.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.retweet_inactive));
            }
        }

        private void updateRetweetCount(TextView retweetCount,Tweet tweet) {
            retweetCount.setText(String.valueOf(tweet.getUserIds().size()));
        }
    }

