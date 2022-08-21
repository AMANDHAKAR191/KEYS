package com.example.keys.aman.notes;

import static com.example.keys.aman.signin_login.LogInActivity.SHARED_PREF_ALL_DATA;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
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
import com.example.keys.aman.base.TabLayoutActivity;
import com.example.keys.aman.signin_login.LogInActivity;

public class PinLockActivity extends AppCompatActivity {


    private PinLockView mPinLockView;
    private Vibrator vibrator;
    private String comingRequestCode;
    TextView tvTitle, tvErrorMessage;
    private SharedPreferences sharedPreferences;
    Handler handler = new Handler();

    private String PIN = "";
    String setPin, confirmPin;
    int temp = 0;
    private int count;

    private final PinLockListener mPinLockListener = new PinLockListener() {
        @Override
        public void onComplete(String pin) {

            switch (comingRequestCode) {
                case "LogInActivity":
                    if (pin.equals(PIN)) {
                        Intent intent = new Intent(PinLockActivity.this, TabLayoutActivity.class);
                        intent.putExtra(LogInActivity.REQUEST_CODE_NAME, "pinLockFragment");
                        startActivity(intent);
                        finish();

                    } else {
//                        vibrator.vibrate(200);
//                        vibrator.vibrate(VibrationEffect.DEFAULT_AMPLITUDE);
                        vibrator.vibrate(VibrationEffect.createOneShot(200,VibrationEffect.DEFAULT_AMPLITUDE));
                        tvTitle.setText("Wrong Pin");
                        wrongPin();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                tvTitle.setText("Enter Pin Again");
                                mPinLockView.resetPinLockView();
                            }
                        },400);
                    }
                    break;
                case "ShowCardviewDataActivity":
                    if (pin.equals(PIN)) {
                        Intent intent = new Intent(PinLockActivity.this, SecretNotesActivity.class);
                        intent.putExtra("result", "yes");
                        setResult(RESULT_OK, intent);
                        finish();

                    } else {
                        tvTitle.setText("Wrong Pin");
//                        vibrator.vibrate(200);
                        vibrator.vibrate(VibrationEffect.createOneShot(200,VibrationEffect.DEFAULT_AMPLITUDE));

                        mPinLockView.resetPinLockView();
                        wrongPin();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                tvTitle.setText("Enter Pin Again");
                                mPinLockView.resetPinLockView();
                            }
                        },400);
                    }
                    break;
                case "setpin":
                    if (temp == 0) {
                        setPin = pin;
                        tvTitle.setText("Confirm PIn");
                        mPinLockView.resetPinLockView();
                        temp = 1;
                    } else {
                        confirmPin = pin;
                        if (confirmPin.equals(setPin)){
                            tvTitle.setText("Pin Matched");
                            SharedPreferences.Editor editor1 = sharedPreferences.edit();
                            editor1.putBoolean(LogInActivity.IS_PIN_SET, true);
                            editor1.putString(LogInActivity.MASTER_PIN, confirmPin);
                            editor1.apply();

                            Toast.makeText(PinLockActivity.this, "Pin Set", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(getApplicationContext(), TabLayoutActivity.class);
                            intent.putExtra("result", "yes");
                            startActivity(intent);
                            finish();
                        }else {
                            tvTitle.setText("Wrong Pin");
                            mPinLockView.resetPinLockView();
//                            vibrator.vibrate(200);
                            vibrator.vibrate(VibrationEffect.createOneShot(200,VibrationEffect.DEFAULT_AMPLITUDE));
                            Intent intent = new Intent(PinLockActivity.this, PinLockActivity.class);
                            intent.putExtra(LogInActivity.REQUEST_CODE_NAME, "setpin");
                            intent.putExtra("title", "Set Pin");
                            startActivity(intent);
                            finish();
                        }
                    }
                    break;
                case "changepin":
                    if (pin.equals(PIN)) {
                        Intent intent = new Intent(PinLockActivity.this, PinLockActivity.class);
                        intent.putExtra(LogInActivity.REQUEST_CODE_NAME, "setpin");
                        intent.putExtra("title", "Set Pin");
                        startActivity(intent);
                        finish();
                    } else {
                        tvTitle.setText("Wrong Pin");
                        mPinLockView.resetPinLockView();
//                        vibrator.vibrate(200);
                        vibrator.vibrate(VibrationEffect.createOneShot(200,VibrationEffect.DEFAULT_AMPLITUDE));
                        wrongPin();
                    }
                    break;
                case "notesActivity":
                    if (pin.equals(PIN)) {
                        Intent intent = new Intent(PinLockActivity.this, SecretNotesActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        tvTitle.setText("Wrong Pin");
//                        vibrator.vibrate(200);
                        vibrator.vibrate(VibrationEffect.createOneShot(200,VibrationEffect.DEFAULT_AMPLITUDE));
                        mPinLockView.resetPinLockView();
                        wrongPin();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                tvTitle.setText("Enter Pin Again");
                                mPinLockView.resetPinLockView();
                            }
                        },400);
                    }
                    break;
            }

        }

        @Override
        public void onEmpty() {
        }

        @Override
        public void onPinChange(int pinLength, String intermediatePin) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //it is mandatory to call this requestWindowFeature method before setContentView
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.fragment_pin_lock);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        sharedPreferences = getSharedPreferences(SHARED_PREF_ALL_DATA, MODE_PRIVATE);

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        //Hooks
        mPinLockView = findViewById(R.id.pin_lock_view);
        IndicatorDots mIndicatorDots = findViewById(R.id.indicator_dots);
        tvTitle = findViewById(R.id.tv_title_name);
        tvErrorMessage = findViewById(R.id.tv_error_message);
        //properties
        mPinLockView.attachIndicatorDots(mIndicatorDots);
        mPinLockView.setPinLockListener(mPinLockListener);
        mPinLockView.setCustomKeySet(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 0});
        mPinLockView.setPinLength(6);
        mPinLockView.setTextColor(ContextCompat.getColor(this, R.color.white));
        mIndicatorDots.setIndicatorType(IndicatorDots.IndicatorType.FILL_WITH_ANIMATION);

        //Hide mobile no and
        Intent intent = getIntent();
        comingRequestCode = intent.getStringExtra(LogInActivity.REQUEST_CODE_NAME);
        if (comingRequestCode == null) {
            comingRequestCode = "this";
        }

        Toast.makeText(PinLockActivity.this, comingRequestCode, Toast.LENGTH_SHORT).show();

        if (comingRequestCode.equals("LogInActivity")) {
            PIN = sharedPreferences.getString(LogInActivity.MASTER_PIN, "no");
            tvTitle.setText("Enter Pin");
        } else if (comingRequestCode.equals("ShowCardviewDataActivity")) {
            PIN = sharedPreferences.getString(LogInActivity.MASTER_PIN, "no");
            String title = intent.getStringExtra("title");
            tvTitle.setText(title);
        } else if (comingRequestCode.equals("notesActivity")) {
            PIN = sharedPreferences.getString(LogInActivity.MASTER_PIN, "no");
            String title = intent.getStringExtra("title");
            tvTitle.setText(title);
        } else if (comingRequestCode.equals("setpin")) {
            String title = intent.getStringExtra("title");
            tvTitle.setText(title);
        } else if (comingRequestCode.equals("changepin")) {
            PIN = sharedPreferences.getString(LogInActivity.MASTER_PIN, "no");
            String title = intent.getStringExtra("title");
            tvTitle.setText(title);
        }
    }

    public void wrongPin(){
        count =  count + 1;
        if (count >= 3){
            SharedPreferences.Editor editor1 = sharedPreferences.edit();
            editor1.putBoolean(LogInActivity.IS_USER_RESTRICTED, true);
            editor1.apply();
            tvErrorMessage.setVisibility(View.VISIBLE);
            mPinLockView.setVisibility(View.INVISIBLE);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    tvErrorMessage.setVisibility(View.INVISIBLE);
                    mPinLockView.setVisibility(View.VISIBLE);
                    count = 0;
                }
            },5000);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}