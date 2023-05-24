package com.example.twitapp.util;

import android.content.Context;
import android.widget.ImageView;

import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.twitapp.R;

import java.text.DateFormat;
import java.util.Date;

public class ImageUtil {

    public static void loadUrl(ImageView imageView, String url, int errorDrawable) {
        Context context = imageView.getContext();
        if (context != null) {
            RequestOptions options = new RequestOptions()
                    .placeholder(progressDrawable(context))
                    .error(errorDrawable);
            Glide.with(context.getApplicationContext())
                    .load(url)
                    .apply(options)
                    .into(imageView);
        }
    }

    private static CircularProgressDrawable progressDrawable(Context context) {
        CircularProgressDrawable drawable = new CircularProgressDrawable(context);
        drawable.setStrokeWidth(5f);
        drawable.setCenterRadius(30f);
        drawable.start();
        return drawable;
    }

    public static String getDate(Long timestamp) {
        if (timestamp != null) {
            DateFormat df = DateFormat.getDateInstance();
            return df.format(new Date(timestamp));
        }
        return "Unknown";
    }
}
