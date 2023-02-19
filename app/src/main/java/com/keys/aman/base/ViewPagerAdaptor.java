package com.keys.aman.base;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class ViewPagerAdaptor extends FragmentPagerAdapter {


    private final ArrayList<Fragment> fragmentarraylist = new ArrayList<>();
//    Activity activity;
//    Context context;

//    public viewPagerAdaptor(@NonNull FragmentActivity fragmentActivity,
//                            Activity activity, Context context) {
//        super(fragmentActivity);
//        this.activity = activity;
//        this.context = context;
//    }


    public ViewPagerAdaptor(@NonNull FragmentManager fm, int behavior) {
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

    public void addFragment(Fragment fragment){
        fragmentarraylist.add(fragment);
    }

}
