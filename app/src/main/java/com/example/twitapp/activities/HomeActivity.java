package com.example.twitapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.twitapp.R;
import com.example.twitapp.databinding.ActivityHomeBinding;
import com.example.twitapp.fragments.HomeFragment;
import com.example.twitapp.fragments.HomePageFragment;
import com.example.twitapp.fragments.MyActivityFragment;
import com.example.twitapp.fragments.SearchFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

public class HomeActivity extends AppCompatActivity {

//    private FirebaseAuth auth;
//    private Button button;
//    private TextView textView;
//    private FirebaseUser user;

    ActivityHomeBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_home);

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



//
//        auth = FirebaseAuth.getInstance();
////        button = findViewById(R.id.logoutButton);
//////        textView = findViewById(R.id.user_details);
//        user = auth.getCurrentUser();
//        if(user == null){
//            Intent intenet = new Intent(getApplicationContext(), LoginActivity.class);
//            startActivity(intenet);
//            finish();
//        }
//        else{
//            Log.i("test", "test");
////            textView.setText(user.getEmail());
//        }
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                FirebaseAuth.getInstance().signOut();
//                Intent intenet = new Intent(getApplicationContext(), LoginActivity.class);
//                startActivity(intenet);
//                finish();
//            }
//        });

    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, HomeActivity.class);
    }


}