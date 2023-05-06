package com.keys.aman.notes.addnote;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.keys.aman.AES;
import com.keys.aman.MyPreference;
import com.keys.aman.R;
import com.keys.aman.SplashActivity;
import com.keys.aman.authentication.AppLockCounterClass;
import com.keys.aman.base.TabLayoutActivity;
import com.keys.aman.data.Firebase;
import com.keys.aman.iAES;
import com.keys.aman.notes.NoteAdapterForUnpinned;
import com.keys.aman.signin_login.LogInActivity;

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
    TabLayoutActivity tabLayoutActivity = new TabLayoutActivity();
    //todo 2 object calling of AppLockCounterClass
    AppLockCounterClass appLockCounterClass = new AppLockCounterClass(AddNotesActivity.this, AddNotesActivity.this);

    String currentDateAndTime;

    boolean isHideNote;
    MyPreference myPreference;
    private String comingRequestCode;
    private String comingDate;
    private String uid;
    iAES iAES;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notes);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        myPreference = MyPreference.getInstance(this);
        iAES = AES.getInstance(myPreference.getAesKey(), myPreference.getAesIv());
        uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        //todo 3 when is coming from background or foreground always isForeground false
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
        comingRequestCode = intent.getStringExtra(logInActivity.REQUEST_CODE_NAME);
        if (comingRequestCode == null) {
            comingRequestCode = "this";
        }

        comingDate = intent.getStringExtra("date");
        boolean comingIsHideNote = intent.getBooleanExtra("hide note", false);
        String comingTitle = intent.getStringExtra("title");
        String comingNote = intent.getStringExtra("note");
        switch (comingRequestCode) {
            case NoteAdapterForUnpinned.REQUEST_ID:
                tietAddNoteTitle.setText(comingTitle);
                tietAddNoteBody.setText(comingNote);
                cbHideNote.setChecked(comingIsHideNote);
                cbHideNote.setEnabled(false);
                tilAddNoteTitle.setEnabled(false);
                tilAddNoteBody.setEnabled(false);
                img_save.setVisibility(View.INVISIBLE);
                img_edit.setVisibility(View.VISIBLE);
                break;
            case TabLayoutActivity.REQUEST_ID:
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
                currentDateAndTime = sdf.format(new Date());
                System.out.println("Dateandtime: " + currentDateAndTime);
                break;
        }

        cbHideNote.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isHideNote = b;
            }
        });


    }

    public void goCancel(View view) {
        //todo 6 if app is going to another activity make isForeground = true
        SplashActivity.isForeground = true;
        finish();
        overridePendingTransition(0, R.anim.slide_out_down);
    }

    public void goSave(View view) {
        String title, note, titleEncrypted, noteEncrypted;
        title = Objects.requireNonNull(tilAddNoteTitle.getEditText()).getText().toString();
        note = Objects.requireNonNull(tilAddNoteBody.getEditText()).getText().toString();

        titleEncrypted = iAES.doubleEncryption(title);
        noteEncrypted = iAES.doubleEncryption(note);

        NoteHelperClass addDNoteHelper;
        if (comingRequestCode.equals(NoteAdapterForUnpinned.REQUEST_ID)) {
            Firebase.getInstance(AddNotesActivity.this).saveSingleNote(comingDate, titleEncrypted, noteEncrypted, isHideNote, new Firebase.onNoteSaveCallBack() {
                @Override
                public void onNoteSaved() {
                    Toast.makeText(AddNotesActivity.this, "Note saved", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailed(String message) {
                    Toast.makeText(AddNotesActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Firebase.getInstance(AddNotesActivity.this).saveSingleNote(currentDateAndTime, titleEncrypted, noteEncrypted, isHideNote, new Firebase.onNoteSaveCallBack() {
                @Override
                public void onNoteSaved() {
                    Toast.makeText(AddNotesActivity.this, "Note saved", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailed(String message) {
                    Toast.makeText(AddNotesActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            });
        }

        //todo 6 if app is going to another activity make isForeground = true
        SplashActivity.isForeground = true;
        Intent intent = new Intent(AddNotesActivity.this, TabLayoutActivity.class);
        intent.putExtra(logInActivity.REQUEST_CODE_NAME, "addNotesActivity");
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
        overridePendingTransition(0, R.anim.slide_out_down);
    }
}