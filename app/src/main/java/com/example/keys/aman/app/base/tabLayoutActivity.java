package com.example.keys.aman.app.base;

import static com.example.keys.aman.app.signin_login.LogInActivity.REQUEST_CODE_NAME;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.keys.R;
import com.example.keys.aman.app.SplashActivity;
import com.example.keys.aman.app.home.HomeActivity;
import com.example.keys.aman.app.home.PassGenActivity;
import com.example.keys.aman.app.home.addpassword.addPasswordData;
import com.example.keys.aman.app.notes.BiometricActivity;
import com.example.keys.aman.app.notes.addNotesActivity;
import com.example.keys.aman.app.notes.notesActivity;
import com.example.keys.aman.app.notes.pinLockFragment;
import com.example.keys.aman.app.settings.SettingActivity;
import com.example.keys.aman.app.signin_login.LogInActivity;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

public class tabLayoutActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    ImageView img_keys_icon;
    TextView tv_title;
    ExtendedFloatingActionButton exFABtn, exFABtn_notes;
    FloatingActionButton fabAddPassword, fabPasswordGenrator, fabAddNote;
    TextView tvAddPassword, tvPasswordGenrator, tvShowPersonalInfo;
    LinearLayout llFab;
    LinearLayout llToolBar;
    int selectedTab = 0;
    Boolean isAllFabsVisible;
    private int click_counter = 0;
    private SharedPreferences sharedPreferences;
    public static int pauseCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_layout);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        sharedPreferences = getSharedPreferences(LogInActivity.SHARED_PREF_ALL_DATA, MODE_PRIVATE);

        SplashActivity.isForeground = false;

        tabLayout = findViewById(R.id.tablayout);
        viewPager = findViewById(R.id.viewpager);
        img_keys_icon = findViewById(R.id.img_keys_icon);
        tv_title = findViewById(R.id.tv_title);
        exFABtn = findViewById(R.id.ExtendedFloatingActionButton);
        exFABtn_notes = findViewById(R.id.ExtendedFloatingActionButton_notes);
        fabAddPassword = findViewById(R.id.add_password_fab);
        tvAddPassword = findViewById(R.id.tv_add_password);
        fabPasswordGenrator = findViewById(R.id.password_gen_fab);
        tvPasswordGenrator = findViewById(R.id.tv_pass_gen);
        fabAddNote = findViewById(R.id.add_note_fab);
        tvShowPersonalInfo = findViewById(R.id.tv_personal_info);
        llFab = findViewById(R.id.ll_fab);
        tabLayout.setupWithViewPager(viewPager);
        llToolBar = findViewById(R.id.ll_toolbar);

        llFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabAddPassword.hide();
                fabPasswordGenrator.hide();
                fabAddNote.hide();
                tvAddPassword.setVisibility(View.GONE);
                tvPasswordGenrator.setVisibility(View.GONE);
                tvShowPersonalInfo.setVisibility(View.GONE);
                exFABtn.setIconResource(R.drawable.add);
                exFABtn.extend();
                llFab.setBackground(getDrawable(R.drawable.fully_transparent_background));
                llFab.setVisibility(View.INVISIBLE);
                isAllFabsVisible = false;
            }
        });
        fabAddPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SplashActivity.isForeground = true;
                Intent intent = new Intent(getApplicationContext(), addPasswordData.class);
                intent.putExtra(LogInActivity.REQUEST_CODE_NAME, "HomeActivity");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_up);
            }
        });
        fabPasswordGenrator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SplashActivity.isForeground = true;
                Intent intent = new Intent(getApplicationContext(), PassGenActivity.class);
                intent.putExtra(LogInActivity.REQUEST_CODE_NAME, "HomeActivity");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_up);
            }
        });
        fabAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SplashActivity.isForeground = true;
                Intent intent = new Intent(getApplicationContext(), addNotesActivity.class);
                intent.putExtra(LogInActivity.REQUEST_CODE_NAME, "HomeActivity");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_up);
            }
        });
        exFABtn_notes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SplashActivity.isForeground = true;
                Intent intent = new Intent(getApplicationContext(), addNotesActivity.class);
                intent.putExtra(LogInActivity.REQUEST_CODE_NAME,"notesActivity");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_up);
            }
        });


        viewPagerAdaptor viewPagerAdaptor = new viewPagerAdaptor(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPagerAdaptor.addFragment(new HomeActivity(tabLayoutActivity.this,tabLayoutActivity.this));
        viewPagerAdaptor.addFragment(new notesActivity(tabLayoutActivity.this,tabLayoutActivity.this));
        viewPagerAdaptor.addFragment(new SettingActivity(tabLayoutActivity.this,tabLayoutActivity.this));
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
                        exFABtn.setVisibility(View.VISIBLE);
                        tabLayout.getTabAt(position).setText("Home");
                        tv_title.setText("HOME");
                        break;
                    case 1:
                        selectedTab = 2;
                        exFABtn_notes.setVisibility(View.VISIBLE);
                        tabLayout.getTabAt(position).setText("Notes");
                        tv_title.setText("NOTES");
                        break;
                    case 2:
                        selectedTab = 3;
                        llToolBar.setVisibility(View.GONE);
                        tabLayout.getTabAt(position).setText("Setting");
                        tv_title.setText("SETTING");
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                tabLayout.getTabAt(position).setText("");

                switch (position){
                    case 0:
                        exFABtn.setVisibility(View.INVISIBLE);
                        break;
                    case 1:
                        exFABtn_notes.setVisibility(View.INVISIBLE);
                        break;
                    case 2:
                        llToolBar.setVisibility(View.VISIBLE);
                        break;
                }
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
                            SplashActivity.isForeground = true;
                            Intent intent3 = new Intent(getApplicationContext(), pinLockFragment.class);
                            intent3.putExtra(REQUEST_CODE_NAME,"notesActivity");
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

        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.primary_dark));

        setfabVisblity();

    }

    private void setfabVisblity() {
        isAllFabsVisible = false;
        fabAddPassword.setVisibility(View.GONE);
        tvAddPassword.setVisibility(View.GONE);
        fabPasswordGenrator.setVisibility(View.GONE);
        tvPasswordGenrator.setVisibility(View.GONE);
        fabAddNote.setVisibility(View.GONE);
        tvShowPersonalInfo.setVisibility(View.GONE);
        exFABtn.extend();
        exFABtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!isAllFabsVisible) {
                            fabAddPassword.show();
                            fabPasswordGenrator.show();
                            fabAddNote.show();
                            tvAddPassword.setVisibility(View.VISIBLE);
                            tvPasswordGenrator.setVisibility(View.VISIBLE);
                            tvShowPersonalInfo.setVisibility(View.VISIBLE);
                            exFABtn.shrink();
                            exFABtn.setIconResource(R.drawable.close);
                            exFABtn.setTranslationZ(1000);
                            llFab.setBackground(getDrawable(R.drawable.transparent_background));
                            llFab.setVisibility(View.VISIBLE);
                            isAllFabsVisible = true;
                        } else {
                            fabAddPassword.hide();
                            fabPasswordGenrator.hide();
                            fabAddNote.hide();
                            tvAddPassword.setVisibility(View.GONE);
                            tvPasswordGenrator.setVisibility(View.GONE);
                            tvShowPersonalInfo.setVisibility(View.GONE);
                            exFABtn.setIconResource(R.drawable.add);
                            exFABtn.extend();
                            llFab.setVisibility(View.INVISIBLE);
                            llFab.setBackground(getDrawable(R.drawable.transparent_background));
                            isAllFabsVisible = false;
                        }

                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (SplashActivity.isBackground){
            Intent intent = new Intent(tabLayoutActivity.this, BiometricActivity.class);
            intent.putExtra(REQUEST_CODE_NAME, "LockBackGroundApp");
            startActivity(intent);
        }
        if (SplashActivity.isForeground){
            SplashActivity.isForeground = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!SplashActivity.isForeground){
            SplashActivity.isBackground = true;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SplashActivity.isForeground = true;
    }
}