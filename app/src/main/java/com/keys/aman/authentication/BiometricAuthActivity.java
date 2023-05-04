package com.keys.aman.authentication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.keys.aman.MyPreference;
import com.keys.aman.R;
import com.keys.aman.SplashActivity;
import com.keys.aman.base.TabLayoutActivity;
import com.keys.aman.signin_login.LogInActivity;

import java.util.concurrent.Executor;

public class BiometricAuthActivity extends AppCompatActivity {

    public static final String REQUEST_ID = "BiometricAuthActivity";
    BiometricPrompt biometricPrompt;
    BiometricPrompt.PromptInfo promptInfo;
    LogInActivity logInActivity = new LogInActivity();
    PinLockActivity pinLockActivity = new PinLockActivity();
    MyPreference myPreference;

    private String comingRequestCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biometric_authentication);
        myPreference = MyPreference.getInstance(this);

        Intent intent = getIntent();
        comingRequestCode = intent.getStringExtra(logInActivity.REQUEST_CODE_NAME);
        if (comingRequestCode == null){
            comingRequestCode = "this";
        }


        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate()){
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Toast.makeText(this, "Fingerprint Scanner not detected in Device", Toast.LENGTH_SHORT).show();

                SplashActivity.isForeground = true;
                Intent intent3;
                if (myPreference.isPinCompleted()){
                    intent3 = new Intent(BiometricAuthActivity.this, PinLockActivity.class);
                    intent3.putExtra(logInActivity.REQUEST_CODE_NAME,"LogInActivity");
                    intent3.putExtra("title","Enter Pin");
                }else {
                    intent3 = new Intent(getApplicationContext(), PinLockActivity.class);
                    intent3.putExtra(logInActivity.REQUEST_CODE_NAME, "setpin");
                    intent3.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent3.putExtra("title", "Set Pin");
                }
                startActivity(intent3);
                finish();
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Toast.makeText(this, "Biometric is not available", Toast.LENGTH_SHORT).show();
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Toast.makeText(this, "Biometric is not enrolled", Toast.LENGTH_SHORT).show();
                SplashActivity.isForeground = true;
                Intent intent2;
                if (myPreference.isPinCompleted()){
                    intent2 = new Intent(BiometricAuthActivity.this, PinLockActivity.class);
                    intent2.putExtra(logInActivity.REQUEST_CODE_NAME,BiometricAuthActivity.REQUEST_ID);
                    intent2.putExtra("title","Enter Pin");
                }else {
                    intent2 = new Intent(getApplicationContext(), PinLockActivity.class);
                    intent2.putExtra(logInActivity.REQUEST_CODE_NAME, TabLayoutActivity.REQUEST_ID);
                    intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent2.putExtra("title", "Set Pin");
                }
                startActivity(intent2);
                finish();
                break;
        }

        Executor executor = ContextCompat.getMainExecutor(this);

        biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(BiometricAuthActivity.this, "Login Error", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(BiometricAuthActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                SplashActivity.isForeground = true;
                switch (comingRequestCode) {
                    case LogInActivity.REQUEST_ID:
                        myPreference.setUserAuthenticated(true);

                        if (!myPreference.isPinCompleted()) {
                            SplashActivity.isForeground = true;
                            Intent intent = new Intent(BiometricAuthActivity.this, PinLockActivity.class);
                            intent.putExtra(logInActivity.REQUEST_CODE_NAME, LogInActivity.REQUEST_ID);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("title", "Set Pin");
                            startActivity(intent);

                        } else {
                            Intent intent = new Intent(BiometricAuthActivity.this, TabLayoutActivity.class);
                            intent.putExtra(logInActivity.REQUEST_CODE_NAME, comingRequestCode);
                            startActivity(intent);
                            finish();
                        }

                        break;
                    case "LockBackGroundApp":
                        SplashActivity.isBackground = false;
                        finish();
                        break;
                }
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(BiometricAuthActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
            }
        });
        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("KEYS")
                .setDescription("KEYS want to verify your biometric")
                .setDeviceCredentialAllowed(true).build();
        biometricPrompt.authenticate(promptInfo);
    }
}