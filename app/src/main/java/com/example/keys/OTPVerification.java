package com.example.keys;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

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


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpverification);
        sharedPreferences = getSharedPreferences(SignUpActivity.SHARED_PREF_ALL_DATA, MODE_PRIVATE);
        btn_verify = (Button) findViewById(R.id.btn_verify);
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
        comingmobileno = intent.getStringExtra("mobile");
        Toast.makeText(OTPVerification.this, comingrequestcode, Toast.LENGTH_SHORT).show();

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

//                AES aes = new AES();
//                aes.initFromStrings("CHuO1Fjd8YgJqTyapibFBQ==", "e3IYYJC2hxe24/EO");
//                progressbar();
//                mobile = til_mobile.getEditText().getText().toString();
//                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("signupdata");
//                Query checkUser = null;
//                try {
//                    checkUser = reference.orderByChild("mobile").equalTo(aes.encrypt(mobile));
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        if (dataSnapshot.exists()) {
//                            sendVerificationCodeToUser(mobile);
//                            tv_otpverification_text.setVisibility(View.VISIBLE);
//                            tv_otpverification_text.setText("OTP has sent to registered mobile no");
//                        } else if (!dataSnapshot.exists()){
//                            tv_otpverification_text.setVisibility(View.VISIBLE);
//                            tv_otpverification_text.setText("mobile no is not registered!!");
//                            tv_otpverification_text.setTextColor(Color.RED);
//                        }else {
//
//                        }
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//
//                    }
//                });
                //String mobile = sharedPreferences.getString(SignUpActivity.KEY_USER_MOBILE,null);

            }
        });

        btn_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = tiet_otp.getText().toString();
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

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
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
                verifyCode(code);
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
//                                SignUpActivity SA = new SignUpActivity();
//                                SA.onActivityresult();
                                Toast.makeText(OTPVerification.this, "Going to SignIn Activity!!", Toast.LENGTH_LONG).show();
                                tiet_mobile.setText(comingmobileno);
                                Intent resultIntent = new Intent();
                                resultIntent.putExtra("result", 1);
                                setResult(RESULT_OK,resultIntent);
                                finish();
                            } else {
                                Toast.makeText(OTPVerification.this, "Successful!!", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(OTPVerification.this, changePasswordActivity.class);
                                intent.putExtra("mobile", mobile);
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            Toast.makeText(OTPVerification.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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

}