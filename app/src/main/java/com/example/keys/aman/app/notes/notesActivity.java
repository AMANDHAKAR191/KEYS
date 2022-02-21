package com.example.keys.aman.app.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.keys.R;
import com.example.keys.aman.app.settings.SettingActivity;
import com.example.keys.aman.app.home.HomeActivity;
import com.example.keys.aman.app.signin_login.SignUpActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class notesActivity extends AppCompatActivity {

    private static final String TAG = "notesActivity";
    SharedPreferences sharedPreferences;
    TextView tv_NOTE;
    RewardedAd mRewardedAd;
    private int click_counter = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);
        sharedPreferences = getSharedPreferences(SignUpActivity.SHARED_PREF_ALL_DATA, MODE_PRIVATE);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {

            }
        });

        AdRequest adRequest = new AdRequest.Builder().build();

        RewardedAd.load(this, "ca-app-pub-3940256099942544/5224354917",
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        Log.d(TAG, loadAdError.getMessage());
                        Toast.makeText(notesActivity.this, loadAdError.getMessage(), Toast.LENGTH_LONG).show();
                        mRewardedAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        mRewardedAd = rewardedAd;
                        Toast.makeText(notesActivity.this, "Ad was loaded.", Toast.LENGTH_LONG).show();
                    }
                });


        tv_NOTE = findViewById(R.id.tv_NOTE);
        home_bottom_nav();
        recyclerviewsetdata();
    }

    public void home_bottom_nav() {
        // initialize And Assign Variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        //set Home Selected
        bottomNavigationView.setSelectedItemId(R.id.menu_notes);
        //Perform ItemSelectorListener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_home:
                        Intent intent1 = new Intent(notesActivity.this, HomeActivity.class);
                        startActivity(intent1);
                        overridePendingTransition(R.anim.slide_left_right, R.anim.slide_left_right);
                        return true;
                    case R.id.menu_setting:
                        Intent intent2 = new Intent(notesActivity.this, SettingActivity.class);
                        overridePendingTransition(R.anim.slide_right_left, R.anim.slide_right_left);
                        startActivity(intent2);
                        return true;
                    case R.id.menu_notes:

                        return true;
                }
                return false;
            }
        });
    }

    public void recyclerviewsetdata() {
        RecyclerView recyclerView;
        DatabaseReference databaseReference;
        myadaptorfornote adaptor;
        ArrayList<addDNoteHelperClass> dataholder;


        recyclerView = findViewById(R.id.recview);
        String mobile = sharedPreferences.getString(SignUpActivity.KEY_USER_MOBILE, null);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("notes").child(mobile);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dataholder = new ArrayList<>();
        adaptor = new myadaptorfornote(dataholder, getApplicationContext());
        recyclerView.setAdapter(adaptor);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        addDNoteHelperClass data = ds.getValue(addDNoteHelperClass.class);
                        assert data != null;
                        if (data.isHide_note()){

                        }else {
                            dataholder.add(data);
                        }

                    }
                    Collections.sort(dataholder,addDNoteHelperClass.addDNoteHelperClassComparator);
                    adaptor.notifyDataSetChanged();
                } else {
                    tv_NOTE.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        recyclerView.setAdapter(adaptor);


    }


    public void addNotes(View view) {
        if (mRewardedAd != null) {
            Activity activityContext = notesActivity.this;
            mRewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    // Handle the reward.
                    Log.d(TAG, "The user earned the reward.");
                    int rewardAmount = rewardItem.getAmount();
                    String rewardType = rewardItem.getType();
                }
            });
        } else {
            Toast.makeText(notesActivity.this, "The rewarded ad wasn't ready yet.", Toast.LENGTH_SHORT).show();
        }
        Intent intent = new Intent(notesActivity.this, addNotesActivity.class);
        overridePendingTransition(R.anim.upward,0);
        startActivity(intent);
    }

    public void open_secret_notes(View view) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                click_counter = 0;
            }
        },1000);
        if (click_counter == 2){
            Toast.makeText(notesActivity.this,"Opening Secret Notes", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(notesActivity.this,pinLockFragment.class));
            click_counter = 0;
        }
        click_counter = click_counter + 1;
        Toast.makeText(notesActivity.this,"Click counter: " + click_counter, Toast.LENGTH_SHORT).show();
    }
}