package com.example.keys.aman.settings;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.keys.R;
import com.example.keys.aman.SplashActivity;
import com.example.keys.aman.authentication.BiometricActivity;
import com.example.keys.aman.signin_login.LogInActivity;

public class AppInfoActivity extends AppCompatActivity {
    TextView tv_app_version;
    ImageView img_back;
    LogInActivity logInActivity = new LogInActivity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info);
        tv_app_version = findViewById(R.id.tv_app_version);
        img_back = findViewById(R.id.img_back);

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
        if (SplashActivity.isBackground){
            Intent intent = new Intent(AppInfoActivity.this, BiometricActivity.class);
            intent.putExtra(logInActivity.getREQUEST_CODE_NAME(), "LockBackGroundApp");
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
        overridePendingTransition(0, R.anim.slide_out_down);
    }
}