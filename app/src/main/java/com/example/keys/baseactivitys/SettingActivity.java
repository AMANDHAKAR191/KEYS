package com.example.keys.baseactivitys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.example.keys.R;
import com.example.keys.SignUpActivity;
import com.example.keys.settings.AppInfo;
import com.example.keys.settings.sendEmailActivity;

public class SettingActivity extends AppCompatActivity {

    TextView tv_app_info, tv_contectus;
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
        sw_enable_fingerprint = findViewById(R.id.sw_enable_fingerprint);
        img_back = findViewById(R.id.img_back);
        //TODO Check: add Security textview in this
        //TODO Check: let user choose weather user want to use fingerprint lock or not
        //TODO Check: ask user to use biometric info in this Activity only

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingActivity.this, HomeActivity.class));
                finish();
            }
        });
        tv_app_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingActivity.this, AppInfo.class));
            }
        });
        tv_contectus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingActivity.this, sendEmailActivity.class));
            }
        });

        fingerprintenabled();
    }

    private void fingerprintenabled() {


        sw_enable_fingerprint = findViewById(R.id.sw_enable_fingerprint);

        ischecked = sharedPreferences.getBoolean(SignUpActivity.KEY_USE_FINGERPRINT, false);
        sw_enable_fingerprint.setChecked(ischecked);
        sw_enable_fingerprint.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(SignUpActivity.KEY_USE_FINGERPRINT, b);
                editor.apply();
            }
        });
    }

}