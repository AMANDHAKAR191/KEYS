package com.example.keys.aman.app.base;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.keys.R;
import com.example.keys.aman.app.home.HomeActivity;
import com.example.keys.aman.app.notes.notesActivity;
import com.example.keys.aman.app.notes.pinLockFragment;
import com.example.keys.aman.app.settings.SettingActivity;
import com.example.keys.aman.app.signin_login.LogInActivity;
import com.google.android.material.tabs.TabLayout;

public class tabLayoutActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    ImageView img_keys_icon;
    TextView tv_title;
    int selectedTab = 0;

    private int click_counter = 0;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_layout);
        sharedPreferences = getSharedPreferences(LogInActivity.SHARED_PREF_ALL_DATA, MODE_PRIVATE);

        tabLayout = findViewById(R.id.tablayout);
        viewPager = findViewById(R.id.viewpager);
        img_keys_icon = findViewById(R.id.img_keys_icon);
        tv_title = findViewById(R.id.tv_title);

        tabLayout.setupWithViewPager(viewPager);





        viewPagerAdaptor viewPagerAdaptor = new viewPagerAdaptor(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPagerAdaptor.addFragment(new HomeActivity(getApplicationContext(),tabLayoutActivity.this));
        viewPagerAdaptor.addFragment(new notesActivity(getApplicationContext(),tabLayoutActivity.this));
        viewPagerAdaptor.addFragment(new SettingActivity(getApplicationContext(),tabLayoutActivity.this));
        viewPager.setAdapter(viewPagerAdaptor);

        tabLayout.getTabAt(0).setIcon(R.drawable.home);
        tabLayout.getTabAt(1).setIcon(R.drawable.notes);
        tabLayout.getTabAt(2).setIcon(R.drawable.settings);
        tabLayout.getTabAt(0).setText("Home");
        tabLayout.setUnboundedRipple(true);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                switch (position){
                    case 0:
                        selectedTab = 1;
                        tabLayout.getTabAt(position).setText("Home");
                        tv_title.setText("HOME");
                        break;
                    case 1:
                        selectedTab = 2;
                        tabLayout.getTabAt(position).setText("Notes");
                        tv_title.setText("NOTES");
                        break;
                    case 2:
                        selectedTab = 3;
                        tabLayout.getTabAt(position).setText("Setting");
                        tv_title.setText("SETTING");
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                tabLayout.getTabAt(position).setText("");
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        img_keys_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedTab == 2){
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            click_counter = 0;
                        }
                    },1000);
                    if (click_counter == 1){
                        boolean ispin_set =  sharedPreferences.getBoolean(LogInActivity.IS_PIN_SET,false);
                        if (ispin_set){
                            Intent intent3 = new Intent(getApplicationContext(), pinLockFragment.class);
                            intent3.putExtra(LogInActivity.REQUEST_CODE_NAME,"notesActivity");
                            intent3.putExtra("title","Enter Pin");
                            startActivity(intent3);
                            click_counter = 0;
                        }else {
                            Toast.makeText(getApplicationContext(), "please set pin", Toast.LENGTH_SHORT).show();
                        }

                    }
                    click_counter = click_counter + 1;
                }

            }
        });

        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.button_back));

    }

}