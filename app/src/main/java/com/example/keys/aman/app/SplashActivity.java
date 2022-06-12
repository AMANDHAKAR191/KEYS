package com.example.keys.aman.app;

import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.keys.R;
import com.example.keys.aman.app.home.PassGenActivity;
import com.example.keys.aman.app.signin_login.LogInActivity;

import java.util.Arrays;

public class SplashActivity extends AppCompatActivity {

    private String[] PERMISSIONS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        createShortcutOfApp();

        Intent intent = new Intent(SplashActivity.this, LogInActivity.class);
        intent.putExtra(LogInActivity.REQUEST_CODE_NAME,"SplashActivity");
        startActivity(intent);
        finish();

        //TODO Check 5: check internet in this Activity also internet stability
        //TODO Check 6: Cehck internet spped in every Activity if speed is low then show Error to user

//        PERMISSIONS = new String[]{
//                Manifest.permission.READ_EXTERNAL_STORAGE,
////                Manifest.permission.ACCESS_NETWORK_STATE,
////                Manifest.permission.USE_BIOMETRIC,
////                Manifest.permission.VIBRATE
//        };
//
//        new Handler().postDelayed(new Runnable() {
//            @RequiresApi(api = Build.VERSION_CODES.P)
//            @Override
//            public void run() {
//                hasPermissions(SplashActivity.this, PERMISSIONS);
//                if (!hasPermissions(SplashActivity.this, PERMISSIONS)) {
//                    ActivityCompat.requestPermissions(SplashActivity.this, PERMISSIONS, 1);
//                }else {
//                    Intent i = new Intent(SplashActivity.this, LogInActivity.class);
//                    startActivity(i);
//                    finish();
//                }
//            }
//        }, 1000);


    }

    public void createShortcutOfApp() {

        ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);

        Intent intent = new Intent(SplashActivity.this, PassGenActivity.class);
        intent.setAction(Intent.ACTION_VIEW);
        ShortcutInfo shortcutInfo = new ShortcutInfo.Builder(this,"short1").
                setShortLabel("Gen Password").
                setLongLabel("Open PassGenActivity ").
                setIcon(Icon.createWithResource(SplashActivity.this,R.drawable.add)).
                setIntent(intent).
                build();
        shortcutManager.setDynamicShortcuts(Arrays.asList(shortcutInfo));
    }

//    private boolean hasPermissions(Context context, String... permissions) {
//        if (context != null && PERMISSIONS != null) {
//            for (String permission : PERMISSIONS) {
//                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
//                    return false;
//                }
//            }
//        }
//        return true;
//    }
//
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == 1) {
//            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                Toast.makeText(this, "External Storage Permission GRANTED", Toast.LENGTH_SHORT).show();
//                Intent i = new Intent(SplashActivity.this, LogInActivity.class);
//                startActivity(i);
//                finish();
//            }else {
//                Toast.makeText(this, "External Storage Permission DENIED", Toast.LENGTH_SHORT).show();
//            }
//
////            if (grantResults[1] == PackageManager.PERMISSION_GRANTED){
////                Toast.makeText(this, "Network Access Permission GRANTED", Toast.LENGTH_SHORT).show();
////                Intent i = new Intent(SplashActivity.this, LogInActivity.class);
////                startActivity(i);
////                finish();
////            }else {
////                Toast.makeText(this, "Network Access Permission DENIED", Toast.LENGTH_SHORT).show();
////            }
////
////            if (grantResults[2] == PackageManager.PERMISSION_GRANTED){
////                Toast.makeText(this, "Biometric Permission GRANTED", Toast.LENGTH_SHORT).show();
////            }else {
////                Toast.makeText(this, "Biometric Permission DENIED", Toast.LENGTH_SHORT).show();
////            }
////
////            if (grantResults[3] == PackageManager.PERMISSION_GRANTED){
////                Toast.makeText(this, "Vibration Permission GRANTED", Toast.LENGTH_SHORT).show();
////            }else {
////                Toast.makeText(this, "Vibration Permission DENIED", Toast.LENGTH_SHORT).show();
////            }
//        }
//    }

}