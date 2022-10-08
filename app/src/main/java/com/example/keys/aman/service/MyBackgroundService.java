package com.example.keys.aman.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class MyBackgroundService extends Service {
    private int count = 0;
//    LogInActivity logInActivity = new LogInActivity();
//    Context context;
//    Activity activity;

    public MyBackgroundService() {
    }

//    public MyBackgroundService(Context context, Activity activity) {
//        this.context = context;
//        this.activity = activity;
//    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Log.e("BackgroundService", "Counter = " + count);
                    count = count + 1;
                    if (count > 10) {
                        count = 0;
//                        if (SplashActivity.isBackground) {
//                            Intent intent = new Intent(context, BiometricActivity.class);
//                            intent.putExtra(logInActivity.getREQUEST_CODE_NAME(), "LockBackGroundApp");
//                            startActivity(intent);
//                        }
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }
}
