package com.example.keys.aman.app.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.keys.R;
import com.example.keys.aman.app.AES;
import com.example.keys.aman.app.PrograceBar;
import com.example.keys.aman.app.changePasswordActivity;
import com.example.keys.aman.app.signin_login.LogInActivity;
import com.example.keys.aman.app.signin_login.SignUpActivity;
import com.example.keys.aman.app.signin_login.UserHelperClass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class OTPVerification extends AppCompatActivity {

    Button btn_verify, btn_send_otp;
    TextInputLayout til_otp, til_mobile;
    TextInputEditText tiet_otp, tiet_mobile;
    TextView tv_otpverification_text;
    SharedPreferences sharedPreferences;
    ImageView img_back;
    String mobile;
    String verificationCodeBySystem;
    String comingrequestcode;
    String comingmobileno;
    String comingname, comingemail, comingpassword;
    private String uid;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpverification);
        sharedPreferences = getSharedPreferences(SignUpActivity.SHARED_PREF_ALL_DATA, MODE_PRIVATE);
        btn_verify = findViewById(R.id.btn_verify);
        til_otp = findViewById(R.id.til_otp);
        tiet_otp = findViewById(R.id.tiet_otp);
        til_mobile = findViewById(R.id.til_mobile);
        tiet_mobile = findViewById(R.id.tiet_mobile);
        btn_send_otp = findViewById(R.id.btn_send_otp);
        tv_otpverification_text = findViewById(R.id.tv_otpverification_text);
        img_back = findViewById(R.id.img_back);
        //TODO Check: correct the feature, when OTP is sent to another device, will able to verify
        //TODO Check: add country code feature in this Activity

        //Hide mobile no and
        Intent intent = getIntent();
        comingrequestcode = intent.getStringExtra("request_code");
        if (comingrequestcode == null) {
            comingrequestcode = "this";
        }
        comingname = intent.getStringExtra("name");
        comingmobileno = intent.getStringExtra("mobile");
        comingemail = intent.getStringExtra("email");
        comingpassword = intent.getStringExtra("password");
        Toast.makeText(OTPVerification.this, comingrequestcode, Toast.LENGTH_SHORT).show();
        System.out.println("coming from" + comingname+comingmobileno+comingemail+comingpassword);

        if (comingrequestcode.equals("SignInActivity")) {
            Toast.makeText(OTPVerification.this, comingmobileno, Toast.LENGTH_SHORT).show();
            tiet_mobile.setText(comingmobileno);

        }

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(OTPVerification.this, LogInActivity.class));
                finish();
            }
        });
        btn_send_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendVerificationCodeToUser(comingmobileno);
                tv_otpverification_text.setVisibility(View.VISIBLE);
                tv_otpverification_text.setText("OTP has sent to registered mobile no");

            }
        });

        btn_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = Objects.requireNonNull(tiet_otp.getText()).toString();
                progressbar();
                verifyCode(code);
            }
        });
    }

    private void sendVerificationCodeToUser(String mobile) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + mobile,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks);

    }

    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationCodeBySystem = s;
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                progressbar();
                tiet_otp.setText(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            System.out.println(e.getMessage());
            Toast.makeText(OTPVerification.this, "Failed!! /n" + e.getMessage(), Toast.LENGTH_SHORT).show();
            tv_otpverification_text.setVisibility(View.VISIBLE);
            tv_otpverification_text.setText(e.getMessage());
        }
    };

    private void verifyCode(String codebyUser) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCodeBySystem, codebyUser);
        signInTheUserByCredsntial(credential);
    }

    private void signInTheUserByCredsntial(PhoneAuthCredential credential) {

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(OTPVerification.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (comingrequestcode.equals("SignInActivity")) {


////                                SignUpActivity SA = new SignUpActivity();
////                                SA.onActivityresult();
                                Toast.makeText(OTPVerification.this, "Going to SignIn Activity!!", Toast.LENGTH_LONG).show();
//                                tiet_mobile.setText(comingmobileno);
////                                SignUpActivity.AES_KEY = PassGenActivity.generateRandomPassword(22,true,true,true, false);
////                                SignUpActivity.AES_KEY = SignUpActivity.AES_KEY + "==";
////                                SignUpActivity.AES_KEY = PassGenActivity.generateRandomPassword(16,true,true,true, false);
//                                Toast.makeText(OTPVerification.this, comingname +  comingemail + comingpassword, Toast.LENGTH_SHORT).show();
//                                onActivityresult(comingname, comingemail, comingpassword);
//                                System.out.println(comingname +  comingemail + comingpassword);
                                Intent intentresult = getIntent();
                                setResult(RESULT_OK, intentresult);
                                Toast.makeText(OTPVerification.this, "Successful!!", Toast.LENGTH_LONG).show();
                                finish();
                            }else if (comingrequestcode.equals("this")){
                                Toast.makeText(OTPVerification.this, "Successful1122", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(OTPVerification.this, "Successful!!", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(OTPVerification.this, changePasswordActivity.class);
                                intent.putExtra("mobile", mobile);
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            Toast.makeText(OTPVerification.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    public void progressbar() {
        final PrograceBar prograce_bar = new PrograceBar(OTPVerification.this);
        prograce_bar.showDialog();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                prograce_bar.dismissbar();
            }
        }, 1000);
    }

    public void onActivityresult(String name, String email, String password) {
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference("signupdata");
        myRef.child(mobile).setValue("Hello");

        //progressbar();
        AES aes = new AES();
        aes.initFromStrings(SignUpActivity.AES_KEY,SignUpActivity.AES_IV);
        String e_name, e_mobile, e_email, e_password;
        try {
            Toast.makeText(OTPVerification.this,"Saving Data on DB",Toast.LENGTH_SHORT).show();
            e_name = aes.encrypt(name);
            e_mobile = aes.encrypt(mobile);
            e_email = aes.encrypt(email);
            e_password = aes.encrypt(password);
            String key = sharedPreferences.getString(SignUpActivity.AES_KEY,null);
            String iv = sharedPreferences.getString(SignUpActivity.AES_IV, null);
            UserHelperClass userHelperClass = new UserHelperClass(e_name, e_mobile, e_email, e_password, key, iv, uid);

            if (comingrequestcode.equals("ProfileActivity")) {
                Toast.makeText(OTPVerification.this,"Updated!",Toast.LENGTH_SHORT).show();
            }

            myRef.child(mobile).setValue(userHelperClass);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}