package com.example.keys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.keys.signin_login.SignUpActivity;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class changePasswordActivity extends AppCompatActivity {

    TextInputLayout til_oldpassowrd, til_newpassword, til_conformpassword;
    String passwordFromDB,mobileFromDB;
    SharedPreferences sharedPreferences;
    Button btn_sumbit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        sharedPreferences = getSharedPreferences(SignUpActivity.SHARED_PREF_ALL_DATA,MODE_PRIVATE);
        til_oldpassowrd = findViewById(R.id.til_old_password);
        til_newpassword = findViewById(R.id.til_new_password);
        til_conformpassword = findViewById(R.id.til_new_Conform_password);
        btn_sumbit = findViewById(R.id.btnsubmit);



        btn_sumbit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                String mobile = intent.getStringExtra("mobile");
                System.out.println(mobile);
                String userEnteredoldPassword = Objects.requireNonNull(til_oldpassowrd.getEditText()).getText().toString();
                System.out.println(userEnteredoldPassword);
                String userEnterednewPassword = Objects.requireNonNull(til_newpassword.getEditText()).getText().toString();
                System.out.println(userEnterednewPassword);
                String userEnteredconformPassword = Objects.requireNonNull(til_conformpassword.getEditText()).getText().toString();
                System.out.println(userEnteredconformPassword);
                Toast.makeText(changePasswordActivity.this,userEnterednewPassword,Toast.LENGTH_SHORT).show();
                changePassword(mobile,userEnteredoldPassword,userEnterednewPassword,userEnteredconformPassword);
            }
        });

    }
    private void changePassword(String mobile, String userEnteredoldPassword, String userEnterednewPassword, String userEnteredconformPassword){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("signupdata");
        Query checkUser = reference.orderByChild("mobile").equalTo(mobile);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                AES aes = new AES();
                aes.initFromStrings("CHuO1Fjd8YgJqTyapibFBQ==", "e3IYYJC2hxe24/EO");
                if (dataSnapshot.exists()){
                    Toast.makeText(changePasswordActivity.this, "User exist", Toast.LENGTH_SHORT).show();
                    passwordFromDB = dataSnapshot.child(mobile).child("password").getValue(String.class);
                    //mobileFromDB = dataSnapshot.child(mobile).child("mobile").getValue(String.class);
                    System.out.println(mobileFromDB);
                    if(validatePassword(passwordFromDB,userEnteredoldPassword,userEnterednewPassword,userEnteredconformPassword)){
                        Toast.makeText(changePasswordActivity.this, "Password Changed", Toast.LENGTH_SHORT).show();
                        System.out.println(userEnterednewPassword);
                        try {
                            reference.child(mobile).child("password").setValue(aes.encrypt(userEnterednewPassword));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //startActivity(new Intent(changePasswordActivity.this, HomeActivity.class));
                        //finish();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private boolean validatePassword(String passwordFromDB,String userEnteredoldPassword, String userEnterednewPassword, String userEnteredconformPassword){
        if(userEnterednewPassword.equals("") || userEnteredoldPassword.equals("") || userEnteredconformPassword.equals("")){
            return true;
        }else if(!passwordFromDB.equals(userEnteredoldPassword)){
            System.out.println("password not Matched");
            return false;
        }else if(userEnteredoldPassword.equals(userEnterednewPassword)){
            System.out.println("same password please enter again");
            return false;
        }else return userEnterednewPassword.equals(userEnteredconformPassword);
    }

}