package com.example.keys.aman.app.settings;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.keys.R;
import com.example.keys.aman.app.home.HomeActivity;

public class AppInfo extends AppCompatActivity {
    TextView tv_app_version;
    ImageView img_back, img_next;
    private String comingrequestcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info);
        tv_app_version = findViewById(R.id.tv_app_version);
        img_back = findViewById(R.id.img_back);
        img_next= findViewById(R.id.img_next);
        Intent intent = getIntent();
        comingrequestcode = intent.getStringExtra("request_code");
        if (comingrequestcode == null) {
            comingrequestcode = "this";
        }

        Toast.makeText(AppInfo.this, comingrequestcode, Toast.LENGTH_SHORT).show();
        if (comingrequestcode.equals("LogInActivity")) {
            img_next.setVisibility(View.VISIBLE);
            img_back.setVisibility(View.INVISIBLE);
        }else {
            img_next.setVisibility(View.INVISIBLE);
            img_back.setVisibility(View.VISIBLE);
        }

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            }
        });
        img_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(AppInfo.this, HomeActivity.class);
                startActivity(intent1);
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