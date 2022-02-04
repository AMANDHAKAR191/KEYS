package com.example.keys;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {
    public PrograceBar prograce_bar;
    public dialogForDBTakingLoingTime DBTakingLoingTime;
    public String REQUEST_CODE = "SignInActivity";
    TextInputLayout til_name, til_mobile, til_email, til_password, til_cpassword;
    Button b_signup;
    TextView set_error;
    String name, mobile, email, password, cpassword;

    SharedPreferences sharedPreferences;
    public static String SHARED_PREF_ALL_DATA = "All data";
    public static String KEY_USER_MOBILE = "mobile";
    public static String KEY_USER_PASSSWORD = "password";
    public static String KEY_USER_NAME = "name";
    public static String KEY_USER_EMAIL = "email";
    public static String KEY_REMEMBER_ME;
    public static String KEY_USE_FINGERPRINT;
    public static String TAG = "main Activity";


    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference myRef = firebaseDatabase.getReference("signupdata");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        sharedPreferences = getSharedPreferences(SHARED_PREF_ALL_DATA, MODE_PRIVATE);

        til_name = findViewById(R.id.til_signin_name);
        til_mobile = findViewById(R.id.til_signin_mobile_no);
        til_email = findViewById(R.id.til_signin_email);
        til_password = findViewById(R.id.til_signin_password);
        til_cpassword = findViewById(R.id.til_signin_cpassword);
        b_signup = findViewById(R.id.b_sign_register);
        set_error = findViewById(R.id.set_error);
        //TODO Check1 add country code feature in login and sign in Activity

        saveDataOnFireBase();
    }

    private void saveDataOnFireBase() {
        //Validating users Data
        b_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = Objects.requireNonNull(til_name.getEditText()).getText().toString();
                mobile = Objects.requireNonNull(til_mobile.getEditText()).getText().toString();
                email = Objects.requireNonNull(til_email.getEditText()).getText().toString();
                password = Objects.requireNonNull(til_password.getEditText()).getText().toString();
                cpassword = Objects.requireNonNull(til_cpassword.getEditText()).getText().toString();

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

                    Toast.makeText(SignUpActivity.this,"Done",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignUpActivity.this, OTPVerification.class);
                    intent.putExtra("request_code",REQUEST_CODE);
                    intent.putExtra("mobile",mobile);
                    startActivityForResult(intent,1);
                    onActivityresult();
                    finish();
                }

            }
        });
    }

    public void onActivityresult() {
        //progressbar();
        AES aes = new AES();
        aes.initFromStrings("CHuO1Fjd8YgJqTyapibFBQ==", "e3IYYJC2hxe24/EO");
        String e_name, e_mobile, e_email, e_password;
        try {
            Toast.makeText(SignUpActivity.this,"Saving Data on DB",Toast.LENGTH_SHORT).show();
            e_name = aes.encrypt(name);
            e_mobile = aes.encrypt(mobile);
            e_email = aes.encrypt(email);
            e_password = aes.encrypt(password);
            UserHelperClass userHelperClass = new UserHelperClass(e_name, e_mobile, e_email, e_password);
            set_error.setVisibility(View.INVISIBLE);
            myRef.child(mobile).setValue(userHelperClass);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void progressbar() {
        prograce_bar = new PrograceBar(SignUpActivity.this);
        prograce_bar.showDialog();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //prograce_bar.dismissbar();
                //showerror();
            }
        }, 500);
    }
    public void showerror() {
        DBTakingLoingTime = new dialogForDBTakingLoingTime(SignUpActivity.this);
        DBTakingLoingTime.showDialog();
        prograce_bar.dismissbar();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //DBTakingLoingTime.dismissbar();
            }
        }, 3000);
    }
}