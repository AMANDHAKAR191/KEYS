package com.keys.aman.base;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.keys.aman.MyPreference;
import com.keys.aman.R;
import com.keys.aman.SplashActivity;
import com.keys.aman.authentication.AppLockCounterClass;
import com.keys.aman.authentication.PinLockActivity;
import com.keys.aman.data.Firebase;
import com.keys.aman.data.iFirebaseDAO;
import com.keys.aman.home.HomeFragment;
import com.keys.aman.home.PasswordGeneratorActivity;
import com.keys.aman.home.addpassword.AddPasswordActivity;
import com.keys.aman.messages.AddContactEmailDialogFragment;
import com.keys.aman.messages.MessagesFragment;
import com.keys.aman.notes.NotesFragment;
import com.keys.aman.notes.addnote.AddNotesActivity;
import com.keys.aman.settings.SettingFragment;
import com.keys.aman.signin_login.LogInActivity;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class TabLayoutActivity extends AppCompatActivity {

    private static final String TAG = "TabLayoutActivity";

    public static final String REQUEST_ID = "TabLayoutActivity";
    private TabLayout tabLayout;
    TextView tvTitle, tvTitle1;
    LinearLayout llFab, llToolbar;
    Group groupFabHome, groupFabChat, groupFabNote, groupFabAll;
    CircleImageView cimgProfile;
    ExtendedFloatingActionButton exFABtn;
    FloatingActionButton fabAddPassword, fabPasswordGenerator, fabAddNote, fabAddContactEmail;
    LogInActivity logInActivity = new LogInActivity();
    //todo 2 object calling of AppLockCounterClass
    AppLockCounterClass appLockCounterClass = new AppLockCounterClass(TabLayoutActivity.this, TabLayoutActivity.this);
    private String senderPublicUid;
    //variables
    int selectedTab = 1;
    Boolean isAllFabsVisible;
    private int clickCounter = 0;
    MyPreference myPreference;
    iFirebaseDAO iFirebaseDAO;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_layout);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        //init variables
        myPreference = MyPreference.getInstance(this);
        //todo 3 when is coming from background or foreground always isForeground false
        SplashActivity.isForeground = false;
        senderPublicUid = myPreference.getPublicUid();
        selectedTab = 1;
        clickCounter = 0;
        iFirebaseDAO = Firebase.getInstance(TabLayoutActivity.this);

        //init views
        groupFabAll = findViewById(R.id.group_fab_all);
        groupFabHome = findViewById(R.id.group_fab_home);
        groupFabChat = findViewById(R.id.group_fab_chat);
        groupFabNote = findViewById(R.id.group_fab_note);
        tabLayout = findViewById(R.id.tablayout);
        ViewPager viewPager = findViewById(R.id.viewpager);
        cimgProfile = findViewById(R.id.circular_img_profile);
        tvTitle = findViewById(R.id.tv_title_greeting);
        tvTitle1 = findViewById(R.id.tv_title_name);
        exFABtn = findViewById(R.id.ex_fab_button);
        fabAddPassword = findViewById(R.id.add_password_fab);
        fabPasswordGenerator = findViewById(R.id.password_gen_fab);
        fabAddContactEmail = findViewById(R.id.add_contact_email_fab);
        fabAddNote = findViewById(R.id.add_note_fab);
        llFab = findViewById(R.id.ll_fab_background);
        tabLayout.setupWithViewPager(viewPager);
        llToolbar = findViewById(R.id.ll_toolbar);


        llFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exFABtn.setIconResource(R.drawable.add);
                exFABtn.extend();
                llFab.setBackground(getDrawable(R.drawable.fully_transparent_background));
                llFab.setVisibility(View.INVISIBLE);
                isAllFabsVisible = false;
                switch (selectedTab) {
                    case 1:
                        groupFabHome.setVisibility(View.GONE);
                        break;
                    case 2:
                        groupFabChat.setVisibility(View.GONE);
                        break;
                    case 3:
                        groupFabNote.setVisibility(View.GONE);
                        break;
                    case 4:
                        groupFabAll.setVisibility(View.GONE);
                        break;

                }
            }
        });
        fabAddPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo 4 if app is going to another activity make isForeground = true
                SplashActivity.isForeground = true;
                Intent intent = new Intent(getApplicationContext(), AddPasswordActivity.class);
                intent.putExtra(logInActivity.REQUEST_CODE_NAME, REQUEST_ID);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_up);
            }
        });
        fabPasswordGenerator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo 4 if app is going to another activity make isForeground = true
                SplashActivity.isForeground = true;
                Intent intent = new Intent(getApplicationContext(), PasswordGeneratorActivity.class);
                intent.putExtra(logInActivity.REQUEST_CODE_NAME, REQUEST_ID);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_up);
            }
        });
        fabAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo 4 if app is going to another activity make isForeground = true
                SplashActivity.isForeground = true;
                Intent intent = new Intent(getApplicationContext(), AddNotesActivity.class);
                intent.putExtra(logInActivity.REQUEST_CODE_NAME, REQUEST_ID);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_up);
            }
        });
        fabAddContactEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddContactEmailDialogFragment dialogFragment = new AddContactEmailDialogFragment(senderPublicUid);
                dialogFragment.show(getSupportFragmentManager(),"add_chat");
            }
        });

        ViewPagerAdaptor viewPagerAdaptor = new ViewPagerAdaptor(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPagerAdaptor.addFragment(new HomeFragment(TabLayoutActivity.this, TabLayoutActivity.this));
        viewPagerAdaptor.addFragment(new MessagesFragment(TabLayoutActivity.this, TabLayoutActivity.this, savedInstanceState));
        viewPagerAdaptor.addFragment(new NotesFragment(TabLayoutActivity.this, TabLayoutActivity.this));
        viewPagerAdaptor.addFragment(new SettingFragment(TabLayoutActivity.this, TabLayoutActivity.this));
        viewPager.setAdapter(viewPagerAdaptor);

        Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(R.drawable.home_new_set);
        Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.chats_new_set);
        Objects.requireNonNull(tabLayout.getTabAt(2)).setIcon(R.drawable.notes_new_set);
        Objects.requireNonNull(tabLayout.getTabAt(3)).setIcon(R.drawable.setting_new_set);

        Objects.requireNonNull(tabLayout.getTabAt(0)).setText("Home");
        Objects.requireNonNull(tabLayout.getTabAt(1)).setText("Chats");
        Objects.requireNonNull(tabLayout.getTabAt(2)).setText("Notes");
        Objects.requireNonNull(tabLayout.getTabAt(3)).setText("Setting");

        tabLayout.setUnboundedRipple(true);


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                switch (position) {
                    case 0:
                        selectedTab = 1;
                        tvTitle.setTextSize(40);
                        tvTitle.setText("Hello 👋");
                        tvTitle1.setVisibility(View.VISIBLE);
                        tvTitle1.setText(iFirebaseDAO.getDisplayName());
                        break;
                    case 1:
                        selectedTab = 2;
                        tvTitle.setTextSize(40);
                        tvTitle.setText("Chats");
                        break;
                    case 2:
                        selectedTab = 3;
                        tvTitle.setTextSize(40);
                        tvTitle.setText("NOTES");
                        break;
                    case 3:
                        selectedTab = 4;
                        exFABtn.setVisibility(View.GONE);
                        groupFabAll.setVisibility(View.GONE);
                        tvTitle.setTextSize(40);
                        tvTitle.setText("SETTING");
                        cimgProfile.setVisibility(View.INVISIBLE);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int position = tab.getPosition();

                switch (position) {
                    case 0:
                        tvTitle1.setVisibility(View.GONE);
                        groupFabHome.setVisibility(View.GONE);
                        exFABtn.setIconResource(R.drawable.add);
                        exFABtn.extend();
                        llFab.setVisibility(View.GONE);
                        llFab.setBackground(getDrawable(R.drawable.transparent_background));
                        break;
                    case 1:
                        groupFabChat.setVisibility(View.GONE);
                        exFABtn.setIconResource(R.drawable.add);
                        exFABtn.extend();
                        llFab.setVisibility(View.GONE);
                        llFab.setBackground(getDrawable(R.drawable.transparent_background));
                        break;
                    case 2:
                        groupFabNote.setVisibility(View.GONE);
                        exFABtn.setIconResource(R.drawable.add);
                        exFABtn.extend();
                        llFab.setVisibility(View.GONE);
                        llFab.setBackground(getDrawable(R.drawable.transparent_background));
                        break;
                    case 3:
                        exFABtn.setVisibility(View.VISIBLE);
                        exFABtn.setIconResource(R.drawable.add);
                        exFABtn.extend();
                        llFab.setVisibility(View.GONE);
                        llFab.setBackground(getDrawable(R.drawable.transparent_background));
                        cimgProfile.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        cimgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedTab == 3) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            clickCounter = 0;
                        }
                    }, 1000);
                    if (clickCounter == 2) {
                        if (myPreference.isPinCompleted()) {
                            //todo 4 if app is going to another activity make isForeground = true
                            SplashActivity.isForeground = true;
                            Intent intent3 = new Intent(getApplicationContext(), PinLockActivity.class);
                            intent3.putExtra(logInActivity.REQUEST_CODE_NAME, REQUEST_ID);
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

        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.secondaryDarkColor));

        setFabVisibility();

    }

    @Override
    protected void onStart() {
        super.onStart();
        //todo 9 onStartOperation, it will check app is
        // coming from foreground or background.
        appLockCounterClass.onStartOperation();
        Uri currentUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getPhotoUrl();

        tvTitle.setTextSize(40);
        tvTitle.setText("Hello 👋");
        tvTitle1.setVisibility(View.VISIBLE);
        tvTitle1.setText(iFirebaseDAO.getDisplayName());

        if (currentUser == null) {
            // No user is signed in
        } else {
            // User logged in
            Glide.with(this).load(currentUser).into(cimgProfile);

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //todo 10 onPauseOperation, it will check app is
        // going to foreground or background.
        // if UI component made isForeground = true then it
        // is going to another activity then this method will make
        // isForeground = false, so user will not be verified.
        // if UI component is not clicked then it
        // is going in background then this method will make
        // isBackground = true and timer will started,
        // at time of return, user will be verified.
        appLockCounterClass.checkedItem = myPreference.getLockAppSelectedOption();
        appLockCounterClass.onPauseOperation();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //todo 11 app is going to close no to do anything
        SplashActivity.isForeground = true;
    }

    private void setFabVisibility() {
        isAllFabsVisible = false;
        exFABtn.extend();
        groupFabAll.setVisibility(View.GONE);
        exFABtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!isAllFabsVisible) {
                            exFABtn.shrink();
                            exFABtn.setIconResource(R.drawable.close);
                            exFABtn.setTranslationZ(1000);
                            llFab.setBackground(getDrawable(R.drawable.transparent_background));
                            llFab.setVisibility(View.VISIBLE);
                            switch (selectedTab) {
                                case 1:
                                    groupFabHome.setVisibility(View.VISIBLE);
                                    break;
                                case 2:
                                    groupFabChat.setVisibility(View.VISIBLE);
                                    break;
                                case 3:
                                    groupFabNote.setVisibility(View.VISIBLE);
                                    break;
                                case 4:
                                    groupFabAll.setVisibility(View.GONE);
                                    break;

                            }
                            isAllFabsVisible = true;
                        } else {
                            exFABtn.setIconResource(R.drawable.add);
                            exFABtn.extend();
                            llFab.setVisibility(View.INVISIBLE);
                            llFab.setBackground(getDrawable(R.drawable.transparent_background));
                            isAllFabsVisible = false;
                            switch (selectedTab) {
                                case 1:
                                    groupFabHome.setVisibility(View.GONE);
                                    break;
                                case 2:
                                    groupFabChat.setVisibility(View.GONE);
                                    break;
                                case 3:
                                    groupFabNote.setVisibility(View.GONE);
                                    break;
                                case 4:
                                    groupFabAll.setVisibility(View.GONE);
                                    break;

                            }
                        }

                    }
                });
    }


}