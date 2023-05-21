package com.example.twitapp.activities;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.twitapp.fragments.HomeFragment;
import com.example.twitapp.fragments.MyActivityFragment;
import com.example.twitapp.fragments.SearchFragment;

public class SectionsPagerAdapter extends FragmentPagerAdapter {
    private HomeFragment homeFragment;
    private SearchFragment searchFragment;
    private MyActivityFragment myActivityFragment;

    public SectionsPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
        homeFragment = new HomeFragment();
        searchFragment = new SearchFragment();
        myActivityFragment = new MyActivityFragment();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return homeFragment;
            case 1:
                return searchFragment;
            case 2:
                return myActivityFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
