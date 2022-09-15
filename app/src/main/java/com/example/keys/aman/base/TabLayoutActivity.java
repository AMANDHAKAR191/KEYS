package com.example.keys.aman.base;


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
import com.example.keys.aman.SplashActivity;
import com.example.keys.aman.home.HomeFragment;
import com.example.keys.aman.home.PasswordGeneratorActivity;
import com.example.keys.aman.home.addpassword.AddPasswordDataActivity;
import com.example.keys.aman.notes.AddNotesActivity;
import com.example.keys.aman.notes.BiometricActivity;
import com.example.keys.aman.notes.NotesFragment;
import com.example.keys.aman.notes.PinLockActivity;
import com.example.keys.aman.service.MyService;
import com.example.keys.aman.settings.SettingFragment;
import com.example.keys.aman.signin_login.LogInActivity;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

public class TabLayoutActivity extends AppCompatActivity {

    //objects
    private TabLayout tabLayout;
    ImageView img_keys_icon;
    TextView tv_title;
    ExtendedFloatingActionButton exFABtn, exFABtn_notes;
    FloatingActionButton fabAddPassword, fabPasswordGenrator, fabAddNote;
    TextView tvAddPassword, tvPasswordGenrator, tvShowPersonalInfo;
    LinearLayout llFab, llToolBar;
    private SharedPreferences sharedPreferences;
    LogInActivity logInActivity = new LogInActivity();

    //variables
    int selectedTab = 0;
    Boolean isAllFabsVisible;
    private int clickCounter = 0;
    public static int pauseCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_layout);
        startService(new Intent(this, MyService.class));
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        sharedPreferences = getSharedPreferences(logInActivity.getSHARED_PREF_ALL_DATA(), MODE_PRIVATE);

        SplashActivity.isForeground = false;

        tabLayout = findViewById(R.id.tablayout);
        ViewPager viewPager = findViewById(R.id.viewpager);
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
                Intent intent = new Intent(getApplicationContext(), AddPasswordDataActivity.class);
                intent.putExtra(logInActivity.getREQUEST_CODE_NAME(), "HomeActivity");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_up);
            }
        });
        fabPasswordGenrator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SplashActivity.isForeground = true;
                Intent intent = new Intent(getApplicationContext(), PasswordGeneratorActivity.class);
                intent.putExtra(logInActivity.getREQUEST_CODE_NAME(), "HomeActivity");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_up);
            }
        });
        fabAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SplashActivity.isForeground = true;
                Intent intent = new Intent(getApplicationContext(), AddNotesActivity.class);
                intent.putExtra(logInActivity.getREQUEST_CODE_NAME(), "HomeActivity");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_up);
            }
        });
        exFABtn_notes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SplashActivity.isForeground = true;
                Intent intent = new Intent(getApplicationContext(), AddNotesActivity.class);
                intent.putExtra(logInActivity.getREQUEST_CODE_NAME(), "notesActivity");
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_up);
            }
        });


        ViewPagerAdaptor viewPagerAdaptor = new ViewPagerAdaptor(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPagerAdaptor.addFragment(new HomeFragment(TabLayoutActivity.this, TabLayoutActivity.this));
        viewPagerAdaptor.addFragment(new NotesFragment(TabLayoutActivity.this, TabLayoutActivity.this));
        viewPagerAdaptor.addFragment(new SettingFragment(TabLayoutActivity.this, TabLayoutActivity.this));
        viewPager.setAdapter(viewPagerAdaptor);

        Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(R.drawable.home);
        Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.notes);
        Objects.requireNonNull(tabLayout.getTabAt(2)).setIcon(R.drawable.settings);
        Objects.requireNonNull(tabLayout.getTabAt(0)).setText("Home");
        tabLayout.setUnboundedRipple(true);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                switch (position) {
                    case 0:
                        selectedTab = 1;
                        exFABtn.setVisibility(View.VISIBLE);
                        Objects.requireNonNull(tabLayout.getTabAt(position)).setText("Home");
                        tv_title.setText("HOME");
                        break;
                    case 1:
                        selectedTab = 2;
                        exFABtn_notes.setVisibility(View.VISIBLE);
                        Objects.requireNonNull(tabLayout.getTabAt(position)).setText("Notes");
                        tv_title.setText("NOTES");
                        break;
                    case 2:
                        selectedTab = 3;
                        llToolBar.setVisibility(View.GONE);
                        Objects.requireNonNull(tabLayout.getTabAt(position)).setText("Setting");
                        tv_title.setText("SETTING");
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                Objects.requireNonNull(tabLayout.getTabAt(position)).setText("");

                switch (position) {
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
                if (selectedTab == 2) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            clickCounter = 0;
                        }
                    }, 1000);
                    if (clickCounter == 1) {
                        boolean isPinSet = sharedPreferences.getBoolean(logInActivity.getIS_PIN_SET(), false);
                        if (isPinSet) {
                            SplashActivity.isForeground = true;
                            Intent intent3 = new Intent(getApplicationContext(), PinLockActivity.class);
                            intent3.putExtra(logInActivity.getREQUEST_CODE_NAME(), "notesActivity");
                            intent3.putExtra("title", "Enter Pin");
                            startActivity(intent3);
                            clickCounter = 0;
                        } else {
                            Toast.makeText(getApplicationContext(), "please set pin", Toast.LENGTH_SHORT).show();
                        }

                    }
                    clickCounter = clickCounter + 1;
                }

            }
        });

        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.primary_dark));

        setFabVisibility();

    }


    @Override
    protected void onStart() {
        super.onStart();
        if (SplashActivity.isBackground) {
            Intent intent = new Intent(TabLayoutActivity.this, BiometricActivity.class);
            intent.putExtra(logInActivity.getREQUEST_CODE_NAME(), "LockBackGroundApp");
            startActivity(intent);
        }
        if (SplashActivity.isForeground) {
            SplashActivity.isForeground = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!SplashActivity.isForeground) {
            SplashActivity.isBackground = true;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SplashActivity.isForeground = true;
    }

    private void setFabVisibility() {
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
}