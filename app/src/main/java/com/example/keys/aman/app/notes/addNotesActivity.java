package com.example.keys.aman.app.notes;

import static com.example.keys.aman.app.SplashActivity.mRewardedAd;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.keys.R;
import com.example.keys.aman.app.AES;
import com.example.keys.aman.app.signin_login.LogInActivity;
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

public class addNotesActivity extends AppCompatActivity {
    String currentDateAndTime, title, note, title_dc, note_dc;
    boolean isHideNote;
    SharedPreferences sharedPreferences;
    TextInputLayout tilAddNoteTitle, tilAddNoteBody;
    TextInputEditText tietAddNoteTitle, tietAddNoteBody;
    CheckBox cbHideNote;

    ImageButton img_save, img_edit;
    private String comingrequestcode;
    private String coming_date;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notes);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        sharedPreferences = getSharedPreferences(LogInActivity.SHARED_PREF_ALL_DATA, MODE_PRIVATE);
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

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
        comingrequestcode = intent.getStringExtra(LogInActivity.REQUEST_CODE_NAME);
        if (comingrequestcode == null) {
            comingrequestcode = "this";
        }

        coming_date = intent.getStringExtra("date");
        boolean coming_cb_hide_note = intent.getBooleanExtra("hide note", false);
        String coming_title = intent.getStringExtra("title");
        String coming_note = intent.getStringExtra("note");
        if (comingrequestcode.equals("notesCardView")) {
            tietAddNoteTitle.setText(coming_title);
            tietAddNoteBody.setText(coming_note);
            cbHideNote.setChecked(coming_cb_hide_note);
            cbHideNote.setEnabled(false);
            tilAddNoteTitle.setEnabled(false);
            tilAddNoteBody.setEnabled(false);
            img_save.setVisibility(View.INVISIBLE);
            img_edit.setVisibility(View.VISIBLE);
        } else if (comingrequestcode.equals("notesActivity")) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
            currentDateAndTime = sdf.format(new Date());
            System.out.println("Dateandtime: " + currentDateAndTime);
        } else if (comingrequestcode.equals("HomeActivity")) {
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


        if (mRewardedAd != null) {
            Activity activityContext = addNotesActivity.this;
            mRewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    // Handle the reward.
                    int rewardAmount = rewardItem.getAmount();
                    String rewardType = rewardItem.getType();
                }
            });
        } else {
            Toast.makeText(addNotesActivity.this, "The rewarded ad wasn't ready yet.", Toast.LENGTH_SHORT).show();
        }
    }

    public void gocencal(View view) {
        finish();
        overridePendingTransition(0, R.anim.slide_out_down);
    }

    public void gosave(View view) {
        title = Objects.requireNonNull(tilAddNoteTitle.getEditText()).getText().toString();
        note = Objects.requireNonNull(tilAddNoteBody.getEditText()).getText().toString();
        AES aes = new AES();
        aes.initFromStrings(sharedPreferences.getString(LogInActivity.AES_KEY, null), sharedPreferences.getString(LogInActivity.AES_IV, null));
        try {
            // Double encryption
            // TODO : in future, (if needed) give two key to user for double encryption
            title_dc = aes.encrypt(title);
            title_dc = aes.encrypt(title_dc);
            note_dc = aes.encrypt(note);
            note_dc = aes.encrypt(note_dc);
        } catch (Exception e) {
            e.printStackTrace();
        }

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("notes").child(uid);
        if (comingrequestcode.equals("notesCardView")) {
            addDNoteHelperClass addDNoteHelper = new addDNoteHelperClass(coming_date, title_dc, note_dc, isHideNote);
            reference.child(coming_date).setValue(addDNoteHelper);
            Toast.makeText(addNotesActivity.this, "saved!", Toast.LENGTH_SHORT).show();
        } else {
            addDNoteHelperClass addDNoteHelper = new addDNoteHelperClass(currentDateAndTime, title_dc, note_dc, isHideNote);
            reference.child(currentDateAndTime).setValue(addDNoteHelper);
            Toast.makeText(addNotesActivity.this, "saved!", Toast.LENGTH_SHORT).show();
        }
        Intent intent = new Intent(addNotesActivity.this, notesActivity.class);
        intent.putExtra(LogInActivity.REQUEST_CODE_NAME, "addNotesActivity");
        startActivity(intent);
        finish();
        overridePendingTransition(0, R.anim.slide_out_down);
    }

    public void goedit(View view) {
        img_save.setVisibility(View.VISIBLE);
        img_edit.setVisibility(View.INVISIBLE);
        cbHideNote.setEnabled(true);
        tilAddNoteTitle.setEnabled(true);
        tilAddNoteBody.setEnabled(true);
    }
}