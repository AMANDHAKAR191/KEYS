package com.example.keys.aman.messages;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keys.R;
import com.example.keys.aman.authentication.AppLockCounterClass;
import com.example.keys.aman.signin_login.LogInActivity;
import com.example.keys.databinding.ActivityChatBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {

    ActivityChatBinding binding;
    public final String LOCK_APP_OPTIONS = "lock_app";
    TextView tvSentMessage, tvReceivedMessage;
    TextInputLayout tilMessage;
    //    ImageButton imgSendMessage;
    RecyclerView recViewChatMessages;
    private ArrayList<ChatModelClass> dataHolderChatMessages;
    String receiverPublicUid, receiverPublicUname, senderPublicUid, currentDateAndTime;
    private SharedPreferences sharedPreferences;
    LogInActivity logInActivity = new LogInActivity();
    AppLockCounterClass appLockCounterClass = new AppLockCounterClass(ChatActivity.this, ChatActivity.this);

    ChatAdaptor chatAdaptor;
    String senderRoom, receiverRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sharedPreferences = getSharedPreferences(logInActivity.getSHARED_PREF_ALL_DATA(), MODE_PRIVATE);

        Intent intentResult = getIntent();
        receiverPublicUid = intentResult.getStringExtra("receiver_public_uid");
        receiverPublicUname = intentResult.getStringExtra("receiver_public_uname");
        senderPublicUid = sharedPreferences.getString(logInActivity.PUBLIC_UID, null);
        System.out.println(senderPublicUid + "\t" + receiverPublicUid);

        senderRoom = senderPublicUid + receiverPublicUid;
        receiverRoom = receiverPublicUid + senderPublicUid;
        System.out.println("senderRoom\t" + senderRoom);
        System.out.println("receiverRoom\t" + receiverRoom);

        binding.tvReceiverPublicUid.setText(receiverPublicUid);
        binding.tvReceiverPublicUname.setText(receiverPublicUname);


        binding.imgSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
                currentDateAndTime = sdf.format(new Date());
                System.out.println("Dateandtime: " + currentDateAndTime);

                String message = binding.tilMessage.getEditText().getText().toString();
                ChatModelClass chatModelClass = new ChatModelClass(message, currentDateAndTime, senderPublicUid);
                binding.tietMessage.setText("");
                DatabaseReference referenceSender = FirebaseDatabase.getInstance().getReference().child("messages");
                referenceSender.child(senderRoom).push().setValue(chatModelClass)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                DatabaseReference referenceReceiver = FirebaseDatabase.getInstance().getReference().child("messages");
                                referenceReceiver.child(receiverRoom).push()
                                        .setValue(chatModelClass)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {

                                            }
                                        });
                            }
                        });
            }
        });
        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        dataHolderChatMessages = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.recviewChat.setLayoutManager(linearLayoutManager);
        chatAdaptor = new ChatAdaptor(dataHolderChatMessages, ChatActivity.this, ChatActivity.this);
        binding.recviewChat.setAdapter(chatAdaptor);
        FirebaseDatabase.getInstance().getReference().child("messages")
                .child(senderRoom)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        dataHolderChatMessages.clear();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            ChatModelClass chatModel = ds.getValue(ChatModelClass.class);
                            dataHolderChatMessages.add(chatModel);
                        }
                        chatAdaptor.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
        chatAdaptor.notifyDataSetChanged();

        FirebaseDatabase.getInstance().getReference().child("messages")
                .addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                System.out.println(dataSnapshot.getKey());
                if (dataSnapshot.getKey().equals(receiverRoom)){
                    System.out.println("Yes");
                }else {
                    System.out.println("No");
                }

                final String CHANNEL_ID = "Foreground Service ID";
                NotificationChannel channel = new NotificationChannel(
                        CHANNEL_ID,
                        CHANNEL_ID,
                        NotificationManager.IMPORTANCE_DEFAULT
                );

                Intent intent = new Intent(ChatActivity.this, ChatActivity.class);
                intent.putExtra("receiver_public_uid",receiverPublicUid);
                intent.putExtra("receiver_public_uname", receiverPublicUname);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent pendingIntent = PendingIntent.getActivity(ChatActivity.this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

                getSystemService(NotificationManager.class).createNotificationChannel(channel);
                Notification.Builder notificationBuilder = new Notification.Builder(ChatActivity.this,CHANNEL_ID)
                        .setContentText("New message")
                        .setContentTitle(receiverPublicUname)
                        .setSmallIcon(R.drawable.keys_privacy)
                        .setPriority(Notification.PRIORITY_DEFAULT)
                        .setContentIntent(pendingIntent)
                        .setCategory(Notification.CATEGORY_MESSAGE)
                        .setAutoCancel(true);
                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(ChatActivity.this);
                notificationManager.notify(1234,notificationBuilder.build());
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        appLockCounterClass.onStartOperation();
    }

    @Override
    protected void onStop() {
        super.onStop();
        appLockCounterClass.checkedItem = sharedPreferences.getInt(LOCK_APP_OPTIONS, 0);
        appLockCounterClass.onPauseOperation();
    }
    //    public void recyclerViewSetData() {
//        reference = FirebaseDatabase.getInstance().getReference("notes").child(uid);
//        recViewChatMessages.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
//
//        dataHolderChatMessages = new ArrayList<>();
////        adaptorUnpinned = new myadaptorfornote(dataHolderChatMessages, ChatActivity.this, ChatActivity.this) {
////            @Override
////            public void resetAdaptor() {
////                dataHolderChatMessages.clear();
////                dataHolderPinned.clear();
////                Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
////            }
////
////            @Override
////            public void refreshRecView() {
////                dataHolderChatMessages.clear();
////                dataHolderPinned.clear();
////            }
////        };
//        recViewChatMessages.setAdapter(adaptorUnpinned);
//        reference.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for(DataSnapshot ds : dataSnapshot.getChildren()){
//                    ChatHelperClass chatHelperObject = ds.getValue(ChatHelperClass.class);
//                    dataHolderChatMessages.add(chatHelperObject);
//                    tvReceivedMessage.setText(chatHelperObject.getMessage());
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//        recViewChatMessages.setAdapter(adaptorUnpinned);
//
//    }
}