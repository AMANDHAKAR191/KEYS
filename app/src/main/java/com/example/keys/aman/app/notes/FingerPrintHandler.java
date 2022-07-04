package com.example.keys.aman.app.notes;

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
import com.example.keys.aman.app.base.tabLayoutActivity;
import com.example.keys.aman.app.signin_login.LogInActivity;

public class FingerPrintHandler extends FingerprintManager.AuthenticationCallback {
    private final Context context;
    private FingerprintManager fingerprintManager;
    String comingrequestcode;
    Activity activity;
    public SharedPreferences sharedPreferences;

    public FingerPrintHandler(Context context, Activity activity ,String comingrequestcode) {
        this.context = context;
        this.activity = activity;
        this.comingrequestcode = comingrequestcode;
        sharedPreferences = activity.getSharedPreferences(LogInActivity.SHARED_PREF_ALL_DATA, MODE_PRIVATE);
    }

    public void startAuth(FingerprintManager fingerprintManager, FingerprintManager.CryptoObject cryptoObject){
        CancellationSignal cancellationSignal = new CancellationSignal();
        fingerprintManager.authenticate(cryptoObject,cancellationSignal ,0,this,null);
    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
        this.update("There was an Auth Error. " + errString,false);
        Intent intent = new Intent(context,BiometricActivity.class);
        intent.putExtra(LogInActivity.REQUEST_CODE_NAME,"LogInActivity");
        activity.startActivity(intent);
        activity.finish();
    }

    @Override
    public void onAuthenticationFailed() {
        this.update("Auth Failed ",false);
    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
        this.update("Error: " + helpString, false);
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        this.update("you can now access the app.",true);
    }

    private void update(String s, boolean b) {
        TextView paralable = ((Activity)context).findViewById(R.id.tv_result);
        ImageView img_fingerprint = ((Activity)context).findViewById(R.id.img_fingerprint);
        paralable.setText(s);

        if (!b){
            paralable.setTextColor(Color.RED);
        }else {
            paralable.setTextColor(Color.BLACK);
            img_fingerprint.setImageResource(R.mipmap.done_icon);
            switch (comingrequestcode){
                case "LogInActivity":
                    SharedPreferences.Editor editor1 = sharedPreferences.edit();
                    editor1.putBoolean(LogInActivity.ISAUTHENTICATED, true);
                    editor1.apply();

                    Intent intent = new Intent(context, tabLayoutActivity.class);
                    intent.putExtra(LogInActivity.REQUEST_CODE_NAME,comingrequestcode);
                    context.startActivity(intent);
                    activity.finish();
                    break;
                case "notesActivity":
                    comingrequestcode = "BiometricActivity";
                    SharedPreferences.Editor editor2 = sharedPreferences.edit();
                    editor2.putBoolean(LogInActivity.ISAUTHENTICATED, true);
                    editor2.apply();

                    Intent intent1 = new Intent(context, secretNotesActivity.class);
                    intent1.putExtra(LogInActivity.REQUEST_CODE_NAME,comingrequestcode);
                    context.startActivity(intent1);
                    activity.finish();
                    break;
                case "HomeActivity":
                    Intent intent2 = new Intent(context, tabLayoutActivity.class);
                    intent2.putExtra(LogInActivity.REQUEST_CODE_NAME,"HomeActivity");
                    context.startActivity(intent2);
                    activity.finish();
                    break;
            }
        }

    }
}
