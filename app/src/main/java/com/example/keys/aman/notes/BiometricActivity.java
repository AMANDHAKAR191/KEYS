package com.example.keys.aman.notes;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.keys.R;
import com.example.keys.aman.SplashActivity;
import com.example.keys.aman.signin_login.LogInActivity;
//import com.example.keys.aman.app.signin_login.SignUpActivity;

public class BiometricActivity extends AppCompatActivity{

    TextView tvDisplayMessage;
    ImageView imgFingerPrint;
    private boolean isHardwareDetected;
    private boolean hasEnrolledFingerprints;
    LogInActivity logInActivity = new LogInActivity();
    private FingerprintManager fingerprintManager;
    private String comingrequestcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biometric);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        SharedPreferences sharedPreferences = getSharedPreferences(logInActivity.getSHARED_PREF_ALL_DATA(), MODE_PRIVATE);

        //Hooks
        tvDisplayMessage = findViewById(R.id.tv_result);
        imgFingerPrint = findViewById(R.id.img_fingerprint);

        // Check 1: Android version should be greater or equal to Marshmallow
        // Check 2: Device has Fingerprint Scanner
        // Check 3: Have permission to use fingerprint scanner in tha app
        // Check 4: Lock screen is secured with atleast 1 type of lock
        // Check 5: Atleast 1 Fingerprint is registered in device

        //Hide mobile no and
        Intent intent = getIntent();
        comingrequestcode = intent.getStringExtra(logInActivity.getREQUEST_CODE_NAME());
        if (comingrequestcode == null){
            comingrequestcode = "this";
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
            KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            try {
                isHardwareDetected = fingerprintManager.isHardwareDetected();
                hasEnrolledFingerprints = fingerprintManager.hasEnrolledFingerprints();

            }catch (NullPointerException e){

            }


            if (!isHardwareDetected) {
                tvDisplayMessage.setText("Fingerprint Scanner not detected in Device");
                boolean isPinSet =  sharedPreferences.getBoolean(logInActivity.getIS_PIN_SET(),false);
                if (isPinSet){
                    SplashActivity.isForeground = true;
                    Intent intent3 = new Intent(BiometricActivity.this, PinLockActivity.class);
                    intent3.putExtra(logInActivity.getREQUEST_CODE_NAME(),"LogInActivity");
                    intent3.putExtra("title","Enter Pin");
                    Toast.makeText(this, "isHardwareDetected: " + isHardwareDetected, Toast.LENGTH_SHORT).show();
                    startActivity(intent3);
                    finish();
                }else {
                    SplashActivity.isForeground = true;
                    Intent intent3 = new Intent(getApplicationContext(), PinLockActivity.class);
                    intent3.putExtra(logInActivity.getREQUEST_CODE_NAME(), "setpin");
                    intent3.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent3.putExtra("title", "Set Pin");
                    startActivity(intent);
                    finish();
                }
            } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                tvDisplayMessage.setText("Permission not granted to use Fingerprint Scanner");
            }
            else if (!keyguardManager.isKeyguardSecure()) {
                tvDisplayMessage.setText("Add Lock to your Phone in Setting");
                boolean ispin_set =  sharedPreferences.getBoolean(logInActivity.getIS_PIN_SET(),false);
                if (ispin_set){
                    SplashActivity.isForeground = true;
                    Intent intent3 = new Intent(BiometricActivity.this, PinLockActivity.class);
                    intent3.putExtra(logInActivity.getREQUEST_CODE_NAME(),"LogInActivity");
                    intent3.putExtra("title","Enter Pin");
                    startActivity(intent3);
                    finish();
                }else {
                    SplashActivity.isForeground = true;
                    Intent intent3 = new Intent(getApplicationContext(), PinLockActivity.class);
                    intent3.putExtra(logInActivity.getREQUEST_CODE_NAME(), "setpin");
                    intent3.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent3.putExtra("title", "Set Pin");
                    startActivity(intent);
                    finish();
                }
            } else if (!hasEnrolledFingerprints) {
                tvDisplayMessage.setText("You should add atleast 1 Fingerprint to use this Feature");
                boolean ispin_set =  sharedPreferences.getBoolean(logInActivity.getIS_PIN_SET(),false);
                Toast.makeText(this, "ispin_set" + ispin_set, Toast.LENGTH_SHORT).show();
                if (ispin_set){
                    SplashActivity.isForeground = true;
                    Intent intent3 = new Intent(BiometricActivity.this, PinLockActivity.class);
                    intent3.putExtra(logInActivity.getREQUEST_CODE_NAME(),"LogInActivity");
                    intent3.putExtra("title","Enter Pin");
                    startActivity(intent3);
                    finish();
                }else {
                    SplashActivity.isForeground = true;
                    Intent intent3 = new Intent(getApplicationContext(), PinLockActivity.class);
                    intent3.putExtra(logInActivity.getREQUEST_CODE_NAME(), "setpin");
                    intent3.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent3.putExtra("title", "Set Pin");
                    startActivity(intent);
                    finish();
                }
            } else {
                tvDisplayMessage.setText("Place your Finger to Acsess the app");
                callFingerPrintHandler(comingrequestcode, fingerprintManager);
            }

        }
    }

    public void callFingerPrintHandler(String comingrequestcode, FingerprintManager fingerprintManager) {
        FingerPrintHandler fingerPrintHandler = new FingerPrintHandler(this, BiometricActivity.this , comingrequestcode);
        fingerPrintHandler.startAuth(fingerprintManager,null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!SplashActivity.isForeground){
            SplashActivity.isBackground = true;
        }
        SplashActivity.isForeground = false;
    }

    @Override
    protected void onStart() {
        if (SplashActivity.isBackground) {
            callFingerPrintHandler(comingrequestcode, fingerprintManager);
        }
        if (SplashActivity.isForeground) {
            SplashActivity.isForeground = false;
        }

        super.onStart();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // TODO: Temporary Solution
        SplashActivity.isForeground = true;
        SplashActivity.isBackground = false;
        finish();
    }
}