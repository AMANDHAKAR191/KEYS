package com.example.keys.aman.app.base;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class viewPagerAdaptor extends FragmentPagerAdapter {


    private final ArrayList<Fragment> fragmentarraylist = new ArrayList<>();
//    Activity activity;
//    Context context;

//    public viewPagerAdaptor(@NonNull FragmentActivity fragmentActivity,
//                            Activity activity, Context context) {
//        super(fragmentActivity);
//        this.activity = activity;
//        this.context = context;
//    }


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

    public void addFragment(Fragment fragment){
        fragmentarraylist.add(fragment);
    }

//    @NonNull
//    @Override
//    public Fragment createFragment(int position) {
//        switch (position){
//            case 0:
//                return new HomeActivity(context,activity);
//            case 1:
//                return new notesActivity(context,activity);
//            case 2:
//                return new SettingActivity(context,activity);
//        }
//        return new HomeActivity(context,activity);
//    }
//
//    @Override
//    public int getItemCount() {
//        return fragmentarraylist.size();
//    }
}
