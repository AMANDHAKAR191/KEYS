package com.keys.aman.home;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.keys.aman.R;
import com.keys.aman.messages.UserPersonalChatList;
import com.keys.aman.signin_login.LogInActivity;

import java.util.ArrayList;
import java.util.Objects;


public class SharedPasswordFragment extends Fragment {
    Context context;
    Activity activity;


    public SharedPasswordFragment(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    public SharedPasswordFragment() {
    }

    ProgressBar progressBar;
    ImageButton imgBack;
    TextView tvNOTE;
    RecyclerView recview;
    SharedPreferences sharedPreferences;
    public static SharedPasswordUserListAdapter adaptorForUsersList;
    ArrayList<UserPersonalChatList> dataHolderUserList;

    //    ArrayList<String> parentdataholder;
    String uid;
    LogInActivity logInActivity = new LogInActivity();
    public static final String REQUEST_ID = "HomeFragment";
    private DatabaseReference reference;
    public String senderPublicUid;


    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shared_password, container, false);
        sharedPreferences = activity.getSharedPreferences(logInActivity.SHARED_PREF_ALL_DATA, MODE_PRIVATE);

        //Hooks
        progressBar = view.findViewById(R.id.progressBar);
        recview = view.findViewById(R.id.recview_user_list);
        tvNOTE = view.findViewById(R.id.tv_NOTE);
        progressBar.setVisibility(View.VISIBLE);
        imgBack = view.findViewById(R.id.img_back);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBack();
            }
        });


        uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();


        threadRunnableHomeFragment threadRunnableHomeFragment = new threadRunnableHomeFragment(view);
        new Thread(threadRunnableHomeFragment).start();

        return view;
    }

    public void goBack() {
        getParentFragmentManager().beginTransaction().remove(SharedPasswordFragment.this).commit();
    }

    public class threadRunnableHomeFragment implements Runnable {
        Handler handler = new Handler();
        View view;

        public threadRunnableHomeFragment(View view) {
            this.view = view;
        }

        @Override
        public void run() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    recyclerviewsetdata();
                }
            });
        }
    }


    public void recyclerviewsetdata() {
        sharedPreferences = activity.getSharedPreferences(logInActivity.SHARED_PREF_ALL_DATA, MODE_PRIVATE);
        senderPublicUid = sharedPreferences.getString(logInActivity.PUBLIC_UID, null);
        reference = FirebaseDatabase.getInstance().getReference("messageUserList").child(senderPublicUid).child("userPersonalChatList");

        dataHolderUserList = new ArrayList<>();

        recview.setLayoutManager(new LinearLayoutManager(context));
        recview.hasFixedSize();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        UserPersonalChatList personalChatList = ds.getValue(UserPersonalChatList.class);

                        if (!personalChatList.getOtherUserPublicUid().equals(senderPublicUid)) {
                            if (personalChatList.isKnowUser()) {
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
        adaptorForUsersList = new SharedPasswordUserListAdapter(dataHolderUserList, context, activity);
        recview.setAdapter(adaptorForUsersList);
        adaptorForUsersList.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

}
