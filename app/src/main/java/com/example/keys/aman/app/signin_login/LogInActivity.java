package com.example.keys.aman.app.signin_login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.keys.R;
import com.example.keys.aman.app.AES;
import com.example.keys.aman.app.PrograceBar;
import com.example.keys.aman.app.checkInternetFragment;
import com.example.keys.aman.app.home.HomeActivity;
import com.example.keys.aman.app.notes.BiometricActivity;
import com.example.keys.aman.app.notes.pinLockFragment;
import com.example.keys.aman.app.settings.OTPVerification;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class LogInActivity extends AppCompatActivity {
    public PrograceBar prograce_bar;
    Vibrator vibrator;

    TextInputLayout til_login_mobileno, til_login_password;
    TextInputEditText tiet_login_mobileno, tiet_login_password;
    Button login;
    TextView signin_btn, tv_forget_password, set_error, tv_use_pin;
    ImageView img_use_fingerprint;
    String passwordFromDB, nameFromDB, mobileFromDB, emailFromDB;
    String userEnteredmobile, userEnteredPassword;
    SharedPreferences sharedPreferences;
    public final String REQUEST_CODE = "LogInActivity";
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        sharedPreferences = getSharedPreferences(SignUpActivity.SHARED_PREF_ALL_DATA, MODE_PRIVATE);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        //Hooks
        til_login_mobileno = findViewById(R.id.til_login_mobile_no);
        til_login_password = findViewById(R.id.til_login_password);
        tiet_login_mobileno = findViewById(R.id.tiet_login_mobile_no);
        tiet_login_password = findViewById(R.id.tiet_login_password);
        tv_forget_password = findViewById(R.id.tv_forget_password);
        tv_use_pin = findViewById(R.id.tv_use_pin);
        img_use_fingerprint = findViewById(R.id.img_use_fingerprint);
        set_error = findViewById(R.id.set_error);
        set_error.setVisibility(View.INVISIBLE);
        login = findViewById(R.id.b_login);
        signin_btn = findViewById(R.id.b_create_account);

        // remember me data
        boolean isrememberme = sharedPreferences.getBoolean(SignUpActivity.KEY_REMEMBER_ME, false);
        System.out.println("LoginActivity1");
        //TODO Check 4: check internet when user click on login and sign in button in login and sign in Activity
        if (isrememberme) {
            System.out.println("LoginActivity2");
            tiet_login_mobileno.setText(sharedPreferences.getString(SignUpActivity.KEY_USER_MOBILE, null));
            tiet_login_password.setText(sharedPreferences.getString(SignUpActivity.KEY_USER_PASSSWORD, null));
            login.setFocusable(true);
        }

        // show or hide fingerprint and pin options
        boolean is_use_ingerprint = sharedPreferences.getBoolean(SignUpActivity.KEY_USE_FINGERPRINT, false);
        boolean is_use_pin = sharedPreferences.getBoolean(SignUpActivity.KEY_USE_PIN, false);
        Toast.makeText(LogInActivity.this, is_use_ingerprint + " | " + is_use_pin, Toast.LENGTH_SHORT).show();
        if (is_use_pin && is_use_ingerprint) {
            tv_use_pin.setVisibility(View.VISIBLE);
            img_use_fingerprint.setVisibility(View.VISIBLE);
        }

    }

    private void checkUser(String userEnteredmobile, String userEnteredPassword) {
        progressbar();
        AES aes = new AES();
        aes.initFromStrings(sharedPreferences.getString(SignUpActivity.AES_KEY, null), sharedPreferences.getString(SignUpActivity.AES_IV, null));
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("signupdata");
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Query checkUser = null;
        try {
            checkUser = reference.orderByChild("uid").equalTo(uid);
            System.out.println("<>" + checkUser + uid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(userEnteredmobile);
        Objects.requireNonNull(checkUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(LogInActivity.this, "Checking......", Toast.LENGTH_SHORT).show();
                    passwordFromDB = dataSnapshot.child(userEnteredmobile).child("password").getValue(String.class);
                    mobileFromDB = dataSnapshot.child(userEnteredmobile).child("mobile").getValue(String.class);
                    nameFromDB = dataSnapshot.child(userEnteredmobile).child("name").getValue(String.class);
                    emailFromDB = dataSnapshot.child(userEnteredmobile).child("email").getValue(String.class);
                    try {
                        String enc_password = aes.encrypt(userEnteredPassword);
                        if (passwordFromDB.equals(enc_password)) {
                            Toast.makeText(LogInActivity.this, "Password Matched", Toast.LENGTH_SHORT).show();
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(SignUpActivity.KEY_USER_MOBILE, userEnteredmobile);
                            editor.putString(SignUpActivity.KEY_USER_PASSSWORD, userEnteredPassword);
                            editor.putString(SignUpActivity.KEY_USER_NAME, nameFromDB);
                            editor.putString(SignUpActivity.KEY_USER_EMAIL, emailFromDB);
                            editor.putBoolean(SignUpActivity.KEY_REMEMBER_ME, true);
                            editor.putBoolean(SignUpActivity.KEY_USE_FINGERPRINT, true);
                            editor.putBoolean(SignUpActivity.KEY_USE_PIN, true);
                            editor.apply();
                            startActivity(new Intent(LogInActivity.this, HomeActivity.class));
                            prograce_bar.dismissbar();
                            finish();
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        } else {
                            set_error.setVisibility(View.VISIBLE);
                            set_error.setText("Wrong Password");
                            prograce_bar.dismissbar();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    set_error.setVisibility(View.VISIBLE);
                    set_error.setText("You didn't have any account");
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

            }
        }, 3000);
    }


    public void forgetpassword(View view) {
        startActivity(new Intent(LogInActivity.this, OTPVerification.class));
    }

    public void login(View view) {
        System.out.println("LoginActivity3");
        userEnteredmobile = Objects.requireNonNull(til_login_mobileno.getEditText()).getText().toString();
        userEnteredPassword = Objects.requireNonNull(til_login_password.getEditText()).getText().toString();
        System.out.println("LoginActivity4");
        if (validatedata()) {
            checkUser(userEnteredmobile, userEnteredPassword);
        }
//        if (!isConnected(this)){
//            showCustomDiolog();
//        }else {
//            System.out.println("LoginActivity3");
//            userEnteredmobile = Objects.requireNonNull(til_login_mobileno.getEditText()).getText().toString();
//            userEnteredPassword = Objects.requireNonNull(til_login_password.getEditText()).getText().toString();
//            System.out.println("LoginActivity4");
//            if (validatedata()) {
//                checkUser(userEnteredmobile, userEnteredPassword);
//            }
//        }
    }

    // Check Internet Connection
    private boolean isConnected(LogInActivity logInActivity) {
        ConnectivityManager connectivityManager = (ConnectivityManager) logInActivity.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        return mobileConn != null && mobileConn.isConnected();
    }

    private void showCustomDiolog() {
        checkInternetFragment checkInternet = new checkInternetFragment();
        checkInternet.show(getSupportFragmentManager(), "check internet");
    }
    //Validate Fields
    private boolean validatedata() {
        if (userEnteredmobile.equals("")) {
            set_error.setVisibility(View.VISIBLE);
            return false;
        } else if (userEnteredPassword.equals("")) {
            set_error.setVisibility(View.VISIBLE);
            return false;
        }
        return true;
    }

    public void create_account(View view) {
        startActivity(new Intent(LogInActivity.this, SignUpActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void vibrate(View view) {
        vibrator.vibrate(200);
    }

    public void use_pin(View view) {
        Intent intent = new Intent(LogInActivity.this, pinLockFragment.class);
        intent.putExtra("request_code", REQUEST_CODE);
        startActivity(intent);

    }

    public void use_fingerprint(View view) {
        Intent intent = new Intent(LogInActivity.this, BiometricActivity.class);
        intent.putExtra("request_code", REQUEST_CODE);
        startActivity(intent);

    }
}