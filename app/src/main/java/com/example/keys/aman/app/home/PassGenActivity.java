package com.example.keys.aman.app.home;


import static com.example.keys.aman.app.signin_login.LogInActivity.REQUEST_CODE_NAME;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.keys.R;
import com.example.keys.aman.app.SplashActivity;
import com.example.keys.aman.app.notes.BiometricActivity;
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

import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PassGenActivity extends AppCompatActivity {

    public static RewardedAd mRewardedAd;
    private final String rewardedAdId = "ca-app-pub-3752721223259598/1273800402";

    final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    final DatabaseReference myRef = firebaseDatabase.getReference("usedPassword");

    //variable
    SwitchCompat swCapitalCaseLetter, swLowerCaseLetter, swNumbers, swSymbols;
    public Button btnUsePassword, btnCopyPassword;
    ImageButton imgBack;
    TextView tvPassword, tvPasswordStrength;
    String comingRequestCode;
    Slider slider;
    int MAX_LENGTH = 0;
    private String pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass_gen);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

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
        MAX_LENGTH = 8;
        genrate_password();
        //Hide use button
        Intent intent = getIntent();
        comingRequestCode = intent.getStringExtra(REQUEST_CODE_NAME);
        if (comingRequestCode == null) {
            comingRequestCode = "fromAppsShortcut";
        }
        if (comingRequestCode.equals("HomeActivity")) {
            btnUsePassword.setVisibility(View.INVISIBLE);
        } else if (comingRequestCode.equals("addPasswordData")) {
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
                intentresult.putExtra("saved_Password", pass);
                setResult(RESULT_OK, intentresult);
                finish();
            }
        });
        btnCopyPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRewardedAd != null) {
                    Activity activityContext = PassGenActivity.this;
                    mRewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                            // Handle the reward.

                            int rewardAmount = rewardItem.getAmount();
                            String rewardType = rewardItem.getType();
                        }
                    });
                } else {
                    Toast.makeText(PassGenActivity.this, "The rewarded ad wasn't ready yet.", Toast.LENGTH_SHORT).show();
                }

                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("Copy_Password", pass);
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(PassGenActivity.this, "Copied!", Toast.LENGTH_SHORT).show();
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

        RewardedAd.load(this, rewardedAdId,
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        Toast.makeText(PassGenActivity.this, loadAdError.getMessage(), Toast.LENGTH_LONG).show();
                        mRewardedAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        mRewardedAd = rewardedAd;
                        Toast.makeText(PassGenActivity.this, "Ad was loaded.", Toast.LENGTH_LONG).show();
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

    protected String genrate_password() {
        StringBuilder password = new StringBuilder();
        ArrayList<Integer> passSel = new ArrayList<>();
        slider.addOnChangeListener(new Slider.OnChangeListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                MAX_LENGTH = (int) slider.getValue();
            }
        });

        pass = generateRandomPassword(MAX_LENGTH, swCapitalCaseLetter.isChecked(), swLowerCaseLetter.isChecked(), swNumbers.isChecked(), swSymbols.isChecked());
        tvPassword.setText("Password: " + pass);

        Pattern pattern = Pattern.compile("[a-zA-Z0-9]");
        Pattern pattern1 = Pattern.compile("[^a-zA-Z0-9]");
        Matcher matcher = pattern.matcher(pass);
        Matcher matcher1 = pattern1.matcher(pass);
        boolean matchFound = matcher.find();
        boolean matchFound1 = matcher1.find();

        if (matchFound & !matchFound1) {
            if (pass.length() < 8) {
                tvPasswordStrength.setText("Strength: Very week");
                tvPasswordStrength.setTextColor(Color.RED);

            } else {
                if (pass.length() < 12) {
                    tvPasswordStrength.setText("Strength: week");
                    tvPasswordStrength.setTextColor(Color.YELLOW);
                } else {
                    if (pass.length() > 12) {
                        tvPasswordStrength.setText("Strength: strong");
                        tvPasswordStrength.setTextColor(Color.GREEN);
                    }
                }
            }

        } else if (!matchFound & matchFound1) {
            if (pass.length() < 8) {
                tvPasswordStrength.setText("Strength: week");
                tvPasswordStrength.setTextColor(Color.RED);

            } else {
                if (pass.length() < 12) {
                    tvPasswordStrength.setText("Strength: strong");
                    tvPasswordStrength.setTextColor(Color.YELLOW);
                } else {
                    if (pass.length() > 12) {
                        tvPasswordStrength.setText("Strength: strong");
                        tvPasswordStrength.setTextColor(Color.GREEN);
                    }
                }
            }
        } else {
            if (pass.length() < 8) {
                tvPasswordStrength.setText("Strength: strong");
                tvPasswordStrength.setTextColor(Color.RED);

            } else {
                if (pass.length() < 12) {
                    tvPasswordStrength.setText("Strength: very strong");
                    tvPasswordStrength.setTextColor(Color.YELLOW);
                } else {
                    if (pass.length() > 12) {
                        tvPasswordStrength.setText("Strength: very strong");
                        tvPasswordStrength.setTextColor(Color.GREEN);
                    }
                }
            }
        }

        return pass;
    }

    public void goBack(View view) {
        SplashActivity.isForeground = true;
        finish();
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (SplashActivity.isBackground){
            Intent intent = new Intent(PassGenActivity.this, BiometricActivity.class);
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
        finish();
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
    }
}