package com.keys.aman;

import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

//import com.keys.aman.data.LocalDatabase;
import com.keys.aman.home.PasswordGeneratorActivity;
import com.keys.aman.service.MyFirebaseMessagingService;
import com.keys.aman.signin_login.LogInActivity;

import java.util.Arrays;

public class SplashActivity extends AppCompatActivity {


    private String[] PERMISSIONS;
    public static boolean isConnected = false;
    ConnectivityManager connectivityManager;
    public static boolean isForeground = false;
    public static boolean isBackground = false;
    ImageView imageView, imageView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        imageView = findViewById(R.id.imageView);
        imageView1 = findViewById(R.id.imageView1);

//        LocalDatabase.getInstance(this).getDatabases();

        createShortcutOfApp();

        //start firebase messaging service
        Intent serviceIntent = new Intent(SplashActivity.this, MyFirebaseMessagingService.class);
        startService(serviceIntent);

        isForeground = true;
        Intent i = new Intent(SplashActivity.this, LogInActivity.class);
        startActivity(i);
        finish();

    }


    public void createShortcutOfApp() {

        ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);

        Intent intent = new Intent(SplashActivity.this, PasswordGeneratorActivity.class);
        intent.setAction(Intent.ACTION_VIEW);
        ShortcutInfo shortcutInfo = new ShortcutInfo.Builder(this, "short1").
                setShortLabel("Gen Password").
                setLongLabel("Open PassGenActivity ").
                setIcon(Icon.createWithResource(SplashActivity.this, R.drawable.add)).
                setIntent(intent).
                build();
        shortcutManager.setDynamicShortcuts(Arrays.asList(shortcutInfo));
    }


}