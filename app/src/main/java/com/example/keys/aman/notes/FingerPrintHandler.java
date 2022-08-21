package com.example.keys.aman.notes;

import static android.content.Context.MODE_PRIVATE;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.keys.R;
import com.example.keys.aman.SplashActivity;
import com.example.keys.aman.base.TabLayoutActivity;
import com.example.keys.aman.signin_login.LogInActivity;

public class FingerPrintHandler extends FingerprintManager.AuthenticationCallback {
    private final Context context;
    private FingerprintManager fingerprintManager;
    String comingRequestCode;
    Activity activity;
    public SharedPreferences sharedPreferences;
    TextView paralable;
    CancellationSignal cancellationSignal;

    public FingerPrintHandler(Context context, Activity activity, String comingrRequestCode) {
        this.context = context;
        this.activity = activity;
        this.comingRequestCode = comingrRequestCode;
        sharedPreferences = activity.getSharedPreferences(LogInActivity.SHARED_PREF_ALL_DATA, MODE_PRIVATE);
    }

    public void startAuth(FingerprintManager fingerprintManager, FingerprintManager.CryptoObject cryptoObject) {
        cancellationSignal = new CancellationSignal();
        fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
        this.update("There was an Auth Error. " + errString, false);
//        Intent intent = new Intent(context,BiometricActivity.class);
//        intent.putExtra(LogInActivity.REQUEST_CODE_NAME,"LogInActivity");
//        activity.startActivity(intent);
//        activity.finish();
    }

    @Override
    public void onAuthenticationFailed() {
        this.update("Auth Failed ", false);
    }

    public void stopFingerAuth() {
        if (cancellationSignal != null && !cancellationSignal.isCanceled()) {
            cancellationSignal.cancel();
        }
    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
        this.update("Error: " + helpString, false);
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        this.update("you can now access the app.", true);
    }

    private void update(String s, boolean b) {
        paralable = ((Activity) context).findViewById(R.id.tv_result);
        ImageView img_fingerprint = ((Activity) context).findViewById(R.id.img_fingerprint);
        paralable.setText(s);

        if (!b) {
            paralable.setTextColor(Color.RED);
        } else {
            paralable.setTextColor(Color.BLACK);
            img_fingerprint.setImageResource(R.mipmap.done_icon);
            SplashActivity.isForeground = true;
            switch (comingRequestCode) {
                case "LogInActivity":
                    SharedPreferences.Editor editor1 = sharedPreferences.edit();
                    editor1.putBoolean(LogInActivity.IS_AUTHENTICATED, true);
                    editor1.apply();

                    String masterPin = sharedPreferences.getString(LogInActivity.MASTER_PIN, "");
                    boolean ispinset = sharedPreferences.getBoolean(LogInActivity.IS_PIN_SET,false);

                    if (!ispinset) {
                        SplashActivity.isForeground = true;
                        Intent intent = new Intent(context, PinLockActivity.class);
                        intent.putExtra(LogInActivity.REQUEST_CODE_NAME, "setpin");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.putExtra("title", "Set Pin");
                        activity.startActivity(intent);

                    } else {
                        Intent intent = new Intent(context, TabLayoutActivity.class);
                        intent.putExtra(LogInActivity.REQUEST_CODE_NAME, comingRequestCode);
                        context.startActivity(intent);
                        activity.finish();
                    }


                    break;
                case "notesActivity":
                    comingRequestCode = "BiometricActivity";
                    SharedPreferences.Editor editor2 = sharedPreferences.edit();
                    editor2.putBoolean(LogInActivity.IS_AUTHENTICATED, true);
                    editor2.apply();

                    Intent intent1 = new Intent(context, SecretNotesActivity.class);
                    intent1.putExtra(LogInActivity.REQUEST_CODE_NAME, comingRequestCode);
                    context.startActivity(intent1);
                    activity.finish();
                    break;
                case "HomeActivity":
                    Intent intent2 = new Intent(context, TabLayoutActivity.class);
                    intent2.putExtra(LogInActivity.REQUEST_CODE_NAME, "HomeActivity");
                    context.startActivity(intent2);
                    activity.finish();
                    break;
                case "LockBackGroundApp":
                    SplashActivity.isBackground = false;
                    activity.finish();
                    break;
            }
        }

    }
}
