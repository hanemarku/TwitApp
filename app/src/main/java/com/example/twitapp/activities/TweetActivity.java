package com.example.twitapp.activities;

import static com.example.twitapp.util.Constants.DATA_IMAGES;
import static com.example.twitapp.util.Constants.REQUEST_CODE_PHOTO;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.twitapp.R;
import com.example.twitapp.util.models.Tweet;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TweetActivity extends AppCompatActivity {

    private FirebaseFirestore firebaseDB;
    private StorageReference firebaseStorage;
    private String imageUrl;
    private String userId;
    private String userName;


    public static final String PARAM_USER_ID = "UserId";
    public static final String PARAM_USER_NAME = "UserName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tweet);
        firebaseDB = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance().getReference();

        if (getIntent().hasExtra(PARAM_USER_ID) && getIntent().hasExtra(PARAM_USER_NAME)) {
            userId = getIntent().getStringExtra(PARAM_USER_ID);
            userName = getIntent().getStringExtra(PARAM_USER_NAME);
        }else{
            Toast.makeText(this, "Error creating tweet", Toast.LENGTH_SHORT).show();
        }

        findViewById(R.id.tweetProgressLayout).setOnTouchListener((view, motionEvent) -> true);
    }

    public void addImage(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Image"), REQUEST_CODE_PHOTO);
    }

    public void postTweet(View view) {
        findViewById(R.id.tweetProgressLayout).setVisibility(View.VISIBLE);
        String tweetText = ((TextView) findViewById(R.id.tweetText)).getText().toString();
        if (TextUtils.isEmpty(tweetText) && TextUtils.isEmpty(imageUrl)) {
            Toast.makeText(this, "Cannot post empty tweet!", Toast.LENGTH_SHORT).show();
            findViewById(R.id.tweetProgressLayout).setVisibility(View.GONE);
            return;
        }

        final String tweetId = firebaseDB.collection("tweets").document().getId();
        ArrayList<String> hashTags = extractHashTags(tweetText);

        Tweet tweet = new Tweet(tweetId, userId, userName, tweetText, hashTags, imageUrl, new Date().getTime());
        firebaseDB.collection("tweets").document(tweetId).set(tweet)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        findViewById(R.id.tweetProgressLayout).setVisibility(View.GONE);
                        Toast.makeText(TweetActivity.this, "Tweet posted successfully", Toast.LENGTH_SHORT).show();
                        setResult(Activity.RESULT_OK);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        findViewById(R.id.tweetProgressLayout).setVisibility(View.GONE);
                        Toast.makeText(TweetActivity.this, "Failed to post tweet", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_PHOTO && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                uploadImage(selectedImageUri);
            }
        }
    }

    public void uploadImage(Uri imageUri) {
        findViewById(R.id.tweetProgressLayout).setVisibility(View.VISIBLE);
        StorageReference imageRef = firebaseStorage.child("tweets/" + userId + "_" + System.currentTimeMillis());
        imageRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        imageRef.getDownloadUrl()
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        imageUrl = uri.toString();
                                        findViewById(R.id.tweetProgressLayout).setVisibility(View.GONE);
                                        Glide.with(TweetActivity.this).load(imageUrl).into((ImageView) findViewById(R.id.tweetImage));
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        findViewById(R.id.tweetProgressLayout).setVisibility(View.GONE);
                                        Toast.makeText(TweetActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        findViewById(R.id.tweetProgressLayout).setVisibility(View.GONE);
                        Toast.makeText(TweetActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private static ArrayList<String> extractHashTags(String tweetText) {
        ArrayList<String> hashTags = new ArrayList<>();
        Pattern pattern = Pattern.compile("#\\w+");
        Matcher matcher = pattern.matcher(tweetText);
        while (matcher.find()) {
            hashTags.add(matcher.group());
        }
        return hashTags;
    }

    public static Intent newIntent(Context context, String userId, String userName) {
        Intent intent = new Intent(context, TweetActivity.class);
        intent.putExtra(PARAM_USER_ID, userId);
        intent.putExtra(PARAM_USER_NAME, userName);
        return intent;
    }

}