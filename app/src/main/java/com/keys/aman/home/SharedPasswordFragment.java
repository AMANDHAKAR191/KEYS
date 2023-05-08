package com.keys.aman.home;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import com.keys.aman.data.MyPreference;
import com.keys.aman.messages.UserPersonalChatList;
import com.keys.aman.signin_login.LogInActivity;

import java.util.ArrayList;
import java.util.Objects;


public class SharedPasswordFragment extends Fragment {
    public static final String REQUEST_ID = "HomeFragment";
    public static SharedPasswordUserListAdapter adaptorForUsersList;
    public String senderPublicUid;
    Context context;
    Activity activity;
    ProgressBar progressBar;
    ImageButton imgBack;
    TextView tvNOTE, textViewTitleSharedPassword;
    RecyclerView recview;
    SharedPreferences sharedPreferences;
    ArrayList<UserPersonalChatList> dataHolderUserList;

    //    ArrayList<String> parentdataholder;
    String uid;
    LogInActivity logInActivity = new LogInActivity();
    MyPreference myPreference;
    private DatabaseReference reference;
    public SharedPasswordFragment(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }
    public SharedPasswordFragment() {
    }

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_shared_password, container, false);
        myPreference = MyPreference.getInstance(context);

        //Hooks
        progressBar = view.findViewById(R.id.progressBar);
        recview = view.findViewById(R.id.recview_user_list);
        tvNOTE = view.findViewById(R.id.tv_NOTE);
        progressBar.setVisibility(View.VISIBLE);
        imgBack = view.findViewById(R.id.img_back);
        textViewTitleSharedPassword = view.findViewById(R.id.tv_title_shared_pass);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBack();
            }
        });

        // Add touch listener to title view
        textViewTitleSharedPassword.setOnTouchListener(new View.OnTouchListener() {
            private int screenHeight;
            private float initialY;
            private boolean isDragging = false;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        initialY = event.getRawY();
                        isDragging = false;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float currentY = event.getRawY();
                        float deltaY = currentY - initialY;
                        if (deltaY > 50) {
                            // User has dragged down the title
                            isDragging = true;
                            // Animate rootView to bottom of screen
                            int duration = getResources().getInteger(android.R.integer.config_shortAnimTime);
                            ValueAnimator animator = ValueAnimator.ofInt(view.getTop(), screenHeight);
                            animator.setDuration(duration);
                            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator animation) {
                                    int animatedValue = (int) animation.getAnimatedValue();
                                    view.layout(view.getLeft(), animatedValue, view.getRight(), animatedValue + view.getHeight());
                                }
                            });
                            animator.addListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    goBack();
                                }
                            });
                            animator.start();
                        }
                        break;
                }
                return isDragging;
            }
        });



        uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        recyclerViewSetData();
        return view;
    }

    public void goBack() {
        getParentFragmentManager().beginTransaction().remove(SharedPasswordFragment.this).commit();
    }


    public void recyclerViewSetData() {
        senderPublicUid = myPreference.getPublicUid();
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
