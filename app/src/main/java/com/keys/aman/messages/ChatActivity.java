package com.keys.aman.messages;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.keys.aman.AES;
import com.keys.aman.MyNoteViewModel;
import com.keys.aman.MyPasswordViewModel;
import com.keys.aman.data.MyPreference;
import com.keys.aman.R;
import com.keys.aman.SplashActivity;
import com.keys.aman.authentication.AppLockCounterClass;
import com.keys.aman.base.TabLayoutActivity;
import com.keys.aman.data.Firebase;
import com.keys.aman.data.iFirebaseDAO;
import com.keys.aman.databinding.ActivityChatBinding;
import com.keys.aman.home.PasswordAdapter;
import com.keys.aman.home.addpassword.PasswordHelperClass;
import com.keys.aman.iAES;
import com.keys.aman.notes.NoteAdapter;
import com.keys.aman.notes.addnote.NoteHelperClass;
import com.keys.aman.signin_login.LogInActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {

    public String receiverPublicUid, receiverPublicUname, commonEncryptionKey, commonEncryptionIv, senderPublicUid, senderPublicUname, currentDateAndTime;
    public String senderRoom, receiverRoom;
    ActivityChatBinding binding;
    Activity activity;
    Context context;
    TextView tvSentMessage, tvReceivedMessage;
    TextInputLayout tilMessage;
    //    ImageButton imgSendMessage;
    RecyclerView recViewChatMessages;
    LogInActivity logInActivity = new LogInActivity();
    TabLayoutActivity tabLayoutActivity = new TabLayoutActivity();
    //todo 2 object calling of AppLockCounterClass
    AppLockCounterClass appLockCounterClass = new AppLockCounterClass(ChatActivity.this, ChatActivity.this);

    ChatAdaptor chatAdaptor;
    MyPreference myPreference;
    private ArrayList<ChatModelClass> dataHolderChatMessages;
    private NoteHelperClass noteData;
    private PasswordHelperClass passwordData;
    private Intent intentResult;
    private DatabaseReference reference;
    private MutableLiveData<NoteHelperClass> data;
    private MyNoteViewModel viewNoteModel;
    private MyPasswordViewModel viewPasswordModel;
    private iAES iAES;
    private iFirebaseDAO iFirebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = ChatActivity.this;
        context = ChatActivity.this;
        myPreference = MyPreference.getInstance(this);
        reference = FirebaseDatabase.getInstance().getReference().child("messageUserList");
        viewNoteModel = new ViewModelProvider(this).get(MyNoteViewModel.class);
        viewPasswordModel = new ViewModelProvider(this).get(MyPasswordViewModel.class);
        iFirebase = Firebase.getInstance(ChatActivity.this);

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
            //initialize
            System.out.println("commonEncryptionKey: " + commonEncryptionKey);
            System.out.println("commonEncryptionIv: " + commonEncryptionIv);
            iAES = AES.getInstance(commonEncryptionKey, commonEncryptionIv);
        } else if (comingFromActivity.equals(NoteAdapter.REQUEST_ID)) {
            receiverPublicUid = intentResult.getStringExtra("receiver_public_uid");
            receiverPublicUname = intentResult.getStringExtra("receiver_public_uname");
            commonEncryptionKey = intentResult.getStringExtra("commonEncryptionKey");
            commonEncryptionIv = intentResult.getStringExtra("commonEncryptionIv");
            noteData = intentResult.getParcelableExtra("noteData");
            //initialize
            System.out.println("commonEncryptionKey: " + commonEncryptionKey);
            System.out.println("commonEncryptionIv: " + commonEncryptionIv);
            iAES = AES.getInstance(commonEncryptionKey, commonEncryptionIv);
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
            //initialize
            System.out.println("commonEncryptionKey: " + commonEncryptionKey);
            System.out.println("commonEncryptionIv: " + commonEncryptionIv);
            iAES = AES.getInstance(commonEncryptionKey, commonEncryptionIv);
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
//        //initialize
//        System.out.println("commonEncryptionKey: " + commonEncryptionKey);
//        System.out.println("commonEncryptionIv: " + commonEncryptionIv);
//        iAES = AES.getInstance(commonEncryptionKey, commonEncryptionIv);


        senderPublicUid = myPreference.getPublicUid();
        System.out.println(senderPublicUid + "\t" + receiverPublicUid);
        senderPublicUname = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        FirebaseMessaging.getInstance().subscribeToTopic(senderPublicUid);

        senderRoom = senderPublicUid + receiverPublicUid;
        receiverRoom = receiverPublicUid + senderPublicUid;

        myPreference.setReceiverRoomId(receiverRoom);
        myPreference.setReceiverPublicId(receiverPublicUid);
        myPreference.setSenderRoomId(senderRoom);

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
                Intent intent = new Intent(ChatActivity.this, TabLayoutActivity.class);
                intent.putExtra(logInActivity.REQUEST_CODE_NAME, LogInActivity.REQUEST_ID);
                startActivity(intent);
                finish();
            }
        });

        dataHolderChatMessages = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatActivity.this);

        binding.recviewChat.setLayoutManager(linearLayoutManager);


        iFirebase.loadChatMessages(senderRoom, new Firebase.iLoadChatMessagesCallback() {
            @Override
            public void onChatMessagesLoaded(ArrayList<ChatModelClass> dataHolderChatMessage) {
                dataHolderChatMessages = dataHolderChatMessage;
                chatAdaptor = new ChatAdaptor(dataHolderChatMessages, commonEncryptionKey, commonEncryptionIv, ChatActivity.this, ChatActivity.this);

                binding.recviewChat.setAdapter(chatAdaptor);
                chatAdaptor.notifyDataSetChanged();

                int lastPosition = dataHolderChatMessages.size() - 1;
                if (lastPosition >= 0 && lastPosition < dataHolderChatMessages.size()) {
                    binding.recviewChat.smoothScrollToPosition(lastPosition);
                }
            }
        });
    }

    private void sendMessage(String message, String type) {
        if (passwordData != null) {
            String encryptedAddlLogin = "", encryptedAddPassword = "";
            String addLogin = passwordData.getAddDataLogin();
            String addPassword = passwordData.getAddDataPassword();
            String addWebsiteName = passwordData.getAddWebsite_name();
            String addWebsiteLink = passwordData.getAddWebsite_link();
            encryptedAddlLogin = iAES.doubleEncryption(addLogin);
            encryptedAddPassword = iAES.doubleEncryption(addPassword);
            passwordData.setAddDataLogin(encryptedAddlLogin);
            passwordData.setAddDataPassword(encryptedAddPassword);
        } else if (noteData != null) {
            String currentDateAndTime, title, note, titleEncrypted, noteEncrypted;
            title = noteData.getTitle();
            note = noteData.getNote();
            titleEncrypted = iAES.doubleEncryption(title);
            noteEncrypted = iAES.doubleEncryption(note);
            noteData.setTitle(titleEncrypted);
            noteData.setNote(noteEncrypted);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        currentDateAndTime = sdf.format(new Date());
        String decryptedMessage = "";
        if (message != null){
            decryptedMessage = iAES.singleEncryption(message);
        }


        ChatModelClass chatModelClass = new ChatModelClass(decryptedMessage, currentDateAndTime, senderPublicUid, type, "sent", passwordData, noteData);
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
        appLockCounterClass.checkedItem = myPreference.getLockAppSelectedOption();
        appLockCounterClass.onPauseOperation();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //todo 11 do anything
        SplashActivity.isForeground = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}