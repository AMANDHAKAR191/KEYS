package com.example.keys.aman.messages;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keys.R;
import com.example.keys.aman.SplashActivity;
import com.example.keys.aman.authentication.AppLockCounterClass;
import com.example.keys.aman.base.TabLayoutActivity;
import com.example.keys.aman.notes.NoteAdapterForUnpinned;
import com.example.keys.aman.notes.addnote.AddNoteDataHelperClass;
import com.example.keys.aman.signin_login.LogInActivity;
import com.example.keys.databinding.ActivityChatBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {

    ActivityChatBinding binding;
    public final String RECEIVER_ROOM = "receiver_room";
    public final String RECEIVER_PUBLIC_UID = "receiver_public_uid";
    public final String SENDER_ROOM = "sender_room";
    TextView tvSentMessage, tvReceivedMessage;
    TextInputLayout tilMessage;
    //    ImageButton imgSendMessage;
    RecyclerView recViewChatMessages;
    private ArrayList<ChatModelClass> dataHolderChatMessages;
    public String receiverPublicUid, receiverPublicUname, senderPublicUid, senderPublicUname, currentDateAndTime;
    private SharedPreferences sharedPreferences;
    LogInActivity logInActivity = new LogInActivity();
    TabLayoutActivity tabLayoutActivity = new TabLayoutActivity();
    //todo 2 object calling of AppLockCounterClass
    AppLockCounterClass appLockCounterClass = new AppLockCounterClass(ChatActivity.this, ChatActivity.this);

    ChatAdaptor chatAdaptor;
    public String senderRoom, receiverRoom;
    private AddNoteDataHelperClass noteData;
    private Intent intentResult;
    private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sharedPreferences = getSharedPreferences(logInActivity.getSHARED_PREF_ALL_DATA(), MODE_PRIVATE);
        reference = FirebaseDatabase.getInstance().getReference().child("messageUserList");

        //todo 3 when is coming from background or foreground always make isForeground false
        SplashActivity.isForeground = false;



        intentResult = getIntent();
        String comingFromActivity = intentResult.getStringExtra(logInActivity.REQUEST_CODE_NAME);

        Toast.makeText(this, "comingFromActivity" + comingFromActivity, Toast.LENGTH_SHORT).show();
        if (comingFromActivity.equals(UserListAdapter.REQUEST_ID)){
            receiverPublicUid = intentResult.getStringExtra("receiver_public_uid");
            receiverPublicUname = intentResult.getStringExtra("receiver_public_uname");
        }else if (comingFromActivity.equals(NoteAdapterForUnpinned.REQUEST_ID)){
            receiverPublicUid = intentResult.getStringExtra("receiver_public_uid");
            receiverPublicUname = intentResult.getStringExtra("receiver_public_uname");
            noteData = intentResult.getParcelableExtra("noteData");
            Log.e("shareNote", "Check5: ChatActivity: " +noteData);
            binding.tilMessage.setEnabled(false);
//            binding.tietMessage.setText("Note: " + noteData.getTitle() + "is sharing with " + receiverPublicUname);
        }


        senderPublicUid = sharedPreferences.getString(logInActivity.PUBLIC_UID, null);
        System.out.println(senderPublicUid + "\t" + receiverPublicUid);
        senderPublicUname = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        FirebaseMessaging.getInstance().subscribeToTopic(senderPublicUid);

        senderRoom = senderPublicUid + receiverPublicUid;
        receiverRoom = receiverPublicUid + senderPublicUid;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(RECEIVER_ROOM, receiverRoom);
        editor.putString(SENDER_ROOM, senderRoom);
        editor.putString(RECEIVER_PUBLIC_UID, receiverPublicUid);
        editor.apply();

        System.out.println("senderRoom\t" + senderRoom);
        System.out.println("receiverRoom\t" + receiverRoom);

        binding.tvReceiverPublicUid.setText(receiverPublicUid);
        binding.tvReceiverPublicUname.setText(receiverPublicUname);


        binding.imgSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = binding.tilMessage.getEditText().getText().toString();
                sendMessage(message);

            }
        });
        binding.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo 4 if app is going to another activity make isForeground = true
                SplashActivity.isForeground = true;
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
                        if (dataSnapshot.getKey().equals(receiverRoom)) {
                            System.out.println("Yes");
                        } else {
                            System.out.println("No");
                        }

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
        int lastPosition = dataHolderChatMessages.size() - 1;
        if (lastPosition >= 0 && lastPosition < dataHolderChatMessages.size()) {
            binding.recviewChat.smoothScrollToPosition(lastPosition);
        }

    }

    private void sendMessage(String message) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        currentDateAndTime = sdf.format(new Date());
        System.out.println("Dateandtime: " + currentDateAndTime);

        reference.child(receiverPublicUid).child("userPersonalChatList").child(senderPublicUid).child("lastMessage")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        ChatModelClass chatModelClass = new ChatModelClass(message, currentDateAndTime, senderPublicUid,"text", null, noteData);
        binding.tietMessage.setText("");
        DatabaseReference referenceSender = FirebaseDatabase.getInstance().getReference().child("messages");
        referenceSender.child(senderRoom).push().setValue(chatModelClass)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
//                                setSenderMessageStatus("01");
                        DatabaseReference referenceReceiver = FirebaseDatabase.getInstance().getReference().child("messages");
                        referenceReceiver.child(receiverRoom).push()
                                .setValue(chatModelClass)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {

                                        FCMNotificationSender notificationSender =
                                                new FCMNotificationSender("/topics/"+receiverPublicUid, senderPublicUid, message, ChatActivity.this, ChatActivity.this);
                                        notificationSender.sendNotification();

                                        UserPersonalChatList personalChatList = new UserPersonalChatList(senderPublicUid, senderPublicUname, true, ".");

                                        reference.child(receiverPublicUid).child("userPersonalChatList").child(senderPublicUid).setValue(personalChatList);
//                                                setSenderMessageStatus("11");
                                        // Set last message in userList
                                        String lastMessage;
                                        if (message.length() > 6) {
                                            lastMessage = message.substring(0, 6) + "...";
                                        } else {
                                            lastMessage = message;
                                        }

                                        reference.child(receiverPublicUid).child("userPersonalChatList").child(senderPublicUid).child("lastMessage").setValue(lastMessage)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {

                                                    }
                                                });
                                    }
                                });
                    }
                });
    }

    public void createNotification(String tempReceiverPublicUid, String tempReceiverPublicUname) {
        ChatActivity chatActivity = new ChatActivity();
        final String CHANNEL_ID = "Foreground Service ID";
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_ID,
                NotificationManager.IMPORTANCE_DEFAULT
        );

        Intent intent = new Intent(ChatActivity.this, ChatActivity.class);
        intent.putExtra("receiver_public_uid", chatActivity.receiverPublicUid);
        intent.putExtra("receiver_public_uname", chatActivity.receiverPublicUname);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(ChatActivity.this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        Notification.Builder notificationBuilder = new Notification.Builder(ChatActivity.this, CHANNEL_ID)
                .setContentText("New message")
                .setContentTitle(tempReceiverPublicUid)
                .setSmallIcon(R.drawable.keys_privacy)
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setCategory(Notification.CATEGORY_MESSAGE)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(ChatActivity.this);
        notificationManager.notify(1234, notificationBuilder.build());
    }


    @Override
    protected void onStart() {
        super.onStart();
        //todo 9 onStartOperation, it will check app is
        // coming from foreground or background.
        appLockCounterClass.onStartOperation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //todo 10 onPauseOperation, it will check app is
        // going to foreground or background.
        // if UI component made isForeground = true then it
        // is going to another activity then this method will make
        // isForeground = false, so user will not be verified.
        // if UI component is not clicked then it
        // is going in background then this method will make
        // isBackground = true and timer will started,
        // at time of return, user will be verified.
        appLockCounterClass.checkedItem = sharedPreferences.getInt(tabLayoutActivity.LOCK_APP_OPTIONS, 0);
        appLockCounterClass.onPauseOperation();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //todo 11 do anything
        SplashActivity.isForeground = true;
    }
}