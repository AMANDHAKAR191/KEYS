package com.keys.aman.messages;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.keys.aman.R;
import com.keys.aman.SplashActivity;
import com.keys.aman.home.addpassword.PasswordHelperClass;
import com.keys.aman.notes.addnote.NoteHelperClass;
import com.keys.aman.signin_login.LogInActivity;

import java.util.ArrayList;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.myViewHolder> {

    private NoteHelperClass noteData;
    final int SELECTED_ITEM_MESSAGE = 0;
    final int SELECTED_ITEM_NOTE = 1;
    final int SELECTED_ITEM_PASSWORD = 2;
    private PasswordHelperClass passwordData;
    ArrayList<UserPersonalChatList> dataHolder;
    LogInActivity logInActivity = new LogInActivity();
    public static final String REQUEST_ID = "UserListAdapter";
    Context context;
    Activity activity;
    DatabaseReference reference;
    SharedPreferences sharedPreferences;

    public UserListAdapter() {
    }

//    public UserListAdapter(ArrayList<UserPersonalChatList> dataHolder, Context context, Activity activity) {
//        this.dataHolder = dataHolder;
//        this.context = context;
//        this.activity = activity;
//        sharedPreferences = activity.getSharedPreferences(logInActivity.SHARED_PREF_ALL_DATA, MODE_PRIVATE);
//    }

    public UserListAdapter(ArrayList<UserPersonalChatList> dataHolder, Context context, Activity activity, PasswordHelperClass passwordData, NoteHelperClass noteData) {
        this.dataHolder = dataHolder;
        this.context = context;
        this.activity = activity;
        sharedPreferences = activity.getSharedPreferences(logInActivity.SHARED_PREF_ALL_DATA, MODE_PRIVATE);
        System.out.println("noteData + " + noteData + " || passwordData + " + passwordData);
        // retrieve the password data
        this.passwordData = passwordData;
        // Retrieve the note data
        this.noteData = noteData;
    }

    @NonNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_layout, parent, false);

        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {

        holder.tvPublicUid.setText(dataHolder.get(position).getOtherUserPublicUid());
        holder.tvPublicUname.setText(dataHolder.get(position).getOtherUserPublicUname());
        holder.tvLastMessage.setText(dataHolder.get(position).getLastMessage());
        reference = FirebaseDatabase.getInstance().getReference().child("messageUserList");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("UserListAdapter : noteData" + noteData);
                System.out.println("UserListAdapter : passwordData" + passwordData);
                if (noteData != null) {
                    holder.onItemSelectedCall(position, SELECTED_ITEM_NOTE);
                    holder.llSelector.setVisibility(View.VISIBLE);
                    SplashActivity.isForeground = true;
                } else if (passwordData != null) {
                    holder.onItemSelectedCall(position, SELECTED_ITEM_PASSWORD);
                    holder.llSelector.setVisibility(View.VISIBLE);
                    SplashActivity.isForeground = true;
                } else {
                    holder.onItemSelectedCall(position, SELECTED_ITEM_MESSAGE);
                    holder.llSelector.setVisibility(View.VISIBLE);
                    SplashActivity.isForeground = true;
                }


//                String senderPublicUid = sharedPreferences.getString(logInActivity.PUBLIC_UID, null);
//
//
//                SplashActivity.isForeground = true;
//                Intent intent = new Intent(context, ChatActivity.class);
//                intent.putExtra(logInActivity.REQUEST_CODE_NAME, REQUEST_ID);
//                intent.putExtra("receiver_public_uid", dataHolder.get(position).getOtherUserPublicUid());
//                intent.putExtra("receiver_public_uname", dataHolder.get(position).getOtherUserPublicUname());
//                activity.startActivity(intent);
            }
        });
//        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//                holder.onItemSelectedCall(position);
//                holder.llSelector.setVisibility(View.VISIBLE);
//                SplashActivity.isForeground = true;
//                return false;
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return dataHolder.size();
    }


    public class myViewHolder extends RecyclerView.ViewHolder {

        TextView tvPublicUid, tvPublicUname, tvLastMessage;
        LinearLayout llSelector;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPublicUid = itemView.findViewById(R.id.tv_public_uid);
            tvPublicUname = itemView.findViewById(R.id.tv_public_uname);
            tvLastMessage = itemView.findViewById(R.id.tv_last_message);
            llSelector = itemView.findViewById(R.id.ll_selector);
        }

        public void onItemSelectedCall(int position, int selectedItemType) {
            switch (selectedItemType){
                case SELECTED_ITEM_MESSAGE:
                    onItemSelected(position);
                    break;
                case SELECTED_ITEM_NOTE:
                    onNoteSelected(position);
                    break;
                case SELECTED_ITEM_PASSWORD:
                    onPasswordSelected(position);
                    break;
            }

        }
    }

    //
    public void onItemSelected(int position) {}
    public void onNoteSelected(int position) {}
    public void onPasswordSelected(int position) {}
}
