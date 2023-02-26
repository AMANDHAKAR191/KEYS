package com.keys.aman.messages;

import android.content.DialogInterface;
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
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.keys.aman.AES;
import com.keys.aman.R;
import com.keys.aman.SplashActivity;
import com.keys.aman.authentication.AppLockCounterClass;
import com.keys.aman.base.TabLayoutActivity;
import com.keys.aman.databinding.ActivityChatBinding;
import com.keys.aman.home.PasswordAdapter;
import com.keys.aman.home.addpassword.PasswordHelperClass;
import com.keys.aman.notes.NoteAdapterForUnpinned;
import com.keys.aman.notes.addnote.NoteHelperClass;
import com.keys.aman.signin_login.LogInActivity;

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
    public String receiverPublicUid, receiverPublicUname, commonEncryptionKey, commonEncryptionIv, senderPublicUid, senderPublicUname, currentDateAndTime;
    private SharedPreferences sharedPreferences;
    LogInActivity logInActivity = new LogInActivity();
    TabLayoutActivity tabLayoutActivity = new TabLayoutActivity();
    //todo 2 object calling of AppLockCounterClass
    AppLockCounterClass appLockCounterClass = new AppLockCounterClass(ChatActivity.this, ChatActivity.this);

    ChatAdaptor chatAdaptor;
    public String senderRoom, receiverRoom;
    private NoteHelperClass noteData;
    private PasswordHelperClass passwordData;
    private Intent intentResult;
    private DatabaseReference reference;
    private MutableLiveData<NoteHelperClass> data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sharedPreferences = getSharedPreferences(logInActivity.SHARED_PREF_ALL_DATA, MODE_PRIVATE);
        reference = FirebaseDatabase.getInstance().getReference().child("messageUserList");

        recViewChatMessages = findViewById(R.id.recview_chat);

        //todo 3 when is coming from background or foreground always make isForeground false
        SplashActivity.isForeground = false;


        intentResult = getIntent();
        String comingFromActivity = intentResult.getStringExtra(logInActivity.REQUEST_CODE_NAME);

        Toast.makeText(this, "comingFromActivity" + comingFromActivity, Toast.LENGTH_SHORT).show();
        if (comingFromActivity.equals(UserListAdapter.REQUEST_ID)) {
            receiverPublicUid = intentResult.getStringExtra("receiver_public_uid");
            receiverPublicUname = intentResult.getStringExtra("receiver_public_uname");
            commonEncryptionKey = intentResult.getStringExtra("commonEncryptionKey");
            commonEncryptionIv = intentResult.getStringExtra("commonEncryptionIv");
        } else if (comingFromActivity.equals(NoteAdapterForUnpinned.REQUEST_ID)) {
            receiverPublicUid = intentResult.getStringExtra("receiver_public_uid");
            receiverPublicUname = intentResult.getStringExtra("receiver_public_uname");
            commonEncryptionKey = intentResult.getStringExtra("commonEncryptionKey");
            commonEncryptionIv = intentResult.getStringExtra("commonEncryptionIv");
            noteData = intentResult.getParcelableExtra("noteData");
            Log.e("shareNote", "Check5: ChatActivity: " + noteData);
            binding.tilMessage.setEnabled(false);
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Alert!")
                    .setIcon(R.drawable.app_info)
                    .setMessage("Your Note will be shared with " + receiverPublicUname)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            sendMessage(null, "note");
                        }
                    })
                    .setPositiveButtonIcon(getDrawable(R.drawable.send))
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).show();
//            binding.tietMessage.setText("Note: " + noteData.getTitle() + "is sharing with " + receiverPublicUname);
        } else if (comingFromActivity.equals(PasswordAdapter.REQUEST_ID)) {
            receiverPublicUid = intentResult.getStringExtra("receiver_public_uid");
            receiverPublicUname = intentResult.getStringExtra("receiver_public_uname");
            commonEncryptionKey = intentResult.getStringExtra("commonEncryptionKey");
            commonEncryptionIv = intentResult.getStringExtra("commonEncryptionIv");
            passwordData = intentResult.getParcelableExtra("passwordData");
            Log.e("shareNote", "Check5: ChatActivity: " + passwordData);
            binding.tilMessage.setEnabled(false);
            new MaterialAlertDialogBuilder(this)
                    .setTitle("Alert!")
                    .setIcon(R.drawable.app_info)
                    .setMessage("Your Password will be shared with " + receiverPublicUname)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            sendMessage(null, "password");
                        }
                    })
                    .setPositiveButtonIcon(getDrawable(R.drawable.send))
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).show();
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

        // Observe the object in the view model and update the UI when it changes
        data = new MutableLiveData<>();

        data.observe(this, value -> {
            // Update UI
            Log.e("shareNote", "Check: value: ChatActivity: " + value.getNote());
        });


        binding.imgSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = binding.tilMessage.getEditText().getText().toString();
                sendMessage(message, "text");

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
        chatAdaptor = new ChatAdaptor(dataHolderChatMessages,commonEncryptionKey, commonEncryptionIv, ChatActivity.this, ChatActivity.this);

        binding.recviewChat.setAdapter(chatAdaptor);

        FirebaseDatabase.getInstance().getReference().child("messages")
                .child(senderRoom)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        dataHolderChatMessages.clear();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            System.out.println(ds);
                            ChatModelClass chatModel = ds.getValue(ChatModelClass.class);
                            if (chatModel.getType().equals("note")) {
                                System.out.println(chatModel.getNoteModelClass().getTitle());
                                System.out.println(chatModel.getNoteModelClass().getNote());
                            }
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

    private void sendMessage(String message, String type) {
        AES aes = new AES();
        aes.initFromStrings(commonEncryptionKey, commonEncryptionIv);
        if (passwordData != null) {
            String encryptedAddlLogin = "", encryptedAddPassword = "";
            String addLogin = passwordData.getAddDataLogin();
            String addPassword = passwordData.getAddDataPassword();
            String addWebsiteName = passwordData.getAddWebsite_name();
            String addWebsiteLink = passwordData.getAddWebsite_link();


            try {
                // Double encryption
                // TODO : in future, (if needed) give two key to user for double encryption
                encryptedAddlLogin = aes.encrypt(addLogin);
                encryptedAddlLogin = aes.encrypt(encryptedAddlLogin);
                encryptedAddPassword = aes.encrypt(addPassword);
                encryptedAddPassword = aes.encrypt(encryptedAddPassword);
//                e_addwebsite = aes.encrypt(addwesitename);
                passwordData.setAddDataLogin(encryptedAddlLogin);
                passwordData.setAddDataPassword(encryptedAddPassword);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (noteData != null) {
            String currentDateAndTime, title, note, titleEncrypted, noteEncrypted;
            title = noteData.getTitle();
            note = noteData.getNote();
            try {
                // Double encryption
                // TODO : in future, (if needed) give two key to user for double encryption
                titleEncrypted = aes.encrypt(title);
                titleEncrypted = aes.encrypt(titleEncrypted);
                noteEncrypted = aes.encrypt(note);
                noteEncrypted = aes.encrypt(noteEncrypted);

                noteData.setTitle(titleEncrypted);
                noteData.setNote(noteEncrypted);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        currentDateAndTime = sdf.format(new Date());
        System.out.println("Dateandtime: " + currentDateAndTime);


        ChatModelClass chatModelClass = new ChatModelClass(message, currentDateAndTime, senderPublicUid, type, "sent", passwordData, noteData);
        binding.tietMessage.setText("");
        DatabaseReference referenceSender = FirebaseDatabase.getInstance().getReference().child("messages").child(senderRoom);
        referenceSender.push().setValue(chatModelClass)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
//                                setSenderMessageStatus(referenceSender, "sending");
                        DatabaseReference referenceReceiver = FirebaseDatabase.getInstance().getReference().child("messages");
                        referenceReceiver.child(receiverRoom).push().setValue(chatModelClass)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        String lastMessage;
                                        FCMNotificationSender notificationSender =
                                                new FCMNotificationSender("/topics/" + receiverPublicUid, senderPublicUid, message, ChatActivity.this, ChatActivity.this);
                                        notificationSender.sendNotification();

                                        UserPersonalChatList personalChatList = new UserPersonalChatList(senderPublicUid, senderPublicUname, commonEncryptionKey, commonEncryptionIv, true, ".");

                                        reference.child(receiverPublicUid).child("userPersonalChatList").child(senderPublicUid).setValue(personalChatList);
//                                                setSenderMessageStatus(referenceSender,"sent");


                                        // Set last message in userList
                                        lastMessage = message;


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

    private void setSenderMessageStatus(DatabaseReference reference, String statusMessage) {
//        reference.child().child("status");


//        // Get the RecyclerView and its adapter
//
//        RecyclerView.Adapter adapter = recViewChatMessages.getAdapter();
//
//        if (adapter != null) {
//            // Get the last item in the list
//            int lastItemIndex = adapter.getItemCount() - 1;
//            ChatModelClass lastItem = dataHolderChatMessages.get(dataHolderChatMessages.size() - 1);
//
//            // Set the status message for the last item
//            System.out.println("Setting status...");
//            lastItem.setStatus(statusMessage);
//
//            // Update the UI for the last item
//            adapter.notifyItemChanged(lastItemIndex);
//        }
    }


//    public void createNotification(String tempReceiverPublicUid, String tempReceiverPublicUname) {
//        ChatActivity chatActivity = new ChatActivity();
//        final String CHANNEL_ID = "Foreground Service ID";
//        NotificationChannel channel = new NotificationChannel(
//                CHANNEL_ID,
//                CHANNEL_ID,
//                NotificationManager.IMPORTANCE_DEFAULT
//        );
//
//        Intent intent = new Intent(ChatActivity.this, ChatActivity.class);
//        intent.putExtra("receiver_public_uid", chatActivity.receiverPublicUid);
//        intent.putExtra("receiver_public_uname", chatActivity.receiverPublicUname);
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        PendingIntent pendingIntent = PendingIntent.getActivity(ChatActivity.this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
//
//        getSystemService(NotificationManager.class).createNotificationChannel(channel);
//        Notification.Builder notificationBuilder = new Notification.Builder(ChatActivity.this, CHANNEL_ID)
//                .setContentText("New message")
//                .setContentTitle(tempReceiverPublicUid)
//                .setSmallIcon(R.drawable.keys_privacy)
//                .setPriority(Notification.PRIORITY_DEFAULT)
//                .setContentIntent(pendingIntent)
//                .setCategory(Notification.CATEGORY_MESSAGE)
//                .setAutoCancel(true);
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(ChatActivity.this);
//        notificationManager.notify(1234, notificationBuilder.build());
//    }


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