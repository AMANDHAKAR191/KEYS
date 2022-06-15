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
import com.example.keys.aman.app.home.HomeActivity;
import com.example.keys.aman.app.home.addpassword.addPasswordData;
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

        if (b == false){
            paralable.setTextColor(Color.RED);
        }else {
            paralable.setTextColor(Color.BLACK);
            img_fingerprint.setImageResource(R.mipmap.done_icon);
            if (comingrequestcode.equals("LogInActivity")){

                SharedPreferences.Editor editor1 = sharedPreferences.edit();
                editor1.putBoolean(LogInActivity.ISAUTHENTICATED, true);
                editor1.apply();

                Intent intent = new Intent(context, HomeActivity.class);
                intent.putExtra(LogInActivity.REQUEST_CODE_NAME,comingrequestcode);
                context.startActivity(intent);
                activity.finish();
            }else if (comingrequestcode.equals("this")){
                comingrequestcode = "BiometricActivity";
                SharedPreferences.Editor editor1 = sharedPreferences.edit();
                editor1.putBoolean(LogInActivity.ISAUTHENTICATED, true);
                editor1.apply();

                Intent intent = new Intent(context, addPasswordData.class);
                intent.putExtra(LogInActivity.REQUEST_CODE_NAME,comingrequestcode);
                context.startActivity(intent);
                activity.finish();
            }

        }

    }
}
