package com.example.keys.aman.app.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.autofill.AutofillManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.keys.R;
import com.example.keys.aman.app.home.PassGenActivity;
import com.example.keys.aman.app.notes.addNotesActivity;
import com.example.keys.aman.app.signin_login.LogInActivity;
import com.google.firebase.auth.FirebaseAuth;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {

    TextView tv_app_info, tv_contectus, tv_privacy_policy, tv_terms_and_conditions, tv_profile_name;
    ImageView img_back;
    Switch sw_enable_fingerprint, sw_enable_pin, sw_addpassword_shortcut,sw_addnotes_shortcut;
    SharedPreferences sharedPreferences;
    public static boolean ischecked;
    Button button_logout;
    CircleImageView img_profile;
    AutofillManager mAutofillManager;
    private static final int REQUEST_CODE_SET_DEFAULT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        sharedPreferences = getSharedPreferences(LogInActivity.SHARED_PREF_ALL_DATA, MODE_PRIVATE);
        /*---------------Hooks--------------*/
        tv_app_info = findViewById(R.id.tv_app_info);
        tv_contectus = findViewById(R.id.tv_contectus);
        tv_privacy_policy = findViewById(R.id.tv_privacy_policy);
        tv_terms_and_conditions = findViewById(R.id.tv_terms_and_conditions);
        sw_enable_fingerprint = findViewById(R.id.sw_use_finger);
        sw_enable_pin = findViewById(R.id.sw_use_pin);
        sw_addpassword_shortcut = findViewById(R.id.sw_addpassword_shortcut);
        sw_addnotes_shortcut = findViewById(R.id.sw_addnotes_shortcut);
        img_back = findViewById(R.id.img_back);
        button_logout = findViewById(R.id.btn_logout);
        img_profile = findViewById(R.id.img_profile);
        tv_profile_name = findViewById(R.id.tv_profile_name);
        //TODO Check: add Security textview in this
        //TODO Check: let user choose weather user want to use fingerprint lock or not
        //TODO Check: ask user to use biometric info in this Activity only

        tv_app_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingActivity.this, AppInfo.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        tv_contectus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                contactUsFragment contactUs = new contactUsFragment();
                contactUs.show(getSupportFragmentManager(), "contact_us");
            }
        });
        tv_privacy_policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent open_privacy_policy = new Intent(Intent.ACTION_VIEW, Uri.parse("https://amandhakar.blogspot.com/2022/02/privacy-policy-keys.html"));
                startActivity(open_privacy_policy);
            }
        });
        tv_terms_and_conditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent open_terms_conditions = new Intent(Intent.ACTION_VIEW, Uri.parse("https://amandhakar.blogspot.com/2022/02/terms-conditions-keys.html"));
                startActivity(open_terms_conditions);
            }
        });

        String is_use_fingerprint = sharedPreferences.getString(LogInActivity.KEY_USE_FINGERPRINT,"false");
        String is_use_pin = sharedPreferences.getString(LogInActivity.KEY_USE_PIN,"false");
        String is_addp_shortcut_created = sharedPreferences.getString(LogInActivity.KEY_CREATE_ADDP_SHORTCUT,"false");
        String is_addn_shortcut_created = sharedPreferences.getString(LogInActivity.KEY_CREATE_ADDN_SHORTCUT,"false");
        sw_enable_fingerprint.setChecked(is_use_fingerprint == "true");

        if (is_use_pin == "true"){
            sw_enable_pin.setChecked(true);
        }else {
            sw_enable_pin.setChecked(true);
        }

        sw_addpassword_shortcut.setChecked(is_addp_shortcut_created == "true");

        sw_addnotes_shortcut.setChecked(is_addn_shortcut_created == "true");

        sw_enable_fingerprint.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                sw_enable_fingerprint.setChecked(b);
            }
        });
        sw_enable_pin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                sw_enable_pin.setChecked(b);
            }
        });
        sw_addpassword_shortcut.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(LogInActivity.KEY_CREATE_ADDP_SHORTCUT, b);
                editor.apply();
                sw_addpassword_shortcut.setChecked(b);
                create_addp_ShortcutOfApp();
            }
        });
        sw_addnotes_shortcut.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(LogInActivity.KEY_CREATE_ADDN_SHORTCUT, b);
                editor.apply();
                sw_addpassword_shortcut.setChecked(b);
                create_addn_ShortcutOfApp();
            }
        });

//        button_logout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FirebaseAuth.getInstance().signOut();
//
//                SharedPreferences.Editor editor1 = sharedPreferences.edit();
//                editor1.putString(LogInActivity.ISLOGIN, "false");
//                editor1.apply();
//                System.out.println(sharedPreferences.getString(LogInActivity.ISLOGIN,null));
//
//                Intent intent = new Intent(getApplicationContext(),LogInActivity.class);
//                startActivity(intent);
//            }
//        });


        button_logout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mAutofillManager = getSystemService(AutofillManager.class);
               if (mAutofillManager.hasEnabledAutofillServices()){
                   Toast.makeText(SettingActivity.this, "AutoFill Enabled", Toast.LENGTH_SHORT).show();
               } else {
                   Intent intent = new Intent(Settings.ACTION_REQUEST_SET_AUTOFILL_SERVICE);
                   intent.setData(Uri.parse("package:com.example.android.autofill.service"));
                   startActivityForResult(intent, 123);
               }
                return false;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("onActivityResult(): req=" + requestCode);
        switch (requestCode) {
            case REQUEST_CODE_SET_DEFAULT:
                onDefaultServiceSet(resultCode);
                break;
        }
    }
    private void onDefaultServiceSet(int resultCode) {
        System.out.println("resultCode=%d" +  resultCode);
        switch (resultCode) {
            case RESULT_OK:
                System.out.println("Autofill service set.");
                Toast.makeText(SettingActivity.this, "Autofill service set.", Toast.LENGTH_SHORT).show();
                break;
            case RESULT_CANCELED:
                System.out.println("Autofill service not selected.");
                Toast.makeText(SettingActivity.this, "Autofill service not selected.", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        Uri currentUser = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();
        String currentUserName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        System.out.println(currentUser);
        if (currentUser == null) {
            // No user is signed in
        } else {
            // User logged in
            Glide.with(this).load(currentUser).into(img_profile);
            tv_profile_name.setText(currentUserName);
        }
    }

    public void open_profileAcitivity(View view) {
        startActivity(new Intent(SettingActivity.this, ProfileActivity.class));
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_up);
    }

    public void img_back(View view) {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void addwebsite(View view) {
        addWebsiteFragment addWebsiteFragment = new addWebsiteFragment();
        addWebsiteFragment.show(getSupportFragmentManager(), "add_website");
    }

    public void create_addp_ShortcutOfApp() {

        Intent shortcutIntent = new Intent(getApplicationContext(),
                PassGenActivity.class);
        shortcutIntent.setAction(Intent.ACTION_MAIN);

        Intent addIntent = new Intent();
        addIntent
                .putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "Genrate password");
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(getApplicationContext(),
                        R.mipmap.keys_louncher_icon));

        addIntent
                .setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        addIntent.putExtra("duplicate", false);  //may it's already there so   don't duplicate
        getApplicationContext().sendBroadcast(addIntent);
    }
    public void create_addn_ShortcutOfApp() {

        Intent shortcutIntent = new Intent(getApplicationContext(),
                addNotesActivity.class);
        shortcutIntent.setAction(Intent.ACTION_MAIN);

        Intent addIntent = new Intent();
        addIntent
                .putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "Add Notes");
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(getApplicationContext(),
                        R.mipmap.keys_louncher_icon));

        addIntent
                .setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        addIntent.putExtra("duplicate", false);  //may it's already there so   don't duplicate
        getApplicationContext().sendBroadcast(addIntent);
    }



}