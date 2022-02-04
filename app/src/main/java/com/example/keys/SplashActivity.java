package com.example.keys;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class SplashActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    private int BIOMETRIC_PERMISSION_CODE = 1;
    Boolean isfingerprinton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //TODO Check 5: check internet in this Activity also internet stability
        //TODO Check 6: Cehck internet spped in every Activity if speed is low then show Error to user

        new Handler().postDelayed(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void run() {
                if (ContextCompat.checkSelfPermission(SplashActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    sharedPreferences = getSharedPreferences(SignUpActivity.SHARED_PREF_ALL_DATA, MODE_PRIVATE);
                    isfingerprinton = sharedPreferences.getBoolean(SignUpActivity.KEY_USE_FINGERPRINT, false);

//                    isfingerprinton = true;
                    System.out.println(isfingerprinton);
                    if (!isfingerprinton){
                        Intent intent1 = new Intent(SplashActivity.this, LogInActivity.class);
                        startActivity(intent1);
                        finish();
                    }else {
                        Intent intent2 = new Intent(SplashActivity.this, BiometricActivity.class);
                        startActivity(intent2);
                        finish();
                    }

                } else {
                    requestStoragePermission();
                }

            }
        }, 1000);


    }

    //for storage permission
    @RequiresApi(api = Build.VERSION_CODES.P)
    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("To use biometric lock in app. \nWe need access of your \nbiometric data. ")
                    .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.P)
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(SplashActivity.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, BIOMETRIC_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, BIOMETRIC_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == BIOMETRIC_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(SplashActivity.this, LogInActivity.class);
                startActivity(i);
                finish();

            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

}