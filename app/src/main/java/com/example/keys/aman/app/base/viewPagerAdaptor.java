package com.example.keys.aman.app.base;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class viewPagerAdaptor extends FragmentPagerAdapter {


    private final ArrayList<Fragment> fragmentarraylist = new ArrayList<>();
    private final ArrayList<String> fragmenttitles = new ArrayList<>();

    public viewPagerAdaptor(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentarraylist.get(position);
    }

    @Override
    public int getCount() {
        return fragmentarraylist.size();
    }

    public void addFragment(Fragment fragment, String title){
        fragmentarraylist.add(fragment);
        fragmenttitles.add(title);
    }
}
