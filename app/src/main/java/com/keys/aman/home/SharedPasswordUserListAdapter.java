package com.keys.aman.home;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.keys.aman.R;
import com.keys.aman.home.addpassword.PasswordHelperClass;
import com.keys.aman.messages.ChatModelClass;
import com.keys.aman.messages.UserPersonalChatList;
import com.keys.aman.notes.addnote.NoteHelperClass;
import com.keys.aman.signin_login.LogInActivity;

import java.util.ArrayList;

public class SharedPasswordUserListAdapter extends RecyclerView.Adapter<SharedPasswordUserListAdapter.myViewHolder> {

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
    private SharedPasswordAdapter adaptorForUsersList;

    public SharedPasswordUserListAdapter() {
    }

    public SharedPasswordUserListAdapter(ArrayList<UserPersonalChatList> dataHolder, Context context, Activity activity) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.password_user_list_layout, parent, false);

        return new myViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull myViewHolder holder, int position) {


        String receiverPublicUid, senderPublicUid, senderRoom;
        senderPublicUid = sharedPreferences.getString(logInActivity.PUBLIC_UID, null);
        receiverPublicUid = dataHolder.get(position).getOtherUserPublicUid();
        System.out.println("receiverPublicUid ==>> " + receiverPublicUid);
        senderRoom = senderPublicUid + receiverPublicUid;
        System.out.println("senderRoom ==>> " + senderRoom);
        reference = FirebaseDatabase.getInstance().getReference().child("messages").child(senderRoom);


        ArrayList<PasswordHelperClass> dataHolderSharedPasswordUsers = new ArrayList<>();

        holder.recyclerView.hasFixedSize();
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    System.out.println("dataSnapshot ==>> " + dataSnapshot);
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        ChatModelClass tempChat = ds.getValue(ChatModelClass.class);
                        if (!tempChat.getPublicUid().equals(senderPublicUid)) {
                            holder.tvPublicUid.setText(dataHolder.get(position).getOtherUserPublicUid());
                            if (tempChat.getPasswordModelClass() != null) {
                                PasswordHelperClass tempPassword = tempChat.getPasswordModelClass();
                                dataHolderSharedPasswordUsers.add(tempPassword);
                                System.out.println("dataHolderSharedPasswordUsers ==>> " + dataHolderSharedPasswordUsers);
                            }
                        }
                    }
                    adaptorForUsersList = new SharedPasswordAdapter(dataHolderSharedPasswordUsers, context, activity, dataHolder.get(position).getCommonEncryptionKey(), dataHolder.get(position).getCommonEncryptionIv());
                    holder.recyclerView.setAdapter(adaptorForUsersList);
                    adaptorForUsersList.notifyDataSetChanged();
                    System.out.println("dataHolderSharedPasswordUsers ==>><< " + dataHolderSharedPasswordUsers);
                } else {
//                    tvNote.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        dataHolderSharedPasswordUsers.clear();
    }

    @Override
    public int getItemCount() {
        return dataHolder.size();
    }


    public class myViewHolder extends RecyclerView.ViewHolder {

        TextView tvPublicUid;
        RecyclerView recyclerView;

        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPublicUid = itemView.findViewById(R.id.tv_public_uid);
            recyclerView = itemView.findViewById(R.id.recview_password_list_user_wise);
        }
    }

}