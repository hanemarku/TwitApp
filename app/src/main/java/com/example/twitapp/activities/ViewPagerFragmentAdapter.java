package com.example.twitapp.activities;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.twitapp.fragments.HomeFragment;
import com.example.twitapp.fragments.MyActivityFragment;
import com.example.twitapp.fragments.SearchFragment;

import java.util.ArrayList;

public class ViewPagerFragmentAdapter extends FragmentStateAdapter {

    private final ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
    private ArrayList<String> fragmentTitles = new ArrayList<>();



    public ViewPagerFragmentAdapter(@NonNull FragmentManager fragmentManager) {
        super(fragmentManager.getPrimaryNavigationFragment());
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position)
        {
            case 0:
                return new HomeFragment();
            case 1:
                return new SearchFragment();
            default:
                return new MyActivityFragment();
        }
    }



    public Fragment getItem(int position){
        return fragmentArrayList.get(position);
    }

    public void addFragment(Fragment fragment, String title){
        fragmentArrayList.add(fragment);
        fragmentTitles.add(title);
    }

    @Override
    public int getItemCount() {
        return fragmentArrayList.size();
    }

    public CharSequence getPageTitle(int position){
        return fragmentTitles.get(position);
    }
}
