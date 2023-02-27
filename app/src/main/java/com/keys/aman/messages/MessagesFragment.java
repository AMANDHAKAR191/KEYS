package com.keys.aman.messages;

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
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.keys.aman.MyNoteViewModel;
import com.keys.aman.MyPasswordViewModel;
import com.keys.aman.R;
import com.keys.aman.home.PasswordAdapter;
import com.keys.aman.home.addpassword.PasswordHelperClass;
import com.keys.aman.notes.NoteAdapterForUnpinned;
import com.keys.aman.notes.addnote.NoteHelperClass;
import com.keys.aman.signin_login.LogInActivity;

import java.util.ArrayList;

public class MessagesFragment extends Fragment {

    Context context;
    Activity activity;
    ProgressBar progressBar;
    Button btnAddMessage;
//    FloatingActionButton fabAddChat;
    RecyclerView recViewUsersChatList;
    LogInActivity logInActivity = new LogInActivity();
    ArrayList<UserPersonalChatList> dataHolderUserList;
    private SharedPreferences sharedPreferences;
    private DatabaseReference reference;
    private UserListAdapter adaptorForUsersList;
    public String senderPublicUid;
    Bundle args;
    public static final String REQUEST_ID = "MessagesFragment";
    private MyNoteViewModel viewNoteModel;
    private MyPasswordViewModel viewPasswordModel;
    private NoteHelperClass noteData;
    private PasswordHelperClass passwordData;


    public MessagesFragment(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    public MessagesFragment() {
    }

    public MessagesFragment(Context context, Activity activity, Bundle data) {
        this.context = context;
        this.activity = activity;
        this.args = data;
    }

    public static MessagesFragment newInstance() {
        return new MessagesFragment();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.activity_messages, container, false);
        sharedPreferences = activity.getSharedPreferences(logInActivity.SHARED_PREF_ALL_DATA, MODE_PRIVATE);
        viewNoteModel = new ViewModelProvider(requireActivity()).get(MyNoteViewModel.class);
        viewPasswordModel = new ViewModelProvider(requireActivity()).get(MyPasswordViewModel.class);

        recViewUsersChatList = view.findViewById(R.id.recview_user_chat_list);
        progressBar = view.findViewById(R.id.progressBar);
//        fabAddChat = view.findViewById(R.id.fab_add_chat);
        senderPublicUid = sharedPreferences.getString(logInActivity.PUBLIC_UID,null);
        progressBar.setVisibility(View.VISIBLE);

//        fabAddChat.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                AddContactEmailDialogFragment dialogFragment = new AddContactEmailDialogFragment(senderPublicUid);
//                dialogFragment.show(requireActivity().getSupportFragmentManager(),"add_chat");
//            }
//        });

        // Observe the object in the view model and update the UI when it changes
        viewNoteModel.getNoteData().observe(getViewLifecycleOwner(), note -> {
            // Update UI
            noteData = note;
            recyclerViewSetData(noteData, passwordData);
        });
        // Observe the object in the view model and update the UI when it changes
        viewPasswordModel.getPasswordData().observe(getViewLifecycleOwner(), password -> {
            // Update UI
            passwordData = password;
            recyclerViewSetData(noteData, passwordData);
        });
        recyclerViewSetData(noteData, passwordData);

        return view;
    }

    public void recyclerViewSetData(NoteHelperClass noteData, PasswordHelperClass passwordData) {
        System.out.println("recyclerViewSetData");
        reference = FirebaseDatabase.getInstance().getReference("messageUserList").child(senderPublicUid).child("userPersonalChatList");

        recViewUsersChatList.setLayoutManager(new LinearLayoutManager(context));

        dataHolderUserList = new ArrayList<>();

        Intent intentResult = activity.getIntent();
//        Bundle args = getArguments();
        String comingFromActivity = intentResult.getStringExtra(logInActivity.REQUEST_CODE_NAME);

//        Log.e("shareNote", "Check3: MessagesFragment: comingFromActivity" +comingFromActivity);
//        if (comingFromActivity.equals(NoteAdapterForUnpinned.REQUEST_ID)){
//            Log.e("shareNote", "Check3: MessagesFragment: " + args.getParcelable(NotesFragment.shareNoteCode));
//            adaptorForUsersList = new UserListAdapter(dataHolderUserList, context, activity, args);
//        }else {
        System.out.println("noteData + " + noteData + " || passwordData + " + passwordData);
            adaptorForUsersList = new UserListAdapter(dataHolderUserList, context, activity ,passwordData,  noteData){
                @Override
                public void onItemSelected(int position) {
                    super.onItemSelected(position);
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra(logInActivity.REQUEST_CODE_NAME, UserListAdapter.REQUEST_ID);
                    intent.putExtra("noteData", noteData);
                    intent.putExtra("receiver_public_uid", dataHolder.get(position).getOtherUserPublicUid());
                    intent.putExtra("receiver_public_uname", dataHolder.get(position).getOtherUserPublicUname());
                    intent.putExtra("commonEncryptionKey", dataHolder.get(position).getCommonEncryptionKey());
                    intent.putExtra("commonEncryptionIv", dataHolder.get(position).getCommonEncryptionIv());
                    activity.startActivity(intent);
                    activity.finish();
                }

                @Override
                public void onNoteSelected(int position) {
                    super.onNoteSelected(position);
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra(logInActivity.REQUEST_CODE_NAME, NoteAdapterForUnpinned.REQUEST_ID);
                    intent.putExtra("noteData", noteData);
                    intent.putExtra("receiver_public_uid", dataHolder.get(position).getOtherUserPublicUid());
                    intent.putExtra("receiver_public_uname", dataHolder.get(position).getOtherUserPublicUname());
                    intent.putExtra("commonEncryptionKey", dataHolder.get(position).getCommonEncryptionKey());
                    intent.putExtra("commonEncryptionIv", dataHolder.get(position).getCommonEncryptionIv());
                    activity.startActivity(intent);
                    activity.finish();
                }

                @Override
                public void onPasswordSelected(int position) {
                    super.onPasswordSelected(position);
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra(logInActivity.REQUEST_CODE_NAME, PasswordAdapter.REQUEST_ID);
                    intent.putExtra("passwordData", passwordData);
                    intent.putExtra("receiver_public_uid", dataHolder.get(position).getOtherUserPublicUid());
                    intent.putExtra("receiver_public_uname", dataHolder.get(position).getOtherUserPublicUname());
                    intent.putExtra("commonEncryptionKey", dataHolder.get(position).getCommonEncryptionKey());
                    intent.putExtra("commonEncryptionIv", dataHolder.get(position).getCommonEncryptionIv());
                    activity.startActivity(intent);
                    activity.finish();
                }
            };
//        }
        recViewUsersChatList.setAdapter(adaptorForUsersList);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        UserPersonalChatList personalChatList = ds.getValue(UserPersonalChatList.class);

                        if (!personalChatList.getOtherUserPublicUid().equals(senderPublicUid)){
                            if (personalChatList.isKnowUser()){
                                dataHolderUserList.add(personalChatList);
//                                if (!personalChatList.getLastMessage().equals(".")){
////                                    createNotification();
//                                }
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
                System.out.println(error.getMessage());
                System.out.println(error.getCode());
                System.out.println(error.getDetails());
            }
        });
        progressBar.setVisibility(View.INVISIBLE);
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