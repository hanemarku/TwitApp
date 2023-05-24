package com.example.twitapp.activities;

import static com.example.twitapp.util.Constants.DATA_IMAGES;
import static com.example.twitapp.util.Constants.DATA_USERS;
import static com.example.twitapp.util.Constants.DATA_USER_EMAIL;
import static com.example.twitapp.util.Constants.DATA_USER_IMAGE_URL;
import static com.example.twitapp.util.Constants.DATA_USER_USERNAME;
import static com.example.twitapp.util.Constants.REQUEST_CODE_PHOTO;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.twitapp.R;
import com.example.twitapp.util.Constants;
import com.example.twitapp.util.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.example.twitapp.util.ImageUtil;


import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseUser user;
    private Button button;
    private FirebaseFirestore firebaseDB;
    private String userId;
    private LinearLayout profileProgressLayout;
    private EditText emailET;
    private EditText usernameET;
    private StorageReference firebaseStorage;
    private String imageUrl = null;
    private ImageView photoIV;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        auth = FirebaseAuth.getInstance();
        firebaseDB = FirebaseFirestore.getInstance();
        userId = auth.getCurrentUser().getUid();
        Log.i("userID", userId);
        profileProgressLayout = findViewById(R.id.profileProgressLayout);

        emailET = findViewById(R.id.emailET);
        usernameET = findViewById(R.id.usernameET);
        photoIV = findViewById(R.id.photoIV);

        firebaseStorage = FirebaseStorage.getInstance().getReference();

        if (userId == null) {
            finish();
        }


        button = findViewById(R.id.signoutButton);
        user = auth.getCurrentUser();
        if(user == null){
            Intent intenet = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intenet);
            finish();
        }
        else{
            Log.i("test", "test");
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intenet = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intenet);
                finish();
            }
        });

        findViewById(R.id.profileProgressLayout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

//        profileProgressLayout.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return true;
//            }
//        });

//        photoIV.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(Intent.ACTION_PICK);
//                intent.setType("image/*");
//                startActivityForResult(intent, REQUEST_CODE_PHOTO);
//            }
//        });

//        photoIV.setOnClickListener(v -> {
//            Intent intent = new Intent(Intent.ACTION_PICK);
//            intent.setType("image/*");
//            launcher.launch(intent);
//
//        });
        findViewById(R.id.photoIV).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                startActivityForResult(intent, Constants.REQUEST_CODE_PHOTO);
            }
        });


        populateInfo();

    }

    private void populateInfo() {
        profileProgressLayout.setVisibility(View.VISIBLE);
        firebaseDB.collection(DATA_USERS).document(userId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User user = documentSnapshot.toObject(User.class);
                        usernameET.setText(user != null ? user.getUsername() : "", TextView.BufferType.EDITABLE);
                        emailET.setText(user != null ? user.getEmail() : "", TextView.BufferType.EDITABLE);
                        imageUrl = user != null ? user.getImageUrl() : null;
                        photoIV = findViewById(R.id.photoIV);
                        if (imageUrl != null) {
                            ImageUtil.loadUrl(photoIV, imageUrl, R.drawable.logo);
                        }
                        profileProgressLayout.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        finish();
                    }
                });
    }

    private ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                }
            }
    );

    public void onApply(View v) {
        profileProgressLayout.setVisibility(View.VISIBLE);
        String username = usernameET.getText().toString();
        Log.i("username", username);
        String email = emailET.getText().toString();
        HashMap<String, Object> map = new HashMap<>();
        map.put(DATA_USER_USERNAME, username);
        map.put(DATA_USER_EMAIL, email);

        firebaseDB.collection(DATA_USERS).document(userId)
                .update(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ProfileActivity.this, "Update successful", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        Toast.makeText(ProfileActivity.this, "Update failed. Please try again.", Toast.LENGTH_SHORT).show();
                        profileProgressLayout.setVisibility(View.GONE);
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_PHOTO) {
             storeImage(data != null ? data.getData() : null);
        }
    }

    private void storeImage(Uri imageUri) {
        if (imageUri != null) {
            Toast.makeText(this, "Uploading...", Toast.LENGTH_SHORT).show();
            findViewById(R.id.profileProgressLayout).setVisibility(View.VISIBLE);


            StorageReference filePath = firebaseStorage.child(DATA_IMAGES).child(userId);
            filePath.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        filePath.getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    String url = uri.toString();
                                    firebaseDB.collection(DATA_USERS).document(userId)
                                            .update(DATA_USER_IMAGE_URL, url)
                                            .addOnSuccessListener(aVoid -> {
                                                imageUrl = url;
                                                ImageView photoIV = findViewById(R.id.photoIV);
                                                ImageUtil.loadUrl(photoIV, imageUrl, R.drawable.logo);
//                                                ImageUtil.loadUrl(imageUrl, R.drawable.logo);
                                                findViewById(R.id.profileProgressLayout).setVisibility(View.GONE);
                                            });
//                                    profileProgressLayout.setVisibility(View.GONE);
                                })
                                .addOnFailureListener(e -> onUploadFailure());
                    })
                    .addOnFailureListener(e -> onUploadFailure());
        }
    }

    private void onUploadFailure() {
        Toast.makeText(this, "Image upload failed. Please try again later.", Toast.LENGTH_SHORT).show();
        profileProgressLayout.setVisibility(View.GONE);
    }




    public static Intent newIntent(Context context) {
        return new Intent(context, ProfileActivity.class);
    }
}