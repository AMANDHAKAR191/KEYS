package com.keys.aman.notes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.keys.aman.MyPreference;
import com.keys.aman.R;
import com.keys.aman.SplashActivity;
import com.keys.aman.authentication.AppLockCounterClass;
import com.keys.aman.base.TabLayoutActivity;
import com.keys.aman.notes.addnote.NoteHelperClass;
import com.keys.aman.signin_login.LogInActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class SecretNotesActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    TextView tvNote;
    private String uid;
    LogInActivity logInActivity = new LogInActivity();
    MyPreference myPreference;
    TabLayoutActivity tabLayoutActivity = new TabLayoutActivity();
    //todo 2 object calling of AppLockCounterClass
    AppLockCounterClass appLockCounterClass = new AppLockCounterClass(SecretNotesActivity.this, SecretNotesActivity.this);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secret_notes);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);
        myPreference = MyPreference.getInstance(this);
        //todo 3 when is coming from background or foreground always isForeground false
        SplashActivity.isForeground = false;

        //Hooks
        tvNote = findViewById(R.id.tv_NOTE);

        uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        Intent intent = getIntent();
        String comingRequestCode = intent.getStringExtra(logInActivity.REQUEST_CODE_NAME);
        if (comingRequestCode == null){
            comingRequestCode = "this";
        }

        if (comingRequestCode.equals(LogInActivity.REQUEST_ID)) {
        }



        recyclerViewSetData();
    }

    public void recyclerViewSetData() {
        RecyclerView recyclerView;
        SecretNoteAdapter adaptor;
        ArrayList<NoteHelperClass> dataholder;


        recyclerView = findViewById(R.id.recview_website_list);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("notes").child(uid);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dataholder = new ArrayList<>();
        adaptor = new SecretNoteAdapter(dataholder, getApplicationContext(),this){
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
                        NoteHelperClass data = ds.getValue(NoteHelperClass.class);
                        assert data != null;
                        if (data.isHideNote()){
                            dataholder.add(data);
                        }else {

                        }

                    }
                    Collections.sort(dataholder, NoteHelperClass.addDNoteHelperClassComparator);
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
        //todo 4 if app is going to another activity make isForeground = true
        SplashActivity.isForeground = true;
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //todo 9 onStartOperation, it will check app is
        // coming from foreground or background.
        appLockCounterClass.onStartOperation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //todo 10 onPauseOperation, it will check app is
        // going to foreground or background.
        // if UI component made isForeground = true then it
        // is going to another activity then this method will make
        // isForeground = false, so user will not be verified.
        // if UI component is not clicked then it
        // is going in background then this method will make
        // isBackground = true and timer will started,
        // at time of return, user will be verified.
        appLockCounterClass.checkedItem = myPreference.getLockAppSelectedOption();
        appLockCounterClass.onPauseOperation();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //todo 11 app is going to close no to do anything
        SplashActivity.isForeground = true;
        finish();
    }
}