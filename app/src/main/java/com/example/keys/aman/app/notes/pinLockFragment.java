package com.example.keys.aman.app.notes;

import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;
import com.example.keys.R;
import com.example.keys.aman.app.home.HomeActivity;
import com.example.keys.aman.app.signin_login.LogInActivity;
//import com.example.keys.aman.app.signin_login.SignUpActivity;

public class pinLockFragment extends AppCompatActivity {
    

    private PinLockView mPinLockView;
    private IndicatorDots mIndicatorDots;

    private final PinLockListener mPinLockListener = new PinLockListener() {
        @Override
        public void onComplete(String pin) {
            if (pin.equals("1234")) {
                if (comingrequestcode.equals("LogInActivity")) {
                    Intent intent = new Intent(pinLockFragment.this, HomeActivity.class);
                    intent.putExtra(LogInActivity.REQUEST_CODE_NAME, "pinLockFragment");
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(pinLockFragment.this, secretNotesActivity.class);
                    intent.putExtra(LogInActivity.REQUEST_CODE_NAME, "pinLockFragment");
                    startActivity(intent);
                    finish();
                }
            } else {
                vibrator.vibrate(200);
            }
        }

        @Override
        public void onEmpty() {
        }

        @Override
        public void onPinChange(int pinLength, String intermediatePin) {

        }
    };
    private Vibrator vibrator;
    private String comingrequestcode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.fragment_pin_lock);

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        mPinLockView = (PinLockView) findViewById(R.id.pin_lock_view);
        mIndicatorDots = (IndicatorDots) findViewById(R.id.indicator_dots);

        mPinLockView.attachIndicatorDots(mIndicatorDots);
        mPinLockView.setPinLockListener(mPinLockListener);
        mPinLockView.setCustomKeySet(new int[]{2, 3, 1, 5, 9, 6, 7, 0, 8, 4});
        mPinLockView.enableLayoutShuffling();

        mPinLockView.setPinLength(4);
        mPinLockView.setTextColor(ContextCompat.getColor(this, R.color.white));

        mIndicatorDots.setIndicatorType(IndicatorDots.IndicatorType.FILL_WITH_ANIMATION);

        //Hide mobile no and
        Intent intent = getIntent();
        comingrequestcode = intent.getStringExtra(LogInActivity.REQUEST_CODE_NAME);
        if (comingrequestcode == null) {
            comingrequestcode = "this";
        }

        if (comingrequestcode.equals("LogInActivity")) {
        }
    }


    public void gocencal(View view) {
        finish();
    }
}