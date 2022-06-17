package com.example.keys.aman.app.notes;

import static com.example.keys.aman.app.signin_login.LogInActivity.SHARED_PREF_ALL_DATA;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.keys.aman.app.settings.SettingActivity;
import com.example.keys.aman.app.signin_login.LogInActivity;

public class pinLockFragment extends AppCompatActivity {

    public static final String TAG = "PinLockView";

    private PinLockView mPinLockView;
    private IndicatorDots mIndicatorDots;
    private String PIN = "";
    String setpin, confirmpin;
    int temp = 0;

    private SharedPreferences sharedPreferences;
    private final PinLockListener mPinLockListener = new PinLockListener() {
        @Override
        public void onComplete(String pin) {

            if (comingrequestcode.equals("LogInActivity")) {
                if (pin.equals(PIN)) {
                    Intent intent = new Intent(pinLockFragment.this, HomeActivity.class);
                    intent.putExtra(LogInActivity.REQUEST_CODE_NAME, "pinLockFragment");
                    startActivity(intent);
                    finish();

                } else {
                    vibrator.vibrate(200);
                }

            } else if (comingrequestcode.equals("ShowCardviewDataActivity")) {
                if (pin.equals(PIN)) {
                    Intent intent = new Intent(pinLockFragment.this, secretNotesActivity.class);
                    intent.putExtra("result","yes");
                    setResult(RESULT_OK,intent);
                    finish();

                } else {
                    vibrator.vibrate(200);
                    Intent intent = new Intent(pinLockFragment.this, secretNotesActivity.class);
                    intent.putExtra("result","no");
                    setResult(RESULT_OK,intent);
                    finish();
                }

            }else if (comingrequestcode.equals("setpin")){
                
                if (temp == 0){
                    setpin = pin;
                    tv_title_name.setText("Confirm PIn");
                    mPinLockView.resetPinLockView();
                    temp = 1;
                }else {
                    confirmpin = pin;
                    tv_title_name.setText("Pin Matched");
                    SharedPreferences.Editor editor1 = sharedPreferences.edit();
                    editor1.putBoolean(LogInActivity.ISPIN_SET,true);
                    editor1.putString(LogInActivity.MASTER_PIN,confirmpin);
                    editor1.apply();
                    Toast.makeText(pinLockFragment.this, "Pin Set" + confirmpin, Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
                    intent.putExtra("result","yes");
                    setResult(RESULT_OK,intent);
                    finish();
                }
                
                
            }else if (comingrequestcode.equals("notesActivity")) {
                if (pin.equals(PIN)) {
                    Intent intent = new Intent(pinLockFragment.this, secretNotesActivity.class);
                    startActivity(intent);
                    finish();

                } else {
                    vibrator.vibrate(200);
                    Intent intent = new Intent(pinLockFragment.this, secretNotesActivity.class);
                    startActivity(intent);
                    finish();
                }

            }


        }

        @Override
        public void onEmpty() {
        }

        @Override
        public void onPinChange(int pinLength, String intermediatePin) {
            Log.d(TAG, "Pin changed, new length " + pinLength + " with intermediate pin " + intermediatePin);
        }
    };
    private Vibrator vibrator;
    private String comingrequestcode;
    TextView tv_title_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.fragment_pin_lock);
        sharedPreferences = getSharedPreferences(SHARED_PREF_ALL_DATA, MODE_PRIVATE);


        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        mPinLockView = (PinLockView) findViewById(R.id.pin_lock_view);
        mIndicatorDots = (IndicatorDots) findViewById(R.id.indicator_dots);
        tv_title_name = findViewById(R.id.tv_title_name);

        mPinLockView.attachIndicatorDots(mIndicatorDots);
        mPinLockView.setPinLockListener(mPinLockListener);
        mPinLockView.setCustomKeySet(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 0});

        mPinLockView.setPinLength(6);
        mPinLockView.setTextColor(ContextCompat.getColor(this, R.color.white));


        mIndicatorDots.setIndicatorType(IndicatorDots.IndicatorType.FILL_WITH_ANIMATION);

        //Hide mobile no and
        Intent intent = getIntent();
        comingrequestcode = intent.getStringExtra(LogInActivity.REQUEST_CODE_NAME);
        if (comingrequestcode == null) {
            comingrequestcode = "this";
        }

        Toast.makeText(pinLockFragment.this, comingrequestcode, Toast.LENGTH_SHORT).show();

        if (comingrequestcode.equals("LogInActivity")) {
            PIN = sharedPreferences.getString(LogInActivity.MASTER_PIN,"no");
        } else if (comingrequestcode.equals("ShowCardviewDataActivity")) {
            PIN = sharedPreferences.getString(LogInActivity.MASTER_PIN,"no");
            String title = intent.getStringExtra("title");
            tv_title_name.setText(title);
        } else if (comingrequestcode.equals("notesActivity")) {
            PIN = sharedPreferences.getString(LogInActivity.MASTER_PIN,"no");
            String title = intent.getStringExtra("title");
            tv_title_name.setText(title);
        }else if (comingrequestcode.equals("setpin")){
            String title = intent.getStringExtra("title");
            tv_title_name.setText(title);
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