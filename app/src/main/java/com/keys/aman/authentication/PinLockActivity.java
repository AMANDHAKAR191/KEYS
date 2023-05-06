/*created by AMAN DHAKAR
Last update 07/05/2025*/
package com.keys.aman.authentication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;
import com.keys.aman.MyPreference;
import com.keys.aman.R;
import com.keys.aman.base.TabLayoutActivity;

import com.keys.aman.home.ShowCardViewDataDialog;
import com.keys.aman.notes.SecretNotesActivity;
import com.keys.aman.service.BasicService;
import com.keys.aman.settings.SettingFragment;
import com.keys.aman.signin_login.LogInActivity;

public class PinLockActivity extends AppCompatActivity {

    TextView tvTitle, tvDescription, tvErrorMessage;
    Handler handler = new Handler();
    LogInActivity logInActivity = new LogInActivity();
    MyPreference myPreference;
    String setPin, confirmPin;
    int temp = 0;
    private PinLockView mPinLockView;
    private Vibrator vibrator;
    private String comingRequestCode;
    private SharedPreferences sharedPreferences;
    private String PIN = "";
    private int count;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //it is mandatory to call this requestWindowFeature method before setContentView
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.fragment_pin_lock);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        myPreference = MyPreference.getInstance(this);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        //Hooks
        mPinLockView = findViewById(R.id.pin_lock_view);
        IndicatorDots mIndicatorDots = findViewById(R.id.indicator_dots);
        tvTitle = findViewById(R.id.tv_title_name);
        tvDescription = findViewById(R.id.tv_description);
        tvErrorMessage = findViewById(R.id.tv_error_message);

        //properties
        mPinLockView.attachIndicatorDots(mIndicatorDots);
        mPinLockView.setPinLockListener(mPinLockListener);
        mPinLockView.setCustomKeySet(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 0});
        mPinLockView.setPinLength(6);
        mPinLockView.setTextColor(ContextCompat.getColor(this, R.color.white));
        mIndicatorDots.setIndicatorType(IndicatorDots.IndicatorType.FILL_WITH_ANIMATION);

        onComingFromActivity();
    }

    private void onComingFromActivity() {
        Intent intent = getIntent();
        comingRequestCode = intent.getStringExtra(logInActivity.REQUEST_CODE_NAME);
        if (comingRequestCode == null) {
            comingRequestCode = "this";
        }
        switch (comingRequestCode) {
            case TabLayoutActivity.REQUEST_ID: //for opening secret note
                PIN = myPreference.getMasterPin();
                tvTitle.setText("verity Master Pin");
                tvDescription.setText("enter 6 digit master pin to open secret vault");
                break;
            case ShowCardViewDataDialog.REQUEST_ID: //for viewing the password
                PIN = myPreference.getMasterPin();
                tvTitle.setText("verity Master Pin");
                tvDescription.setText("enter 6 digit master pin to see password");
                break;
            case LogInActivity.REQUEST_ID: // for for setting new pin for new user
                tvTitle.setText("Set Master Pin");
                tvDescription.setText("enter 6 digit pin\\nNote: if forgot this pin is not recoverable");
                break;
            case SettingFragment.REQUEST_ID:
                PIN = myPreference.getMasterPin();
                tvTitle.setText("Verify Master Pin");
                tvDescription.setText("Enter your previous 6 digit master Pin to continue");
                break;
            case BiometricAuthActivity.REQUEST_ID: // already logged in user authentication
                PIN = myPreference.getMasterPin();
                tvTitle.setText("Verify Master Pin");
                tvDescription.setText("enter 6 digit pin");
                break;
            case BasicService.REQUEST_ID: // for authenticating user before filling dataset
                PIN = myPreference.getMasterPin();
                tvTitle.setText("Verify Master Pin");
                tvDescription.setText("Enter your 6 digit Master pin to continue");

        }
    }

    private final PinLockListener mPinLockListener = new PinLockListener() {
        @Override
        public void onComplete(String pin) {
            switch (comingRequestCode) {
                case LogInActivity.REQUEST_ID: // for setting new pin for new user
                    if (temp == 0) {    //1st time get the pin
                        setPin = pin;
                        tvTitle.setText("Confirm PIn");
                        mPinLockView.resetPinLockView();
                        temp = 1;
                    } else {    //and 2nd time verify the saved pin with new pin
                        confirmPin = pin;
                        if (confirmPin.equals(setPin)) {    //if matched then go
                            tvTitle.setText("Pin Matched");
                            //update local database
                            myPreference.setPinCompleted(true);
                            myPreference.setMasterPin(confirmPin);
                            Toast.makeText(PinLockActivity.this, "Pin Set", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), TabLayoutActivity.class);
                            intent.putExtra(logInActivity.REQUEST_CODE_NAME, LogInActivity.REQUEST_ID);
                            startActivity(intent);
                        } else {    //otherwise go again
                            tvTitle.setText("Wrong Pin");
                            mPinLockView.resetPinLockView();
                            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                            Intent intent = new Intent(PinLockActivity.this, PinLockActivity.class);
                            // for confirm the pin we have open the same page again
                            intent.putExtra(logInActivity.REQUEST_CODE_NAME, LogInActivity.REQUEST_ID);
                            startActivity(intent);
                        }
                        finish();
                    }
                    break;
                case ShowCardViewDataDialog.REQUEST_ID: //for viewing the password
                    if (pin.equals(PIN)) {
                        Intent intent = new Intent(PinLockActivity.this, SecretNotesActivity.class);
                        intent.putExtra("result", "yes");
                        setResult(RESULT_OK, intent);
                        finish();

                    } else {
                        tvTitle.setText("Wrong Pin");
                        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                        wrongPin();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                tvTitle.setText("Enter Pin Again");
                                mPinLockView.resetPinLockView();
                            }
                        }, 400);
                    }
                    break;
                case SettingFragment.REQUEST_ID:    //for changing the pin
                    if (pin.equals(PIN)) {
                        Intent intent = new Intent(PinLockActivity.this, PinLockActivity.class);
                        //for change the pin After verifying the pin we have use same process as we used at the time of login
                        intent.putExtra(logInActivity.REQUEST_CODE_NAME, LogInActivity.REQUEST_ID);
                        startActivity(intent);
                        finish();
                    } else {
                        tvTitle.setText("Wrong Pin");
                        mPinLockView.resetPinLockView();
                        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                        wrongPin();
                    }
                    break;
                case BiometricAuthActivity.REQUEST_ID:  //for if biometric is involved
                    if (pin.equals(PIN)) {
                        Intent intent = new Intent(PinLockActivity.this, TabLayoutActivity.class);
                        intent.putExtra(logInActivity.REQUEST_CODE_NAME, LogInActivity.REQUEST_ID);
                        startActivity(intent);
                        finish();
                    } else {
                        tvTitle.setText("Wrong Pin");
                        mPinLockView.resetPinLockView();
                        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                        wrongPin();
                    }
                    break;
                case TabLayoutActivity.REQUEST_ID:  //for opening the secret note
                    if (pin.equals(PIN)) {
                        Intent intent = new Intent(PinLockActivity.this, SecretNotesActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        tvTitle.setText("Wrong Pin");
                        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                        wrongPin();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                tvTitle.setText("Enter Pin Again");
                                mPinLockView.resetPinLockView();
                            }
                        }, 400);
                    }
                    break;
                case BasicService.REQUEST_ID:   //for authenticating the dataset before filling
                    if (pin.equals(PIN)) {
                        setResult(RESULT_OK);
                        finish();

                    } else {
                        tvTitle.setText("Wrong Pin");
                        vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                        mPinLockView.resetPinLockView();
                        wrongPin();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                tvTitle.setText("Enter Pin Again");
                                mPinLockView.resetPinLockView();
                            }
                        }, 400);
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

    public void wrongPin() {
        count = count + 1;
        if (count >= 3) {
            //restrict user from login
            myPreference.setUserRestricted(true);

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
            }, 5000);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}