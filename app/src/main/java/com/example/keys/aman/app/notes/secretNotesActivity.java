package com.example.keys.aman.app.notes;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keys.R;
import com.example.keys.aman.app.signin_login.LogInActivity;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class secretNotesActivity extends AppCompatActivity {
    private static final String TAG = "notesActivity";
    SharedPreferences sharedPreferences;
    TextView tv_NOTE;
    RewardedAd mRewardedAd;
    private String uid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_secret_notes);
        sharedPreferences = getSharedPreferences(LogInActivity.SHARED_PREF_ALL_DATA, MODE_PRIVATE);
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        tv_NOTE = findViewById(R.id.tv_NOTE);
        recyclerviewsetdata();
    }

    public void recyclerviewsetdata() {
        RecyclerView recyclerView;
        DatabaseReference databaseReference;
        myadaptorfornote adaptor;
        ArrayList<addDNoteHelperClass> dataholder;


        recyclerView = findViewById(R.id.recview);
        String mobile = sharedPreferences.getString(LogInActivity.KEY_USER_MOBILE, null);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("notes").child(uid);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dataholder = new ArrayList<>();
        adaptor = new myadaptorfornote(dataholder, getApplicationContext(),this);
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

    public void gocencal(View view) {
        finish();
    }

    public void refrash_activty(View view) {
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }
}