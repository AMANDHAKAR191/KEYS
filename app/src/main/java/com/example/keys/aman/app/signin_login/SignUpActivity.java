package com.example.keys.aman.app.signin_login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.keys.R;
import com.example.keys.aman.app.PrograceBar;
import com.example.keys.aman.app.home.PassGenActivity;
import com.example.keys.aman.app.settings.OTPVerification;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {
    public PrograceBar prograce_bar;
    public String REQUEST_CODE = "SignInActivity";
    TextInputLayout til_name, til_mobile, til_email, til_password, til_cpassword;
    TextInputEditText tiet_signin_name, tiet_signin_mobile, tiet_signin_email;
    Button b_signup;
    TextView set_error;
    ImageButton img_back;
    String name, mobile, email, password, cpassword;

    SharedPreferences sharedPreferences;
    public static String AES_KEY = "aes_key";
    public static String AES_IV = "aes_iv";
    public static final String SHARED_PREF_ALL_DATA = "All data";
    public static final String KEY_USER_MOBILE = "mobile";
    public static final String KEY_USER_PASSSWORD = "password";
    public static final String KEY_USER_NAME = "name";
    public static final String KEY_USER_EMAIL = "email";
    public static String KEY_REMEMBER_ME;
    public static String KEY_USE_FINGERPRINT;
    public static String KEY_USE_PIN;
    public static String KEY_CREATE_ADDP_SHORTCUT;
    public static String KEY_CREATE_ADDN_SHORTCUT;
    public static final String TAG = "main Activity";
    public static String aes_key = "aes_key";
    public static String aes_iv = "aes_iv";



    String comingrequestcode;
    String coming_signname, coming_signmobile, coming_signemail;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);
        sharedPreferences = getSharedPreferences(SHARED_PREF_ALL_DATA, MODE_PRIVATE);

        til_name = findViewById(R.id.til_signin_name);
        til_mobile = findViewById(R.id.til_signin_mobile_no);
        til_email = findViewById(R.id.til_signin_email);
        til_password = findViewById(R.id.til_signin_password);
        til_cpassword = findViewById(R.id.til_signin_cpassword);
        tiet_signin_name = findViewById(R.id.tiet_signin_name);
        tiet_signin_mobile = findViewById(R.id.tiet_signin_mobile_no);
        tiet_signin_email = findViewById(R.id.tiet_signin_email);
        img_back = findViewById(R.id.img_back);
        b_signup = findViewById(R.id.b_sign_register);
        b_signup.setText("Register");
        set_error = findViewById(R.id.set_error);
        //TODO Check1 add country code feature in login and sign in Activity

        //Hide mobile no and
        img_back.setVisibility(View.VISIBLE);
        Intent intent = getIntent();
        comingrequestcode = intent.getStringExtra("request_code");
        if (comingrequestcode == null){
            comingrequestcode = "this";

            SharedPreferences.Editor editor1 = sharedPreferences.edit();
            editor1.putString(SignUpActivity.AES_KEY,PassGenActivity.generateRandomPassword(22,true,true,true,false) +"==");
            editor1.putString(SignUpActivity.AES_IV, PassGenActivity.generateRandomPassword(16,true,true,true,false));
            editor1.apply();
        }
        coming_signname = intent.getStringExtra("signname");
        coming_signmobile = intent.getStringExtra("signmobile");
        coming_signemail = intent.getStringExtra("signemail");
        Toast.makeText(SignUpActivity.this, comingrequestcode, Toast.LENGTH_SHORT).show();

        if (comingrequestcode.equals("ProfileActivity")) {

            b_signup.setText("Update Sign Data");
            tiet_signin_name.setText(coming_signname);
            tiet_signin_mobile.setText(coming_signmobile);
            tiet_signin_email.setText(coming_signemail);
            til_mobile.setEnabled(false);

        }


    }

    public void register_or_updatadata(View view) {
        saveDataOnFireBase();
        if (comingrequestcode.equals("ProfileActivity")) {
            Toast.makeText(SignUpActivity.this,"Going to ProfileActivity",Toast.LENGTH_SHORT).show();
            finish();
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }else {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }

    }
    private void saveDataOnFireBase() {
        //Validating users Data

                name = Objects.requireNonNull(til_name.getEditText()).getText().toString();
                mobile = Objects.requireNonNull(til_mobile.getEditText()).getText().toString();
                email = Objects.requireNonNull(til_email.getEditText()).getText().toString();
                password = Objects.requireNonNull(til_password.getEditText()).getText().toString();
                cpassword = Objects.requireNonNull(til_cpassword.getEditText()).getText().toString();
                System.out.println(name +  mobile + email + password);

                if (name.equals("") || mobile.equals("") || password.equals("")) {
                    set_error.setError("Fill the Blank Fields");
                    set_error.setVisibility(View.VISIBLE);
                } else if (password.length() <= 4) {
                    set_error.setVisibility(View.VISIBLE);
                    set_error.setText("Password is too short");
                } else if (!password.equals(cpassword)) {
                    set_error.setVisibility(View.VISIBLE);
                    set_error.setText("Password didn't Match");
                } else {

//                    onActivityresult();
//                    Toast.makeText(SignUpActivity.this,"Done",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignUpActivity.this, OTPVerification.class);
                    intent.putExtra("request_code",REQUEST_CODE);
                    intent.putExtra("name",name);
                    intent.putExtra("mobile",mobile);
                    intent.putExtra("email",email);
                    intent.putExtra("password",password);
                    System.out.println(name +  mobile + email + password);
                    Toast.makeText(SignUpActivity.this, "Going to OTP Activity!!", Toast.LENGTH_LONG).show();
                    startActivityForResult(intent, 1);
                }
    }


//    public void onActivityresult() {
//        Toast.makeText(SignUpActivity.this, "SignIn Activity!!", Toast.LENGTH_LONG).show();
//        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        //super.onActivityResult(requestCode, resultCode, data);
//        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = firebaseDatabase.getReference("signupdata");
//
//        //progressbar();
//        AES aes = new AES();
//        aes.initFromStrings(sharedPreferences.getString(SignUpActivity.AES_KEY, null), sharedPreferences.getString(SignUpActivity.AES_IV, null));
//        String e_name, e_mobile, e_email, e_password;
//        try {
//            Toast.makeText(SignUpActivity.this, "Saving Data on DB", Toast.LENGTH_SHORT).show();
//            e_name = aes.encrypt(name);
//            e_mobile = aes.encrypt(mobile);
//            e_email = aes.encrypt(email);
//            e_password = aes.encrypt(password);
//            String key = sharedPreferences.getString(AES_KEY, null);
//            String iv = sharedPreferences.getString(AES_IV, null);
//            UserHelperClass userHelperClass = new UserHelperClass(e_name, e_mobile, e_email, e_password, key, iv, uid);
//
//            if (comingrequestcode.equals("ProfileActivity")) {
//                Toast.makeText(SignUpActivity.this, "Updated!", Toast.LENGTH_SHORT).show();
//            }
//
//            myRef.child(uid).setValue(userHelperClass);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public void progressbar() {
        prograce_bar = new PrograceBar(SignUpActivity.this);
        prograce_bar.showDialog();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        }, 500);
    }
    public void goback(View view) {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

    }
}