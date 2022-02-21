package com.example.keys.aman.app.home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.keys.aman.app.AES;
import com.example.keys.R;
import com.example.keys.aman.app.signin_login.SignUpActivity;
import com.google.android.material.slider.Slider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Random;

public class PassGenActivity extends AppCompatActivity {

    public static final int REQUEST_DETAIL_CODE = 2001;
    SharedPreferences sharedPreferences;
    private static final String KEY_PASSWORD = "password";

    final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    final DatabaseReference myRef = firebaseDatabase.getReference("usedPassword");
    int initialvalue;
    final String getnumber = "0";

    //variable
    SwitchCompat capitalLetter, lowercaseLetter, numbers, symbols;
    public Button bt_use, bt_copy;
    ImageButton img_back;
    TextView tv_password;
    String saved_pass;
    Slider slider;
    int MAX_LENGTH = 0;
    private String pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass_gen);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);
        sharedPreferences = getSharedPreferences(SignUpActivity.SHARED_PREF_ALL_DATA, MODE_PRIVATE);
        createShortcutOfApp();
        //pass_gen_bottom_nav();
//        String saved_pass = sharedPreferences.getString(KEY_NAME,null);

        // Hooks
        switchButton();
        tv_password = findViewById(R.id.tv_password);
        //tv_pass.setText("Password: ");
        slider = findViewById(R.id.slider_pass_lenght);
        bt_use = findViewById(R.id.bt_use);
        bt_copy = findViewById(R.id.bt_copy);
        img_back = findViewById(R.id.img_back);
        //TODO Check: show all used password in a list
        //TODO Check: add feature to see last 10 or 20 used password and give button to clear used password

        // set default value of checkbox
        capitalLetter.setChecked(true);
        lowercaseLetter.setChecked(true);
        numbers.setChecked(true);
        symbols.setChecked(true);
        MAX_LENGTH = 8;
        genrate_password();
//        bt_use.setVisibility(View.VISIBLE);
        //Hide use button
        Intent intent = getIntent();
        String comingrequestcode =  intent.getStringExtra("requestCode");
        //Toast.makeText(PassGenActivity.this,comingrequestcode,Toast.LENGTH_SHORT).show();
        if (comingrequestcode.equals("HomeActivity")){
            Toast.makeText(PassGenActivity.this,comingrequestcode,Toast.LENGTH_SHORT).show();
            bt_use.setVisibility(View.INVISIBLE);
        }else if (comingrequestcode.equals("addPasswordData")){
            Toast.makeText(PassGenActivity.this,comingrequestcode,Toast.LENGTH_SHORT).show();
            bt_use.setVisibility(View.VISIBLE);
        }

        //Slider
        slider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {

            }

            @SuppressLint("RestrictedApi")
            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                genrate_password();
            }
        });

        bt_use.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textViewpassword = tv_password.getText().toString();


                myRef.child("0").setValue(textViewpassword);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(KEY_PASSWORD, getnumber);
                editor.apply();
                Toast.makeText(PassGenActivity.this, "Used!", Toast.LENGTH_SHORT).show();

                saved_pass = sharedPreferences.getString(KEY_PASSWORD, null);
                System.err.println("Your Saved Password: " + pass);
                Intent intentresult = getIntent();
                intentresult.putExtra("saved_Password", pass);
                setResult(RESULT_OK, intentresult);
                finish();
            }
        });
        bt_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(KEY_PASSWORD, pass);
                editor.apply();

                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                saved_pass = sharedPreferences.getString(KEY_PASSWORD, null);
                ClipData clipData = ClipData.newPlainText("Copy_Password", saved_pass);
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(PassGenActivity.this, "Copied!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void switchButton() {
        capitalLetter = findViewById(R.id.checkBox1);
        lowercaseLetter = findViewById(R.id.checkBox2);
        numbers = findViewById(R.id.checkBox3);
        symbols = findViewById(R.id.checkBox4);
        capitalLetter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                genrate_password();
            }
        });
        lowercaseLetter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                genrate_password();
            }
        });
        numbers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                genrate_password();
            }
        });
        symbols.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                genrate_password();
            }
        });
    }




    private static String generateRandomPassword(int max_length, boolean upperCase, boolean lowerCase, boolean numbers, boolean specialCharacters) {
        Random rn = new Random();
        StringBuilder sb = new StringBuilder(max_length);
        try {
            String upperCaseChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
            String lowerCaseChars = "abcdefghijklmnopqrstuvwxyz";
            String numberChars = "0123456789";
            String specialChars = "!@#$%^&*()_-+=<>?/{}~|";
            String allowedChars = "";


            //this will fulfill the requirements of atleast one character of a type.
            if (upperCase) {
                allowedChars += upperCaseChars;
                sb.append(upperCaseChars.charAt(rn.nextInt(upperCaseChars.length() - 1)));
            }
            if (lowerCase) {
                allowedChars += lowerCaseChars;
                sb.append(lowerCaseChars.charAt(rn.nextInt(lowerCaseChars.length() - 1)));
            }
            if (numbers) {
                allowedChars += numberChars;
                sb.append(numberChars.charAt(rn.nextInt(numberChars.length() - 1)));
            }
            if (specialCharacters) {
                allowedChars += specialChars;
                sb.append(specialChars.charAt(rn.nextInt(specialChars.length() - 1)));
            }
            //fill the allowed length from different chars now.
            for (int i = sb.length(); i < max_length; ++i) {
                sb.append(allowedChars.charAt(rn.nextInt(allowedChars.length())));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public String genrate_password() {
        StringBuilder password = new StringBuilder();
        ArrayList<Integer> passSel = new ArrayList<>();
        slider.addOnChangeListener(new Slider.OnChangeListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                MAX_LENGTH = (int) slider.getValue();
            }
        });

        pass = generateRandomPassword(MAX_LENGTH, capitalLetter.isChecked(), lowercaseLetter.isChecked(), numbers.isChecked(), symbols.isChecked());
        tv_password.setText("Password: " + pass);
        try {
            AES aes = new AES();
            aes.initFromStrings("CHuO1Fjd8YgJqTyapibFBQ==", "e3IYYJC2hxe24/EO");
            String encryptedMessage = aes.encrypt(pass);
//                    saved_pass = encryptedMessage;
            //System.err.println("Encrypted Message : " + encryptedMessage);
            String decryptedMessage = aes.decrypt(encryptedMessage);
            //pass_decoded.setText(decryptedMessage);
            //System.err.println("Decrypted Message : " + decryptedMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pass;
    }

    public void createShortcutOfApp() {

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


    public void goback(View view) {
        finish();
    }
}