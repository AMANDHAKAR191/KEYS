package com.keys.aman.settings;


import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.keys.aman.data.MyPreference;
import com.keys.aman.R;
import com.keys.aman.SplashActivity;
import com.keys.aman.authentication.AppLockCounterClass;
import com.keys.aman.base.TabLayoutActivity;
import com.keys.aman.signin_login.LogInActivity;

public class AppInfoActivity extends AppCompatActivity {
    TextView tv_app_version;
    ImageView img_back;
    LogInActivity logInActivity = new LogInActivity();
    TabLayoutActivity tabLayoutActivity = new TabLayoutActivity();
    //todo 2 object calling of AppLockCounterClass
    AppLockCounterClass appLockCounterClass = new AppLockCounterClass(AppInfoActivity.this, AppInfoActivity.this);
    private SharedPreferences sharedPreferences;
    MyPreference myPreference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info);
        myPreference = MyPreference.getInstance(this);
        tv_app_version = findViewById(R.id.tv_app_version);
        img_back = findViewById(R.id.img_back);
        //todo 3 when is coming from background or foreground always isForeground false
        SplashActivity.isForeground = false;

        Intent intent = getIntent();
        String comingRequestCode = intent.getStringExtra("request_code");
        if (comingRequestCode == null) {
            comingRequestCode = "this";
        }
        
        if (comingRequestCode.equals("LogInActivity")) {
            img_back.setVisibility(View.INVISIBLE);
        }else {
            img_back.setVisibility(View.VISIBLE);
        }

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo 4 if app is going to another activity make isForeground = true
                SplashActivity.isForeground = true;
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });

        try {

            tv_app_version.setText("Version Name: " + getPackageManager().
                    getPackageInfo(getPackageName(),0).versionName + "\n Version Code: " + getPackageManager().
                    getPackageInfo(getPackageName(),0).versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
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
        appLockCounterClass.checkedItem = myPreference.getLockAppSelectedOption();
        appLockCounterClass.onPauseOperation();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //todo 11 app is going to close no to do anything
        SplashActivity.isForeground = true;
        finish();
        overridePendingTransition(0, R.anim.slide_out_down);
    }
}