package com.example.keys.aman.app.notes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.keys.R;
import com.example.keys.aman.app.AES;
import com.example.keys.aman.app.signin_login.LogInActivity;
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
    String currentDateandTime, mobile, title, note, title_dc, note_dc;
    boolean hide_note;
    SharedPreferences sharedPreferences;
    TextInputLayout til_addtitle, til_addnote;
    TextInputEditText tiet_addtitle, tiet_addnote;
    CheckBox cb_hide_note;

    private static final int PICK_IMAGE_REQUEST = 1;

    ImageButton img_attach_image, img_show_image, img_save, img_edit;
    ProgressBar hori_prograssbar;
    Uri mImageUri;
    private String comingrequestcode;
    private String coming_data;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notes);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);
        sharedPreferences = getSharedPreferences(LogInActivity.SHARED_PREF_ALL_DATA, MODE_PRIVATE);
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //Hooks
        til_addtitle = findViewById(R.id.til_addtitle);
        til_addnote = findViewById(R.id.til_addnote);
        tiet_addtitle = findViewById(R.id.tiet_addtitle);
        tiet_addnote = findViewById(R.id.tiet_addnote);
        cb_hide_note = findViewById(R.id.cb_hide_note);
        img_save = findViewById(R.id.img_save);
        img_edit = findViewById(R.id.img_edit);

        //Hide mobile no and
        Intent intent = getIntent();
        comingrequestcode = intent.getStringExtra("request_code");
        if (comingrequestcode == null) {
            comingrequestcode = "this";
        }
        Toast.makeText(addNotesActivity.this, comingrequestcode, Toast.LENGTH_LONG).show();
        coming_data = intent.getStringExtra("date");
        boolean coming_cb_hide_note = intent.getBooleanExtra("hide note", false);
        String coming_title = intent.getStringExtra("title");
        String coming_note = intent.getStringExtra("note");
        Toast.makeText(addNotesActivity.this, comingrequestcode, Toast.LENGTH_SHORT).show();
        if (comingrequestcode.equals("notesCardView")) {
            tiet_addtitle.setText(coming_title);
            tiet_addnote.setText(coming_note);
            cb_hide_note.setChecked(coming_cb_hide_note);
            cb_hide_note.setEnabled(false);
            til_addtitle.setEnabled(false);
            til_addnote.setEnabled(false);
            img_save.setVisibility(View.INVISIBLE);
            img_edit.setVisibility(View.VISIBLE);
            Toast.makeText(addNotesActivity.this,"Date_time: " + currentDateandTime,Toast.LENGTH_LONG).show();
        }

        cb_hide_note.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                hide_note = b;
            }
        });

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        currentDateandTime = sdf.format(new Date());
        System.out.println("Dateandtime: " + currentDateandTime);
        Toast.makeText(addNotesActivity.this,"Date_time: " + currentDateandTime,Toast.LENGTH_LONG).show();
    }

    public void gocencal(View view) {
        finish();
        overridePendingTransition(0, R.anim.slide_out_down);
    }

    public void gosave(View view) {
        title = Objects.requireNonNull(til_addtitle.getEditText()).getText().toString();
        note = Objects.requireNonNull(til_addnote.getEditText()).getText().toString();
        AES aes = new AES();
        aes.initFromStrings(sharedPreferences.getString(LogInActivity.AES_KEY,null),sharedPreferences.getString(LogInActivity.AES_IV,null));
        try {
            title_dc = aes.encrypt(title);
            note_dc = aes.encrypt(note);
        } catch (Exception e) {
            e.printStackTrace();
        }

        addDNoteHelperClass addDNoteHelper = new addDNoteHelperClass(currentDateandTime,title_dc,note_dc, hide_note);

        mobile = sharedPreferences.getString(LogInActivity.KEY_USER_MOBILE, null);
        System.out.println(mobile);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("notes").child(uid);
        if (comingrequestcode.equals("notesCardView")){
            Toast.makeText(addNotesActivity.this,"notesCardView",Toast.LENGTH_SHORT).show();
            reference.child(coming_data).setValue(addDNoteHelper);
        }else {
            Toast.makeText(addNotesActivity.this,"this",Toast.LENGTH_SHORT).show();
            reference.child(currentDateandTime).setValue(addDNoteHelper);
        }
        startActivity(new Intent(addNotesActivity.this,notesActivity.class));
        finish();
        overridePendingTransition(0, R.anim.slide_out_down);
    }

    public void goedit(View view) {
        img_save.setVisibility(View.VISIBLE);
        img_edit.setVisibility(View.INVISIBLE);
        cb_hide_note.setEnabled(true);
        til_addtitle.setEnabled(true);
        til_addnote.setEnabled(true);
    }
}