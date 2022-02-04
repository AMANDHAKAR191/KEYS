package com.example.keys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.keys.baseactivitys.HomeActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LogInActivity extends AppCompatActivity {
    public PrograceBar prograce_bar;
    public dialogForDBTakingLoingTime DBTakingLoingTime;

    TextInputLayout til_login_mobileno, til_login_password;
    TextInputEditText tiet_login_mobileno, tiet_login_password;
    Button login;
    TextView signin_btn, tv_forget_password, set_error;
    String passwordFromDB, nameFromDB, mobileFromDB, emailFromDB;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        sharedPreferences = getSharedPreferences(SignUpActivity.SHARED_PREF_ALL_DATA, MODE_PRIVATE);
        til_login_mobileno = findViewById(R.id.til_login_mobile_no);
        til_login_password = findViewById(R.id.til_login_password);
        tiet_login_mobileno = findViewById(R.id.tiet_login_mobile_no);
        tiet_login_password = findViewById(R.id.tiet_login_password);
        Boolean isrememberme = sharedPreferences.getBoolean(SignUpActivity.KEY_REMEMBER_ME,false);
        System.out.println("LoginActivity1");
        //TODO Check 4: check internet when user click on login and sign in button in login and sign in Activity
        if (isrememberme){
            System.out.println("LoginActivity2");
            tiet_login_mobileno.setText(sharedPreferences.getString(SignUpActivity.KEY_USER_MOBILE,null));
            tiet_login_password.setText(sharedPreferences.getString(SignUpActivity.KEY_USER_PASSSWORD,null));
        }

        til_login_mobileno.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        tv_forget_password = findViewById(R.id.tv_forget_password);
        set_error = findViewById(R.id.set_error);
        set_error.setVisibility(View.INVISIBLE);
        //rememberme = findViewById(R.id.check_remember_me);
        login = findViewById(R.id.b_login);
        signin_btn = findViewById(R.id.b_create_account);

        tv_forget_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               startActivity(new Intent(LogInActivity.this,OTPVerification.class));
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                System.out.println("LoginActivity3");
                String userEnteredmobile, userEnteredPassword;
                userEnteredmobile = Objects.requireNonNull(til_login_mobileno.getEditText()).getText().toString();
                userEnteredPassword = Objects.requireNonNull(til_login_password.getEditText()).getText().toString();
                System.out.println("LoginActivity4");
                checkUser(userEnteredmobile, userEnteredPassword);
            }
        });
        signin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LogInActivity.this, SignUpActivity.class));
            }
        });

    }
    private void checkUser(String userEnteredmobile, String userEnteredPassword) {
        progressbar();
        AES aes = new AES();
        aes.initFromStrings("CHuO1Fjd8YgJqTyapibFBQ==", "e3IYYJC2hxe24/EO");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("signupdata");
        Query checkUser = null;
        try {
            checkUser = reference.orderByChild("mobile").equalTo(aes.encrypt(userEnteredmobile));
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(userEnteredmobile);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(LogInActivity.this, "User exist", Toast.LENGTH_SHORT).show();
                    passwordFromDB = dataSnapshot.child(userEnteredmobile).child("password").getValue(String.class);
                    mobileFromDB = dataSnapshot.child(userEnteredmobile).child("mobile").getValue(String.class);
                    nameFromDB = dataSnapshot.child(userEnteredmobile).child("name").getValue(String.class);
                    emailFromDB = dataSnapshot.child(userEnteredmobile).child("email").getValue(String.class);
                    //System.out.println(mobileFromDB);
                    try {
                        String enc_password = aes.encrypt(userEnteredPassword);
                        Toast.makeText(LogInActivity.this,passwordFromDB + "="+enc_password,Toast.LENGTH_SHORT).show();
                        if (passwordFromDB.equals(enc_password)) {
                            Toast.makeText(LogInActivity.this, "Password Matched", Toast.LENGTH_SHORT).show();
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(SignUpActivity.KEY_USER_MOBILE, userEnteredmobile);
                            editor.putString(SignUpActivity.KEY_USER_PASSSWORD,userEnteredPassword);
                            editor.putString(SignUpActivity.KEY_USER_NAME, nameFromDB);
                            editor.putString(SignUpActivity.KEY_USER_EMAIL, emailFromDB);
                            editor.putBoolean(SignUpActivity.KEY_REMEMBER_ME,true);
                            editor.apply();
                            startActivity(new Intent(LogInActivity.this, HomeActivity.class));
                            prograce_bar.dismissbar();
                            //DBTakingLoingTime.dismissbar();
                            finish();
                        } else {
                            Toast.makeText(LogInActivity.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                            set_error.setVisibility(View.VISIBLE);
                            set_error.setText("Wrong Password");
                            prograce_bar.dismissbar();
                            //DBTakingLoingTime.dismissbar();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(LogInActivity.this, "No such User exist", Toast.LENGTH_SHORT).show();
                    set_error.setVisibility(View.VISIBLE);
                    set_error.setText("No such User exist");
                    prograce_bar.dismissbar();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void progressbar() {
        prograce_bar = new PrograceBar(LogInActivity.this);
        prograce_bar.showDialog();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //prograce_bar.dismissbar();
                //showerror();
            }
        }, 3000);
    }
    public void showerror() {
        DBTakingLoingTime = new dialogForDBTakingLoingTime(LogInActivity.this);
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