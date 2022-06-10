package com.example.keys.aman.app.notes;

import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;
import com.example.keys.R;
import com.example.keys.aman.app.home.HomeActivity;
//import com.example.keys.aman.app.signin_login.SignUpActivity;

public class pinLockFragment extends AppCompatActivity {

    public static final String TAG = "PinLockView";

    private PinLockView mPinLockView;
    private IndicatorDots mIndicatorDots;

    private final PinLockListener mPinLockListener = new PinLockListener() {
        @Override
        public void onComplete(String pin) {
            Log.d(TAG, "Pin complete: " + pin);
            if (pin.equals("1234")){
                Log.d(TAG, "Pin matched");
                if (comingrequestcode.equals("LogInActivity")) {
                    startActivity(new Intent(pinLockFragment.this, HomeActivity.class));
                    finish();
                }else {
                    startActivity(new Intent(pinLockFragment.this, secretNotesActivity.class));
                    finish();
                }
            }else {
                vibrator.vibrate(200);
            }
        }

        @Override
        public void onEmpty() {
            Log.d(TAG, "Pin empty");
        }

        @Override
        public void onPinChange(int pinLength, String intermediatePin) {
            Log.d(TAG, "Pin changed, new length " + pinLength + " with intermediate pin " + intermediatePin);
        }
    };
    private Vibrator vibrator;
    private String comingrequestcode;
    TextView tv_use_biometrix;

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
//        tv_use_biometrix = findViewById(R.id.tv_use_biometrix);

        mPinLockView.attachIndicatorDots(mIndicatorDots);
        mPinLockView.setPinLockListener(mPinLockListener);
        mPinLockView.setCustomKeySet(new int[]{2, 3, 1, 5, 9, 6, 7, 0, 8, 4});
        mPinLockView.enableLayoutShuffling();

        mPinLockView.setPinLength(4);
        mPinLockView.setTextColor(ContextCompat.getColor(this, R.color.white));

        mIndicatorDots.setIndicatorType(IndicatorDots.IndicatorType.FILL_WITH_ANIMATION);

        //Hide mobile no and
        Intent intent = getIntent();
        comingrequestcode = intent.getStringExtra("request_code");
        if (comingrequestcode == null){
            comingrequestcode = "this";
        }

        Toast.makeText(pinLockFragment.this, comingrequestcode, Toast.LENGTH_SHORT).show();

        if (comingrequestcode.equals("LogInActivity")) {
//            tv_use_biometrix.setVisibility(View.INVISIBLE);
        }
    }

//    public void open_fingerprintActivity(View view) {
//        startActivity(new Intent(pinLockFragment.this,BiometricActivity.class));
//        finish();
//    }

    public void gocencal(View view) {
        finish();
    }
}