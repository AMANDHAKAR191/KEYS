package com.example.keys.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.example.keys.R;
import com.example.keys.signin_login.SignUpActivity;

public class SettingActivity extends AppCompatActivity {

    TextView tv_app_info, tv_contectus, tv_privacy_policy, tv_terms_and_conditions;
    ImageView img_back;
    Switch sw_enable_fingerprint;
    SharedPreferences sharedPreferences;
    public static boolean ischecked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        sharedPreferences = getSharedPreferences(SignUpActivity.SHARED_PREF_ALL_DATA, MODE_PRIVATE);
        /*---------------Hooks--------------*/
        tv_app_info = findViewById(R.id.tv_app_info);
        tv_contectus = findViewById(R.id.tv_contectus);
        tv_privacy_policy = findViewById(R.id.tv_privacy_policy);
        tv_terms_and_conditions = findViewById(R.id.tv_terms_and_conditions);
        sw_enable_fingerprint = findViewById(R.id.sw_enable_fingerprint);
        img_back = findViewById(R.id.img_back);
        //TODO Check: add Security textview in this
        //TODO Check: let user choose weather user want to use fingerprint lock or not
        //TODO Check: ask user to use biometric info in this Activity only

        tv_app_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingActivity.this, AppInfo.class));
                overridePendingTransition(R.anim.slide_right_left, 0);
            }
        });
        tv_contectus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contactUsFragment contactUs = new contactUsFragment();
                contactUs.show(getSupportFragmentManager(),"contact_us");
            }
        });
        tv_privacy_policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent open_privacy_policy = new Intent(Intent.ACTION_VIEW, Uri.parse("https://amandhakar.blogspot.com/2022/02/privacy-policy-keys.html"));
                startActivity(open_privacy_policy);
            }
        });
        tv_terms_and_conditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent open_terms_conditions = new Intent(Intent.ACTION_VIEW, Uri.parse("https://amandhakar.blogspot.com/2022/02/terms-conditions-keys.html"));
                startActivity(open_terms_conditions);
            }
        });
    }


    public void open_profileAcitivity(View view) {
        startActivity(new Intent(SettingActivity.this, ProfileActivity.class));
        overridePendingTransition(R.anim.slide_right_left, 0);
    }

    public void img_back(View view) {
        overridePendingTransition(R.anim.slide_left_right, R.anim.slide_left_right);
        finish();
    }
}