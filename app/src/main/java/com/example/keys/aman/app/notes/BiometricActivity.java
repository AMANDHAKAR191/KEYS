package com.example.keys.aman.app.notes;

import static com.example.keys.aman.app.signin_login.LogInActivity.SHARED_PREF_ALL_DATA;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.keys.R;
import com.example.keys.aman.app.home.HomeActivity;
import com.example.keys.aman.app.signin_login.LogInActivity;
//import com.example.keys.aman.app.signin_login.SignUpActivity;

public class BiometricActivity extends AppCompatActivity{

    TextView tv_result;
    ImageView img_fingerprint;
    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;
    private String comingrequestcode;
    private SharedPreferences sharedPreferences;
    private boolean isHardwareDetected;
    private boolean hasEnrolledFingerprints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biometric);
        sharedPreferences = getSharedPreferences(SHARED_PREF_ALL_DATA, MODE_PRIVATE);
        tv_result = findViewById(R.id.tv_result);
        img_fingerprint = findViewById(R.id.img_fingerprint);

        // Check 1: Android version should be greater or equal to Marshmallow
        // Check 2: Device has Fingerprint Scanner
        // Check 3: Have permission to use fingerprint scanner in tha app
        // Check 4: Lock screen is secured with atleast 1 type of lock
        // Check 5: Atleast 1 Fingerprint is registered in device

        //Hide mobile no and
        Intent intent = getIntent();
        comingrequestcode = intent.getStringExtra(LogInActivity.REQUEST_CODE_NAME);
        if (comingrequestcode == null){
            comingrequestcode = "this";
        }


//        if (comingrequestcode.equals("LogInActivity")) {
//
//        } else if (comingrequestcode.equals("this")) {
//
//        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
            keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            try {
                isHardwareDetected = fingerprintManager.isHardwareDetected();
                hasEnrolledFingerprints = fingerprintManager.hasEnrolledFingerprints();

            }catch (NullPointerException e){

            }


            if (!isHardwareDetected) {
                tv_result.setText("Fingerprint Scanner not detected in Device");
                boolean ispin_set =  sharedPreferences.getBoolean(LogInActivity.ISPIN_SET,false);
                if (ispin_set){
                    Intent intent3 = new Intent(BiometricActivity.this,pinLockFragment.class);
                    intent3.putExtra(LogInActivity.REQUEST_CODE_NAME,"LogInActivity");
                    intent3.putExtra("title","Enter Pin");
                    Toast.makeText(this, "isHardwareDetected: " + isHardwareDetected, Toast.LENGTH_SHORT).show();
                    startActivity(intent3);
                    finish();
                }else {
                    Toast.makeText(this, "Fingerprint Scanner not detected in Device. please set pin", Toast.LENGTH_SHORT).show();
                    Intent intent3 = new Intent(BiometricActivity.this, HomeActivity.class);
                    intent3.putExtra(LogInActivity.REQUEST_CODE_NAME,"LogInActivity");
                    startActivity(intent3);
                    finish();
                }
            } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                tv_result.setText("Permission not granted to use Fingerprint Scanner");
            } else if (!keyguardManager.isKeyguardSecure()) {
                tv_result.setText("Add Lock to your Phone in Setting");
                boolean ispin_set =  sharedPreferences.getBoolean(LogInActivity.ISPIN_SET,false);
                if (ispin_set){
                    Intent intent3 = new Intent(BiometricActivity.this,pinLockFragment.class);
                    intent3.putExtra(LogInActivity.REQUEST_CODE_NAME,"LogInActivity");
                    intent3.putExtra("title","Enter Pin");
                    startActivity(intent3);
                    finish();
                }else {
                    Toast.makeText(this, "Pin is set yet! please set pin", Toast.LENGTH_SHORT).show();
                    Intent intent3 = new Intent(BiometricActivity.this, HomeActivity.class);
                    intent3.putExtra(LogInActivity.REQUEST_CODE_NAME,"LogInActivity");
                    startActivity(intent3);
                    finish();
                }
            } else if (!hasEnrolledFingerprints) {
                tv_result.setText("You should add atleast 1 Fingerprint to use this Feature");
                boolean ispin_set =  sharedPreferences.getBoolean(LogInActivity.ISPIN_SET,false);
                Toast.makeText(this, "ispin_set" + ispin_set, Toast.LENGTH_SHORT).show();
                if (ispin_set){
                    Intent intent3 = new Intent(BiometricActivity.this,pinLockFragment.class);
                    intent3.putExtra(LogInActivity.REQUEST_CODE_NAME,"LogInActivity");
                    intent3.putExtra("title","Enter Pin");
                    startActivity(intent3);
                    finish();
                }else {
                    Toast.makeText(this, "Pin is set yet! please set pin", Toast.LENGTH_SHORT).show();
                    Intent intent3 = new Intent(BiometricActivity.this,HomeActivity.class);
                    intent3.putExtra(LogInActivity.REQUEST_CODE_NAME,"LogInActivity");
                    startActivity(intent3);
                    finish();
                }
            } else {
                tv_result.setText("Place your Finger to Acsess the app");
                FingerPrintHandler fingerPrintHandler = new FingerPrintHandler(this, BiometricActivity.this ,comingrequestcode);
                fingerPrintHandler.startAuth(fingerprintManager,null);
            }

        }
    }

}