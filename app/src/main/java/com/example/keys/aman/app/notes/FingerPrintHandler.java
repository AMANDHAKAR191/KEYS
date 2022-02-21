package com.example.keys.aman.app.notes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.keys.R;
import com.example.keys.aman.app.home.HomeActivity;

public class FingerPrintHandler extends FingerprintManager.AuthenticationCallback {
    private Context context;
    private FingerprintManager fingerprintManager;

    public FingerPrintHandler(Context context) {
        this.context = context;
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
        TextView paralable = (TextView) ((Activity)context).findViewById(R.id.tv_result);
        ImageView img_fingerprint = (ImageView) ((Activity)context).findViewById(R.id.img_fingerprint);
        paralable.setText(s);

        if (b == false){
            paralable.setTextColor(Color.RED);
        }else {
            paralable.setTextColor(Color.BLACK);
            context.startActivity(new Intent(context, HomeActivity.class));
            ((Activity) context).finish();
        }

    }
}
