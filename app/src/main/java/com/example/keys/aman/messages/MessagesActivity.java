package com.example.keys.aman.messages;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keys.R;
import com.example.keys.aman.signin_login.LogInActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MessagesActivity extends Fragment {

    Context context;
    Activity activity;
    Button btnAddMessage;
//    FloatingActionButton fabAddChat;
    RecyclerView recViewUsersChatList;
    LogInActivity logInActivity = new LogInActivity();
    ArrayList<UserPersonalChatList> dataHolderUserList;
    private SharedPreferences sharedPreferences;
    private DatabaseReference reference;
    private myAdaptorForUserList adaptorForUsersList;
    public String senderPublicUid;


    public MessagesActivity(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    public MessagesActivity() {
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.activity_messages, container, false);
        sharedPreferences = activity.getSharedPreferences(logInActivity.SHARED_PREF_ALL_DATA, MODE_PRIVATE);
        recViewUsersChatList = view.findViewById(R.id.recview_user_chat_list);
//        fabAddChat = view.findViewById(R.id.fab_add_chat);
        senderPublicUid = sharedPreferences.getString(logInActivity.PUBLIC_UID,null);

//        fabAddChat.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                AddContactEmailDialogFragment dialogFragment = new AddContactEmailDialogFragment(senderPublicUid);
//                dialogFragment.show(requireActivity().getSupportFragmentManager(),"add_chat");
//            }
//        });

        recyclerViewSetData();

        return view;
    }

    public void recyclerViewSetData() {
        reference = FirebaseDatabase.getInstance().getReference("messageUserList").child(senderPublicUid).child("userPersonalChatList");

        recViewUsersChatList.setLayoutManager(new LinearLayoutManager(context));

        dataHolderUserList = new ArrayList<>();
        adaptorForUsersList = new myAdaptorForUserList(dataHolderUserList, context, activity);
        recViewUsersChatList.setAdapter(adaptorForUsersList);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
//                    System.out.println(dataSnapshot);
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                        UserListModelClass userListModel = ds.getValue(UserListModelClass.class);
//                        System.out.println(ds);
                        UserPersonalChatList personalChatList = ds.getValue(UserPersonalChatList.class);

                        if (!personalChatList.getOtherUserPublicUid().equals(senderPublicUid)){
//                            System.out.println("userListModel.getPublicUid() " + personalChatList.getOtherUserPublicUid());
//                            System.out.println("userListModel.isKnow() " + personalChatList.isKnowUser());
                            if (personalChatList.isKnowUser()){
                                dataHolderUserList.add(personalChatList);
                                if (!personalChatList.getLastMessage().equals(".")){
//                                    createNotification();
                                }
                            }
                        }
                    }
//                    Collections.sort(dataHolderUserList, AddNoteDataHelperClass.addDNoteHelperClassComparator);
                    adaptorForUsersList.notifyDataSetChanged();
                } else {
//                    tvNote.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        recViewUsersChatList.setAdapter(adaptorForUsersList);

    }
    private void createNotification() {
        ChatActivity chatActivity = new ChatActivity();
        final String CHANNEL_ID = "Foreground Service ID";
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_ID,
                NotificationManager.IMPORTANCE_DEFAULT
        );

        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("receiver_public_uid",chatActivity.receiverPublicUid);
        intent.putExtra("receiver_public_uname", chatActivity.receiverPublicUname);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        activity.getSystemService(NotificationManager.class).createNotificationChannel(channel);
        Notification.Builder notificationBuilder = new Notification.Builder(context,CHANNEL_ID)
                .setContentText("New message")
                .setContentTitle(chatActivity.receiverPublicUid)
                .setSmallIcon(R.drawable.keys_privacy)
                .setPriority(Notification.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setCategory(Notification.CATEGORY_MESSAGE)
                .setAutoCancel(true);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1234,notificationBuilder.build());
    }

}