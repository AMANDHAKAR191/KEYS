package com.keys.aman.messages;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.keys.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class AddContactEmailDialogFragment extends DialogFragment {

    TextInputLayout tilAddUser;
    String senderUid;

    public AddContactEmailDialogFragment(String senderUid) {
        this.senderUid = senderUid;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_dialog_add_contact_email, container, false);
        tilAddUser = view.findViewById(R.id.til_add_user);
        tilAddUser.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Checking User");
                String userName = tilAddUser.getEditText().getText().toString();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("messageUserList");
                Query checkUser = reference.orderByChild("publicUid").equalTo(userName);
                checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                System.out.println("User Found");
                                UserListModelClass userListModel = ds.getValue(UserListModelClass.class);
                                String tempUserId = userListModel.getPublicUid();
                                String tempUserName = userListModel.getPublicUname();

                                UserPersonalChatList personalChatList = new UserPersonalChatList(tempUserId, tempUserName,true, ".");
                                System.out.println("senderUid: " + senderUid);
                                reference.child(senderUid).child("userPersonalChatList").child(tempUserId).setValue(personalChatList)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                System.out.println("Changed..");
                                                dismiss();
                                            }
                                        });
                            }
                        } else {
                            tilAddUser.setError("No User Found!");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        return view;
    }
}
