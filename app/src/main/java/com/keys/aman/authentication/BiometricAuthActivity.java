/*created by AMAN DHAKAR
Last update 07/05/2025*/
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
        //init variable
        myPreference = MyPreference.getInstance(this);

        onComingFromActivity();
        openBiometricPrompt();
    }

    private void openBiometricPrompt() {
        Executor executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                SplashActivity.isForeground = true;
                Intent intent2;
                if (myPreference.isPinCompleted()){
                    intent2 = new Intent(BiometricAuthActivity.this, PinLockActivity.class);
                    intent2.putExtra(logInActivity.REQUEST_CODE_NAME,BiometricAuthActivity.REQUEST_ID);
                }else {
                    intent2 = new Intent(getApplicationContext(), PinLockActivity.class);
                    intent2.putExtra(logInActivity.REQUEST_CODE_NAME, TabLayoutActivity.REQUEST_ID);
                    intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                }
                startActivity(intent2);
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

    public void onComingFromActivity(){
        Intent intent = getIntent();
        comingRequestCode = intent.getStringExtra(logInActivity.REQUEST_CODE_NAME);
        if (comingRequestCode == null){
            comingRequestCode = "this";
        }
    }
}