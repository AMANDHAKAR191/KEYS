package com.example.keys.aman.authentication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.util.Log;

import com.example.keys.aman.SplashActivity;
import com.example.keys.aman.signin_login.LogInActivity;

public class AppLockCounterClass implements AppLockCounter {

    private static final String TAG = "AppLockCounterClass";
    Activity activity;
    Context context;
    CountDownTimer countDownTimer;
    LogInActivity logInActivity = new LogInActivity();
    public int checkedItem = 0;

    public AppLockCounterClass(Activity activity, Context context)
    {
        this.activity = activity;
        this.context = context;
    }

    @Override
    public void initializeCounter() {
        if (countDownTimer == null) {
            countDownTimer = new CountDownTimer(10000, 1000) {
                @Override
                public void onTick(long milliSecondCount) {
                    Log.d(TAG, "Counter Running:" + milliSecondCount / 1000);
                }

                @Override
                public void onFinish() {
                    Log.d(TAG, "Counter Finished");
                    if (SplashActivity.isBackground) {
                        Intent intent = new Intent(context, BiometricAuthActivity.class);
                        intent.putExtra(logInActivity.getREQUEST_CODE_NAME(), "LockBackGroundApp");
                        activity.startActivity(intent);
                    }
                    if (SplashActivity.isForeground) {
                        SplashActivity.isForeground = false;
                    }
                }
            }.start();
        }

    }

    public void onStartOperation() {
        switch (checkedItem) {
            case 0:
                if (SplashActivity.isBackground) {
                    Intent intent = new Intent(context, BiometricAuthActivity.class);
                    intent.putExtra(logInActivity.getREQUEST_CODE_NAME(), "LockBackGroundApp");
                    activity.startActivity(intent);
                }
                if (SplashActivity.isForeground) {
                    SplashActivity.isForeground = false;
                }
                break;
            case 1:
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
                if (SplashActivity.isForeground) {
                    SplashActivity.isForeground = false;
                }
                break;
            case 2:

                break;
        }
    }

    public void onPauseOperation() {
        switch (checkedItem) {
            case 0:
                if (!SplashActivity.isForeground) {
                    SplashActivity.isBackground = true;
                }
                break;
            case 1:
                Log.d(TAG, "SplashActivity.isForeground=? " + SplashActivity.isForeground);
                if (!SplashActivity.isForeground) {
                    SplashActivity.isBackground = true;
                    if (countDownTimer == null) {
                        initializeCounter();
                    } else {
                        countDownTimer.start();
                    }
                } else {
                    SplashActivity.isForeground = false;
                }
                Log.d(TAG, "SplashActivity.isForeground=?? " + SplashActivity.isForeground);
                break;
            case 2:
                break;
        }
    }

}
