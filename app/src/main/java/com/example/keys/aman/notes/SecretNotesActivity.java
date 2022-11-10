package com.example.keys.aman.notes;

import static com.example.keys.aman.SplashActivity.mRewardedAd;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keys.R;
import com.example.keys.aman.SplashActivity;
import com.example.keys.aman.authentication.BiometricAuthActivity;
import com.example.keys.aman.notes.addnote.AddNoteDataHelperClass;
import com.example.keys.aman.signin_login.LogInActivity;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class SecretNotesActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    TextView tvNote;
    private String uid;
    LogInActivity logInActivity = new LogInActivity();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secret_notes);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);
        sharedPreferences = getSharedPreferences(logInActivity.getSHARED_PREF_ALL_DATA(), MODE_PRIVATE);
        SplashActivity.isForeground = false;

        //Hooks
        tvNote = findViewById(R.id.tv_NOTE);

        uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        Intent intent = getIntent();
        String comingRequestCode = intent.getStringExtra(logInActivity.getREQUEST_CODE_NAME());
        if (comingRequestCode == null){
            comingRequestCode = "this";
        }

        if (comingRequestCode.equals("LogInActivity")) {
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mRewardedAd != null) {
                    Activity activityContext = SecretNotesActivity.this;
                    SplashActivity.isForeground = true;
                    mRewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                        @Override
                        public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                            // Handle the reward.
                            int rewardAmount = rewardItem.getAmount();
                            String rewardType = rewardItem.getType();
                        }
                    });
                } else {
                    Toast.makeText(SecretNotesActivity.this, "The rewarded ad wasn't ready yet.", Toast.LENGTH_SHORT).show();
                }
            }
        },1000);


        recyclerViewSetData();
    }

    public void recyclerViewSetData() {
        RecyclerView recyclerView;
        myadaptorfornote adaptor;
        ArrayList<AddNoteDataHelperClass> dataholder;


        recyclerView = findViewById(R.id.recview);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("notes").child(uid);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dataholder = new ArrayList<>();
        adaptor = new myadaptorfornote(dataholder, getApplicationContext(),this){
            @Override
            public void resetAdaptor(){
                dataholder.clear();
                recyclerViewSetData();
                Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
            }
        };
        recyclerView.setAdapter(adaptor);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        AddNoteDataHelperClass data = ds.getValue(AddNoteDataHelperClass.class);
                        assert data != null;
                        if (data.isHideNote()){
                            dataholder.add(data);
                        }else {

                        }

                    }
                    Collections.sort(dataholder, AddNoteDataHelperClass.addDNoteHelperClassComparator);
                    adaptor.notifyDataSetChanged();
                } else {
                    tvNote.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        recyclerView.setAdapter(adaptor);


    }

    public void goCancel(View view) {
        SplashActivity.isForeground = true;
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (SplashActivity.isBackground){
            Intent intent = new Intent(SecretNotesActivity.this, BiometricAuthActivity.class);
            intent.putExtra(logInActivity.getREQUEST_CODE_NAME(), "LockBackGroundApp");
            startActivity(intent);
        }
        if (SplashActivity.isForeground){
            SplashActivity.isForeground = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!SplashActivity.isForeground){
            SplashActivity.isBackground = true;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SplashActivity.isForeground = true;
        finish();
    }
}