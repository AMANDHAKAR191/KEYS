package com.example.keys.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.keys.R;
import com.example.keys.home.HomeActivity;
import com.example.keys.settings.SettingActivity;
import com.example.keys.signin_login.SignUpActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class secretNotesActivity extends AppCompatActivity {
    private static final String TAG = "notesActivity";
    SharedPreferences sharedPreferences;
    TextView tv_NOTE;
    RewardedAd mRewardedAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secret_notes);
        sharedPreferences = getSharedPreferences(SignUpActivity.SHARED_PREF_ALL_DATA, MODE_PRIVATE);

        tv_NOTE = findViewById(R.id.tv_NOTE);
        recyclerviewsetdata();
    }

    public void recyclerviewsetdata() {
        RecyclerView recyclerView;
        DatabaseReference databaseReference;
        myadaptorfornote adaptor;
        ArrayList<addDNoteHelperClass> dataholder;


        recyclerView = findViewById(R.id.recview);
        String mobile = sharedPreferences.getString(SignUpActivity.KEY_USER_MOBILE, null);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("notes").child(mobile);
        recyclerView.setHasFixedSize(true);
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
                            dataholder.add(data);
                        }else {

                        }

                    }
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

    public void gocencal(View view) {
        finish();
    }
}