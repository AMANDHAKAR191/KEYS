package com.example.keys.aman.home;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.keys.R;
import com.example.keys.aman.SplashActivity;
import com.example.keys.aman.authentication.AppLockCounterClass;
import com.example.keys.aman.base.TabLayoutActivity;
import com.example.keys.aman.home.addpassword.AddPasswordActivity;
import com.example.keys.aman.signin_login.LogInActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.material.slider.Slider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordGeneratorActivity extends AppCompatActivity {

    public static RewardedAd mRewardedAd;
    public static final String REQUEST_ID = "PasswordGeneratorActivity";


    final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    final DatabaseReference myRef = firebaseDatabase.getReference("usedPassword");
    LogInActivity logInActivity = new LogInActivity();
    TabLayoutActivity tabLayoutActivity = new TabLayoutActivity();
    //todo 2 object calling of AppLockCounterClass
    AppLockCounterClass appLockCounterClass = new AppLockCounterClass(PasswordGeneratorActivity.this, PasswordGeneratorActivity.this);

    //variable
    SwitchCompat swCapitalCaseLetter, swLowerCaseLetter, swNumbers, swSymbols;
    public Button btnUsePassword, btnCopyPassword;
    ImageButton imgBack;
    TextView tvPassword, tvPasswordStrength;
    Slider slider;

    String comingRequestCode;
    int maxLength = 0;
    private String generatedPassword;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass_gen);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        sharedPreferences = getSharedPreferences(logInActivity.getSHARED_PREF_ALL_DATA(), MODE_PRIVATE);
        //todo 3 when is coming from background make isForeground false
        SplashActivity.isForeground = false;

        // Hooks
        switchButton();
        tvPassword = findViewById(R.id.tv_password);
        tvPasswordStrength = findViewById(R.id.tv_password_strength);
        //tv_pass.setText("Password: ");
        slider = findViewById(R.id.slider_pass_lenght);
        btnUsePassword = findViewById(R.id.bt_use);
        btnCopyPassword = findViewById(R.id.bt_copy);
        imgBack = findViewById(R.id.img_back);
        //TODO Check: show all used password in a list
        //TODO Check: add feature to see last 10 or 20 used password and give button to clear used password

        // set default value of checkbox
        swCapitalCaseLetter.setChecked(true);
        swLowerCaseLetter.setChecked(true);
        swNumbers.setChecked(true);
        swSymbols.setChecked(true);
        maxLength = 8;
        genrate_password();
        //Hide use button
        Intent intent = getIntent();
        comingRequestCode = intent.getStringExtra(logInActivity.getREQUEST_CODE_NAME());
        if (comingRequestCode == null) {
            comingRequestCode = "fromAppsShortcut";
        }
        if (comingRequestCode.equals(TabLayoutActivity.REQUEST_ID)) {
            btnUsePassword.setVisibility(View.INVISIBLE);
        } else if (comingRequestCode.equals(AddPasswordActivity.REQUEST_ID)) {
            btnUsePassword.setVisibility(View.VISIBLE);
        } else if (comingRequestCode.equals("fromAppsShortcut")) {
            btnUsePassword.setVisibility(View.INVISIBLE);
        }

        //Slider
        slider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {

            }

            @SuppressLint("RestrictedApi")
            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                genrate_password();
            }
        });

        btnUsePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textViewpassword = tvPassword.getText().toString();
                Intent intentresult = getIntent();
                intentresult.putExtra(logInActivity.REQUEST_CODE_NAME, REQUEST_ID);
                intentresult.putExtra("saved_Password", generatedPassword);
                setResult(RESULT_OK, intentresult);
                finish();
            }
        });
        btnCopyPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRewardedAd != null) {
                    Activity activityContext = PasswordGeneratorActivity.this;
                    mRewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                            // Handle the reward.

                            int rewardAmount = rewardItem.getAmount();
                            String rewardType = rewardItem.getType();
                        }
                    });
                } else {
                    Toast.makeText(PasswordGeneratorActivity.this, "The rewarded ad wasn't ready yet.", Toast.LENGTH_SHORT).show();
                }

                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("Copy_Password", generatedPassword);
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(PasswordGeneratorActivity.this, "Copied!", Toast.LENGTH_SHORT).show();
            }
        });

        showRewardedAd();
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        showRewardedAd();
//    }

    private void switchButton() {
        swCapitalCaseLetter = findViewById(R.id.checkBox1);
        swLowerCaseLetter = findViewById(R.id.checkBox2);
        swNumbers = findViewById(R.id.checkBox3);
        swSymbols = findViewById(R.id.checkBox4);
        swCapitalCaseLetter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                genrate_password();
            }
        });
        swLowerCaseLetter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                genrate_password();
            }
        });
        swNumbers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                genrate_password();
            }
        });
        swSymbols.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                genrate_password();
            }
        });
    }

    private void showRewardedAd() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {

            }
        });

        AdRequest adRequest = new AdRequest.Builder().build();

        String rewardedAdId = "ca-app-pub-3752721223259598/1273800402";
        RewardedAd.load(this, rewardedAdId,
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        Toast.makeText(PasswordGeneratorActivity.this, loadAdError.getMessage(), Toast.LENGTH_LONG).show();
                        mRewardedAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        mRewardedAd = rewardedAd;
                        Toast.makeText(PasswordGeneratorActivity.this, "Ad was loaded.", Toast.LENGTH_LONG).show();
                    }
                });
    }


    public static String generateRandomPassword(int max_length, boolean upperCase, boolean lowerCase, boolean numbers, boolean specialCharacters) {
        Random rn = new Random();
        StringBuilder sb = new StringBuilder(max_length);
        try {
            String upperCaseChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
            String lowerCaseChars = "abcdefghijklmnopqrstuvwxyz";
            String numberChars = "0123456789";
            String specialChars = "!@#$%^&*()_-+=<>?/{}~|";
            String allowedChars = "";


            //this will fulfill the requirements of atleast one character of a type.
            if (upperCase) {
                allowedChars += upperCaseChars;
                sb.append(upperCaseChars.charAt(rn.nextInt(upperCaseChars.length() - 1)));
            }
            if (lowerCase) {
                allowedChars += lowerCaseChars;
                sb.append(lowerCaseChars.charAt(rn.nextInt(lowerCaseChars.length() - 1)));
            }
            if (numbers) {
                allowedChars += numberChars;
                sb.append(numberChars.charAt(rn.nextInt(numberChars.length() - 1)));
            }
            if (specialCharacters) {
                allowedChars += specialChars;
                sb.append(specialChars.charAt(rn.nextInt(specialChars.length() - 1)));
            }
            //fill the allowed length from different chars now.
            for (int i = sb.length(); i < max_length; ++i) {
                sb.append(allowedChars.charAt(rn.nextInt(allowedChars.length())));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    protected void genrate_password() {
        slider.addOnChangeListener(new Slider.OnChangeListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                maxLength = (int) slider.getValue();
            }
        });

        generatedPassword = generateRandomPassword(maxLength, swCapitalCaseLetter.isChecked(), swLowerCaseLetter.isChecked(), swNumbers.isChecked(), swSymbols.isChecked());
        tvPassword.setText("Password: " + generatedPassword);

        Pattern pattern = Pattern.compile("[a-zA-Z0-9]");
        Pattern pattern1 = Pattern.compile("[^a-zA-Z0-9]");
        Matcher matcher = pattern.matcher(generatedPassword);
        Matcher matcher1 = pattern1.matcher(generatedPassword);
        boolean matchFound = matcher.find();
        boolean matchFound1 = matcher1.find();

        if (matchFound & !matchFound1) {
            if (generatedPassword.length() < 8) {
                tvPasswordStrength.setText("Strength: Very week");
                tvPasswordStrength.setTextColor(Color.RED);

            } else {
                if (generatedPassword.length() < 12) {
                    tvPasswordStrength.setText("Strength: week");
                    tvPasswordStrength.setTextColor(Color.YELLOW);
                } else {
                    if (generatedPassword.length() > 12) {
                        tvPasswordStrength.setText("Strength: strong");
                        tvPasswordStrength.setTextColor(Color.GREEN);
                    }
                }
            }

        } else if (!matchFound & matchFound1) {
            if (generatedPassword.length() < 8) {
                tvPasswordStrength.setText("Strength: week");
                tvPasswordStrength.setTextColor(Color.RED);

            } else {
                if (generatedPassword.length() < 12) {
                    tvPasswordStrength.setText("Strength: strong");
                    tvPasswordStrength.setTextColor(Color.YELLOW);
                } else {
                    if (generatedPassword.length() > 12) {
                        tvPasswordStrength.setText("Strength: strong");
                        tvPasswordStrength.setTextColor(Color.GREEN);
                    }
                }
            }
        } else {
            if (generatedPassword.length() < 8) {
                tvPasswordStrength.setText("Strength: strong");
                tvPasswordStrength.setTextColor(Color.RED);

            } else {
                if (generatedPassword.length() < 12) {
                    tvPasswordStrength.setText("Strength: very strong");
                    tvPasswordStrength.setTextColor(Color.YELLOW);
                } else {
                    if (generatedPassword.length() > 12) {
                        tvPasswordStrength.setText("Strength: very strong");
                        tvPasswordStrength.setTextColor(Color.GREEN);
                    }
                }
            }
        }
    }

    public void goBack(View view) {
        SplashActivity.isForeground = true;
        finish();
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //todo 9 onStartOperation, it will check app is
        // coming from foreground or background.
        appLockCounterClass.onStartOperation();
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
        appLockCounterClass.checkedItem = sharedPreferences.getInt(tabLayoutActivity.LOCK_APP_OPTIONS, 0);
        appLockCounterClass.onPauseOperation();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //todo 11 do anything
        SplashActivity.isForeground = true;
        finish();
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
    }
}