package com.example.twitapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.twitapp.R;
import com.example.twitapp.util.models.Tweet;

import java.util.List;

public class TweetAdapter extends RecyclerView.Adapter<TweetAdapter.ViewHolder> {
    private Context context;
    private List<Tweet> tweetList;

    public TweetAdapter(Context context, List<Tweet> tweetList) {
        this.context = context;
        this.tweetList = tweetList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tweet, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tweet tweet = tweetList.get(position);

        holder.usernameTextView.setText(tweet.getUsername());
        holder.tweetTextView.setText(tweet.getText());
    }

    @Override
    public int getItemCount() {
        return tweetList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView usernameTextView;
        public TextView tweetTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
//            usernameTextView = itemView.findViewById(R.id.usernameTextView);
//            tweetTextView = itemView.findViewById(R.id.tweetTextView);
            // Initialize other views (e.g., image, timestamp, etc.)
        }
    }
}

