package com.example.twitapp.activities;

import static com.example.twitapp.util.Constants.DATA_USERS;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.twitapp.R;
import com.example.twitapp.databinding.ActivityHomeBinding;
import com.example.twitapp.fragments.HomePageFragment;
import com.example.twitapp.fragments.MyActivityFragment;
import com.example.twitapp.fragments.SearchFragment;
import com.example.twitapp.util.ImageUtil;
import com.example.twitapp.util.models.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class HomeActivity extends AppCompatActivity {

    private ImageView logo;
    private FirebaseFirestore firebaseDB;
    private LinearLayout homeProgressLayout;
    private String userId;
    private FirebaseAuth auth;
    ActivityHomeBinding binding;
    private FloatingActionButton fab;
    private User user;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_home);
        auth = FirebaseAuth.getInstance();
        firebaseDB = FirebaseFirestore.getInstance();
        userId = auth.getCurrentUser().getUid();

        homeProgressLayout = findViewById(R.id.homeProgressLayout);
        fab = findViewById(R.id.fab);
        Log.i("fab", String.valueOf(fab));

        logo = findViewById(R.id.logo);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new SearchFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            switch (item.getItemId()){
                case R.id.tabHome:
                    replaceFragment(new HomePageFragment());
                    break;
                case R.id.tabSearch:
                    replaceFragment(new SearchFragment());
                    break;
                case R.id.tabActivity:
                    replaceFragment(new MyActivityFragment());
                    break;
            }

            return true;
        });

        binding.logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });


        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("user", String.valueOf(user));
                if (user != null) {
                    Log.i("useri", String.valueOf(user));
                    String username = user.getUsername();
                    Log.i("username", username);
                    if (username != null) {
                        startActivity(TweetActivity.newIntent(HomeActivity.this, userId, user.getUsername()));
                    }
                }
            }
        });

        binding.homeProgressLayout.setOnTouchListener((v, event) -> true);


//        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
//        if (currentUser == null) {
//            startActivity(LoginActivity.newIntent(this));
//            finish();
//        } else {
//            userId = currentUser.getUid();
//            populate();
//        }

    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (userId == null) {
            startActivity(LoginActivity.newIntent(this));
            finish();
        } else {
            populate();
        }
    }


    public static Intent newIntent(Context context) {
        return new Intent(context, HomeActivity.class);
    }

    private void populate() {
        logo = findViewById(R.id.logo);
        homeProgressLayout = findViewById(R.id.homeProgressLayout);
        homeProgressLayout.setVisibility(View.VISIBLE);
        firebaseDB.collection(DATA_USERS).document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    homeProgressLayout.setVisibility(View.GONE);
                    user = documentSnapshot.toObject(User.class);
                    if (user != null && user.getImageUrl() != null) {
                        ImageUtil.loadUrl(logo, user.getImageUrl(), R.drawable.logo);
                    }
//                    updateFragmentUser();
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                    finish();
                });
    }


    public void setUserData(String userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }

}