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
import com.example.keys.aman.authentication.AppLockCounterClass;
import com.example.keys.aman.authentication.PinLockActivity;
import com.example.keys.aman.home.HomeFragment;
import com.example.keys.aman.home.PasswordGeneratorActivity;
import com.example.keys.aman.home.addpassword.AddPasswordDataActivity;
import com.example.keys.aman.messages.AddContactEmailDialogFragment;
import com.example.keys.aman.messages.MessagesActivity;
import com.example.keys.aman.notes.NotesFragment;
import com.example.keys.aman.notes.addnote.AddNotesActivity;
import com.example.keys.aman.settings.SettingFragment;
import com.example.keys.aman.signin_login.LogInActivity;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

public class TabLayoutActivity extends AppCompatActivity {

    private static final String TAG = "TabLayoutActivity";
    public final String LOCK_APP_OPTIONS = "lock_app";
    //objects
    private TabLayout tabLayout;
    TextView tvTitle, tvAddPassword, tvPasswordGenerator, tvAddNote;
    LinearLayout llFab, llToolbar;
    ImageView imgKeysIcon;
    ExtendedFloatingActionButton exFABtn, exFABtn_notes;
    FloatingActionButton fabAddPassword, fabPasswordGenerator, fabAddNote, fabAddContactEmail;
    private SharedPreferences sharedPreferences;
    LogInActivity logInActivity = new LogInActivity();
    PinLockActivity pinLockActivity = new PinLockActivity();
    AppLockCounterClass appLockCounterClass = new AppLockCounterClass(TabLayoutActivity.this, TabLayoutActivity.this);

    public String getLOCK_APP_OPTIONS() {
        return LOCK_APP_OPTIONS;
    }


    //variables
    int selectedTab = 0;

    Boolean isAllFabsVisible;
    private int clickCounter = 0;
    public static int pauseCounter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_layout);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        sharedPreferences = getSharedPreferences(logInActivity.getSHARED_PREF_ALL_DATA(), MODE_PRIVATE);

        SplashActivity.isForeground = false;

        tabLayout = findViewById(R.id.tablayout);
        ViewPager viewPager = findViewById(R.id.viewpager);
        imgKeysIcon = findViewById(R.id.img_keys_icon);
        tvTitle = findViewById(R.id.tv_title);
        exFABtn = findViewById(R.id.ex_fab);
        exFABtn_notes = findViewById(R.id.ex_fab_notes);
        fabAddPassword = findViewById(R.id.add_password_fab);
        tvAddPassword = findViewById(R.id.tv_add_password);
        fabPasswordGenerator = findViewById(R.id.password_gen_fab);
        fabAddNote = findViewById(R.id.add_contact_email_fab);
        fabAddContactEmail = findViewById(R.id.add_contact_email_fab);
        tvPasswordGenerator = findViewById(R.id.tv_pass_gen);
        fabAddNote = findViewById(R.id.add_note_fab);
        tvAddNote = findViewById(R.id.tv_add_note);
        llFab = findViewById(R.id.ll_fab);
        tabLayout.setupWithViewPager(viewPager);
        llToolbar = findViewById(R.id.ll_toolbar);

        llFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fabAddPassword.hide();
                fabPasswordGenerator.hide();
                fabAddNote.hide();
                tvAddPassword.setVisibility(View.GONE);
                tvPasswordGenerator.setVisibility(View.GONE);
                tvAddNote.setVisibility(View.GONE);
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
        fabPasswordGenerator.setOnClickListener(new View.OnClickListener() {
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
        fabAddContactEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddContactEmailDialogFragment dialogFragment = new AddContactEmailDialogFragment();
                dialogFragment.show(getSupportFragmentManager(), "AddContactEmailDialogFragment");
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
        viewPagerAdaptor.addFragment(new MessagesActivity(TabLayoutActivity.this, TabLayoutActivity.this));
        viewPagerAdaptor.addFragment(new NotesFragment(TabLayoutActivity.this, TabLayoutActivity.this));
        viewPagerAdaptor.addFragment(new SettingFragment(TabLayoutActivity.this, TabLayoutActivity.this));
        viewPager.setAdapter(viewPagerAdaptor);

        Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(R.drawable.home);
        Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.chat);
        Objects.requireNonNull(tabLayout.getTabAt(2)).setIcon(R.drawable.notes);
        Objects.requireNonNull(tabLayout.getTabAt(3)).setIcon(R.drawable.settings);
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
                        tvTitle.setText("HOME");
                        break;
                    case 1:
                        selectedTab = 2;
                        llToolbar.setVisibility(View.VISIBLE);
                        fabAddContactEmail.setVisibility(View.VISIBLE);
                        Objects.requireNonNull(tabLayout.getTabAt(position)).setText("Chats");
                        tvTitle.setText("Chats");
                        break;
                    case 2:
                        selectedTab = 3;
                        exFABtn_notes.setVisibility(View.VISIBLE);
                        Objects.requireNonNull(tabLayout.getTabAt(position)).setText("Notes");
                        tvTitle.setText("NOTES");
                        break;
                    case 3:
                        selectedTab = 4;
                        llToolbar.setVisibility(View.GONE);
                        Objects.requireNonNull(tabLayout.getTabAt(position)).setText("Setting");
                        tvTitle.setText("SETTING");
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
                        fabAddContactEmail.setVisibility(View.INVISIBLE);
                        break;
                    case 2:
                        exFABtn_notes.setVisibility(View.INVISIBLE);
                        break;
                    case 3:
                        llToolbar.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        imgKeysIcon.setOnClickListener(new View.OnClickListener() {
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
                        boolean isPinSet = sharedPreferences.getBoolean(pinLockActivity.getIS_PIN_SET(), false);
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
        appLockCounterClass.onStartOperation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        appLockCounterClass.checkedItem = sharedPreferences.getInt(LOCK_APP_OPTIONS, 0);
        appLockCounterClass.onPauseOperation();
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
        fabPasswordGenerator.setVisibility(View.GONE);
        tvPasswordGenerator.setVisibility(View.GONE);
        fabAddNote.setVisibility(View.GONE);
        tvAddNote.setVisibility(View.GONE);
        exFABtn.extend();
        exFABtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!isAllFabsVisible) {
                            fabAddPassword.show();
                            fabPasswordGenerator.show();
                            fabAddNote.show();
                            tvAddPassword.setVisibility(View.VISIBLE);
                            tvPasswordGenerator.setVisibility(View.VISIBLE);
                            tvAddNote.setVisibility(View.VISIBLE);
                            exFABtn.shrink();
                            exFABtn.setIconResource(R.drawable.close);
                            exFABtn.setTranslationZ(1000);
                            llFab.setBackground(getDrawable(R.drawable.transparent_background));
                            llFab.setVisibility(View.VISIBLE);
                            isAllFabsVisible = true;
                        } else {
                            fabAddPassword.hide();
                            fabPasswordGenerator.hide();
                            fabAddNote.hide();
                            tvAddPassword.setVisibility(View.GONE);
                            tvPasswordGenerator.setVisibility(View.GONE);
                            tvAddNote.setVisibility(View.GONE);
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