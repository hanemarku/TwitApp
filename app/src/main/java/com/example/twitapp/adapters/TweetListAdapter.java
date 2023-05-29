package com.example.twitapp.adapters;

import android.content.Context;
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
        tweets.addAll(newTweets);
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
            likeCount.setText(String.valueOf(tweet.getLikes().size()));
            retweetCount.setText(String.valueOf(tweet.getUserIds().size() - 1));

            layout.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onLayoutClick(tweet);
                }
            });

            like.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onLike(tweet);
                }
            });

            retweet.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRetweet(tweet);
                }
            });

            boolean isLiked = tweet.getLikes().contains(userId);
            like.setImageDrawable(ContextCompat.getDrawable(context, isLiked ? R.drawable.like : R.drawable.like_inactive));

            List<String> userIds = tweet.getUserIds();
            if (userIds != null && !userIds.isEmpty()) {
                String firstUserId = userIds.get(0);
                if (firstUserId.equals(userId)) {
                    retweet.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.original));
                    retweet.setClickable(false);
                } else if (userIds.contains(userId)) {
                    retweet.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.retweet));
                } else {
                    retweet.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.retweet_inactive));
                }
            }
        }
    }
}
