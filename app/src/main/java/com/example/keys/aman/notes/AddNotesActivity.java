package com.example.keys.aman.notes;

import static com.example.keys.aman.SplashActivity.mRewardedAd;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.keys.R;
import com.example.keys.aman.AES;
import com.example.keys.aman.DatabaseProcess;
import com.example.keys.aman.SplashActivity;
import com.example.keys.aman.base.TabLayoutActivity;
import com.example.keys.aman.signin_login.LogInActivity;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class AddNotesActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    TextInputLayout tilAddNoteTitle, tilAddNoteBody;
    TextInputEditText tietAddNoteTitle, tietAddNoteBody;
    CheckBox cbHideNote;
    ImageButton img_save, img_edit;
    LogInActivity logInActivity = new LogInActivity();

    String currentDateAndTime, title, note, titleDecrypted, noteDecrypted;
    boolean isHideNote;
    private String comingRequestCode;
    private String comingDate;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notes);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        sharedPreferences = getSharedPreferences(logInActivity.getSHARED_PREF_ALL_DATA(), MODE_PRIVATE);
        uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        SplashActivity.isForeground = false;

        //Hooks
        tilAddNoteTitle = findViewById(R.id.til_addtitle);
        tilAddNoteBody = findViewById(R.id.til_addnote);
        tietAddNoteTitle = findViewById(R.id.tiet_addtitle);
        tietAddNoteBody = findViewById(R.id.tiet_addnote);
        cbHideNote = findViewById(R.id.cb_hide_note);
        img_save = findViewById(R.id.img_save);
        img_edit = findViewById(R.id.img_edit);

        //Hide mobile no and
        Intent intent = getIntent();
        comingRequestCode = intent.getStringExtra(logInActivity.getREQUEST_CODE_NAME());
        if (comingRequestCode == null) {
            comingRequestCode = "this";
        }

        comingDate = intent.getStringExtra("date");
        boolean comingIsHideNote = intent.getBooleanExtra("hide note", false);
        String comingTitle = intent.getStringExtra("title");
        String comingNote = intent.getStringExtra("note");
        if (comingRequestCode.equals("notesCardView")) {
            tietAddNoteTitle.setText(comingTitle);
            tietAddNoteBody.setText(comingNote);
            cbHideNote.setChecked(comingIsHideNote);
            cbHideNote.setEnabled(false);
            tilAddNoteTitle.setEnabled(false);
            tilAddNoteBody.setEnabled(false);
            img_save.setVisibility(View.INVISIBLE);
            img_edit.setVisibility(View.VISIBLE);
        } else if (comingRequestCode.equals("notesActivity")) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
            currentDateAndTime = sdf.format(new Date());
            System.out.println("Dateandtime: " + currentDateAndTime);
        } else if (comingRequestCode.equals("HomeActivity")) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
            currentDateAndTime = sdf.format(new Date());
            System.out.println("Dateandtime: " + currentDateAndTime);
        }

        cbHideNote.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isHideNote = b;
            }
        });

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!comingRequestCode.equals("notesCardView")) {
                    if (mRewardedAd != null) {
                        Activity activityContext = AddNotesActivity.this;
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
                        Toast.makeText(AddNotesActivity.this, "The rewarded ad wasn't ready yet.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }, 1000);


    }

    public void goCancel(View view) {
        SplashActivity.isForeground = true;
        finish();
        overridePendingTransition(0, R.anim.slide_out_down);
    }

    public void goSave(View view) {
        title = Objects.requireNonNull(tilAddNoteTitle.getEditText()).getText().toString();
        note = Objects.requireNonNull(tilAddNoteBody.getEditText()).getText().toString();
        AES aes = new AES();
        aes.initFromStrings(sharedPreferences.getString(logInActivity.getAES_KEY(), null), sharedPreferences.getString(logInActivity.getAES_IV(), null));
        try {
            // Double encryption
            // TODO : in future, (if needed) give two key to user for double encryption
            titleDecrypted = aes.encrypt(title);
            titleDecrypted = aes.encrypt(titleDecrypted);
            noteDecrypted = aes.encrypt(note);
            noteDecrypted = aes.encrypt(noteDecrypted);
        } catch (Exception e) {
            e.printStackTrace();
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("notes").child(uid);
        if (comingRequestCode.equals("notesCardView")) {
            AddNoteDataHelperClass addDNoteHelper = new AddNoteDataHelperClass(comingDate, titleDecrypted, noteDecrypted, isHideNote, false);
            DatabaseProcess processStore = new DatabaseProcess(addDNoteHelper);
            processStore.storeData(reference);
//            reference.child(comingDate).setValue(addDNoteHelper);
            Toast.makeText(AddNotesActivity.this, "saved!", Toast.LENGTH_SHORT).show();
        } else {
            AddNoteDataHelperClass addDNoteHelper = new AddNoteDataHelperClass(currentDateAndTime, titleDecrypted, noteDecrypted, isHideNote, true);
            DatabaseProcess processStore = new DatabaseProcess(addDNoteHelper);
            processStore.storeData(reference);
//            reference.child(currentDateAndTime).setValue(addDNoteHelper);
            Toast.makeText(AddNotesActivity.this, "saved!", Toast.LENGTH_SHORT).show();
        }
        SplashActivity.isForeground = true;
        Intent intent = new Intent(AddNotesActivity.this, TabLayoutActivity.class);
        intent.putExtra(logInActivity.getREQUEST_CODE_NAME(), "addNotesActivity");
        startActivity(intent);
        finish();
        overridePendingTransition(0, R.anim.slide_out_down);
    }

    public void goEdit(View view) {
        img_save.setVisibility(View.VISIBLE);
        img_edit.setVisibility(View.INVISIBLE);
        cbHideNote.setEnabled(true);
        tilAddNoteTitle.setEnabled(true);
        tilAddNoteBody.setEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (SplashActivity.isBackground) {
            Intent intent = new Intent(AddNotesActivity.this, BiometricActivity.class);
            intent.putExtra(logInActivity.getREQUEST_CODE_NAME(), "LockBackGroundApp");
            startActivity(intent);
        }
        if (SplashActivity.isForeground) {
            SplashActivity.isForeground = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!SplashActivity.isForeground) {
            SplashActivity.isBackground = true;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SplashActivity.isForeground = true;
        finish();
        overridePendingTransition(0, R.anim.slide_out_down);
    }
}