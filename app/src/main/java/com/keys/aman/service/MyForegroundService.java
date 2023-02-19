package com.keys.aman.service;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.keys.R;
import com.keys.aman.messages.ChatActivity;
import com.keys.aman.signin_login.LogInActivity;

public class MyForegroundService extends Service {

    ChatActivity chatActivity = new ChatActivity();
    LogInActivity logInActivity = new LogInActivity();
    SharedPreferences sharedPreferences;
    Context context;
    Activity activity;

    public MyForegroundService(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    public MyForegroundService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sharedPreferences = getSharedPreferences(logInActivity.SHARED_PREF_ALL_DATA, MODE_PRIVATE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Log.e("MyForegroundService", "Service is Running...");
//                    Log.e("MyForegroundService", chatActivity.receiverRoom);

                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();
        final String CHANNEL_ID = "Foreground Service ID";
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_ID,
                NotificationManager.IMPORTANCE_LOW
        );

        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        Notification.Builder notification = new Notification.Builder(this, CHANNEL_ID)
                .setContentText("Service is Running...")
                .setContentTitle("Autofill Service")
                .setSmallIcon(R.drawable.keys_privacy)
                .setPriority(Notification.PRIORITY_LOW)
                .setCategory(Notification.CATEGORY_SERVICE);

        startForeground(101, notification.build());
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
