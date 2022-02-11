package com.example.keys.notes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.keys.R;

public class BiometricActivity extends AppCompatActivity{

    TextView tv_result;
    ImageView img_fingerprint;
    private FingerprintManager fingerprintManager;
    private KeyguardManager keyguardManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biometric);

        tv_result = findViewById(R.id.tv_result);
        img_fingerprint = findViewById(R.id.img_fingerprint);

        // Check 1: Android version should be greater or equal to Marshmallow
        // Check 2: Device has Fingerprint Scanner
        // Check 3: Have permission to use fingerprint scanner in tha app
        // Check 4: Lock screen is secured with atleast 1 type of lock
        // Check 5: Atleast 1 Fingerprint is registered in device

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
            keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            if (!fingerprintManager.isHardwareDetected()) {
                tv_result.setText("Fingerprint Scanner not detected in Device");
            } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                tv_result.setText("Permission not granted to use Fingerprint Scanner");
            } else if (!keyguardManager.isKeyguardSecure()) {
                tv_result.setText("Add Lock to your Phone in Setting");
            } else if (!fingerprintManager.hasEnrolledFingerprints()) {
                tv_result.setText("You should add atleast 1 Fingerprint to use this Feature");
            } else {
                tv_result.setText("Place your Finger to Acsess the app");
                FingerPrintHandler fingerPrintHandler = new FingerPrintHandler(this);
                fingerPrintHandler.startAuth(fingerprintManager,null);
            }

        }
    }
}