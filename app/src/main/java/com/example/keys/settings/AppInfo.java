package com.example.keys.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.keys.R;
import com.example.keys.baseactivitys.SettingActivity;

public class AppInfo extends AppCompatActivity {
    TextView tv_app_version;
    ImageView img_back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info);
        tv_app_version = findViewById(R.id.tv_app_version);
        img_back = findViewById(R.id.img_back);

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AppInfo.this, SettingActivity.class));
                finish();
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
}