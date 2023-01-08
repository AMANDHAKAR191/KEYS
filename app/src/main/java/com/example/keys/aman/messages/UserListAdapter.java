package com.example.keys.aman.messages;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keys.R;
import com.example.keys.aman.SplashActivity;
import com.example.keys.aman.notes.NoteAdapterForUnpinned;
import com.example.keys.aman.notes.NotesFragment;
import com.example.keys.aman.notes.addnote.AddNoteDataHelperClass;
import com.example.keys.aman.signin_login.LogInActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.myViewHolder> {

    private AddNoteDataHelperClass noteData;
    ArrayList<UserPersonalChatList> dataHolder;
    LogInActivity logInActivity = new LogInActivity();
    public static final String REQUEST_ID = "UserListAdapter";
    Context context;
    Activity activity;
    DatabaseReference reference;
    SharedPreferences sharedPreferences;
    Bundle args;

    public UserListAdapter() {
    }

    public UserListAdapter(ArrayList<UserPersonalChatList> dataHolder, Context context, Activity activity) {
        this.dataHolder = dataHolder;
        this.context = context;
        this.activity = activity;
        sharedPreferences = activity.getSharedPreferences(logInActivity.getSHARED_PREF_ALL_DATA(), MODE_PRIVATE);
    }

    public UserListAdapter(ArrayList<UserPersonalChatList> dataHolder, Context context, Activity activity, Bundle args) {
        this.dataHolder = dataHolder;
        this.context = context;
        this.activity = activity;
        this.args = args;
        sharedPreferences = activity.getSharedPreferences(logInActivity.getSHARED_PREF_ALL_DATA(), MODE_PRIVATE);
        // Retrieve the note data
        noteData = args.getParcelable(NotesFragment.shareNoteCode);
        Log.e("shareNote", "Check4: UserListAdapter: " + noteData);
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

        holder.tvPublicUid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String receiverPublicUid = dataHolder.get(position).getOtherUserPublicUid();
                String senderPublicUid = sharedPreferences.getString(logInActivity.PUBLIC_UID, null);
                reference.child(receiverPublicUid).child("userPersonalChatList").child(senderPublicUid).child("lastMessage").setValue(".")
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(context, "Last Message Removed", Toast.LENGTH_SHORT).show();
                            }
                        });

                SplashActivity.isForeground = true;
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra(logInActivity.REQUEST_CODE_NAME, REQUEST_ID);
                intent.putExtra("receiver_public_uid", dataHolder.get(position).getOtherUserPublicUid());
                intent.putExtra("receiver_public_uname", dataHolder.get(position).getOtherUserPublicUname());
                activity.startActivity(intent);
            }
        });
        holder.tvPublicUid.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                holder.llSelector.setVisibility(View.VISIBLE);
                SplashActivity.isForeground = true;
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra(logInActivity.REQUEST_CODE_NAME, NoteAdapterForUnpinned.REQUEST_ID);
                intent.putExtra("noteData", noteData);
                intent.putExtra("receiver_public_uid", dataHolder.get(position).getOtherUserPublicUid());
                intent.putExtra("receiver_public_uname", dataHolder.get(position).getOtherUserPublicUname());
                activity.startActivity(intent);
                return false;
            }
        });

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
    }
}
