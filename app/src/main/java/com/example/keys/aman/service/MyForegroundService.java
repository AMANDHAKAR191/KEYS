package com.example.keys.aman.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.keys.R;
import com.example.keys.aman.messages.ChatActivity;

public class MyForegroundService extends Service {

    ChatActivity chatActivity = new ChatActivity();
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    Log.e("MyForegroundService", "Service is Running...");
//                    Log.e("MyForegroundService", chatActivity.receiverRoom);

                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

//                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("messageUserList");
//                    reference.child(chatActivity.senderPublicUid).child("userPersonalChatList").child(chatActivity.receiverPublicUid)
//                            .addValueEventListener(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                    System.out.println(dataSnapshot);
//                                    chatActivity.createNotification(chatActivity.receiverPublicUid, chatActivity.receiverPublicUname);
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError error) {
//
//                                }
//                            });

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
        Notification.Builder notification = new Notification.Builder(this,CHANNEL_ID)
                .setContentText("Service is Running...")
                .setContentTitle("Autofill Service")
                .setSmallIcon(R.drawable.keys_privacy)
                .setPriority(Notification.PRIORITY_LOW)
                .setCategory(Notification.CATEGORY_SERVICE);

        startForeground(101,notification.build());
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
