package com.example.keys.aman.app.settings;

import static com.example.keys.aman.app.signin_login.LogInActivity.REQUEST_CODE_NAME;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.keys.R;
import com.example.keys.aman.app.SplashActivity;
import com.example.keys.aman.app.notes.BiometricActivity;

public class AppInfo extends AppCompatActivity {
    TextView tv_app_version;
    ImageView img_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info);
        tv_app_version = findViewById(R.id.tv_app_version);
        img_back = findViewById(R.id.img_back);

        Intent intent = getIntent();
        String comingrequestcode = intent.getStringExtra("request_code");
        if (comingrequestcode == null) {
            comingrequestcode = "this";
        }
        
        if (comingrequestcode.equals("LogInActivity")) {
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
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            tv_app_version.setText("Version: " + version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        if (SplashActivity.isBackground){
            Intent intent = new Intent(AppInfo.this, BiometricActivity.class);
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
        overridePendingTransition(0, R.anim.slide_out_down);
    }
}