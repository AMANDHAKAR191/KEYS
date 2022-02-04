package com.example.keys;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.keys.baseactivitys.HomeActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;
import java.util.Random;

public class addPasswordData extends AppCompatActivity {

    private static final int REQUEST_DETAIL_CODE = 1;
    TextInputLayout til_login, til_password, til_website;
    TextInputEditText tiet_addpassworddata;
    Button btn_submit, bt_genrate_password;
    String getnumber;
    ImageView img_back;
    TextView tv_error;

    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_password_data);
        sharedPreferences = getSharedPreferences(SignUpActivity.SHARED_PREF_ALL_DATA, MODE_PRIVATE);


        til_login = findViewById(R.id.til_addlogindata);
        til_password = findViewById(R.id.til_addpassworddata);
        til_website = findViewById(R.id.til_addwebsitedata);
        btn_submit = findViewById(R.id.btn_submit);
        img_back = findViewById(R.id.img_back);
        tiet_addpassworddata = findViewById(R.id.et_addpassworddata);
        bt_genrate_password = findViewById(R.id.bt_genrate_password);
        tv_error = findViewById(R.id.tv_error);
        //TODO Check 7: clean password field every time
        //TODO Check: enable generate password feature
        bt_genrate_password.setVisibility(View.GONE);
        tiet_addpassworddata.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                bt_genrate_password.setVisibility(View.VISIBLE);
                return false;
            }
        });
        bt_genrate_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(addPasswordData.this, PassGenActivity.class);
                startActivityForResult(intent, REQUEST_DETAIL_CODE);

            }
        });
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(addPasswordData.this, HomeActivity.class));
                finish();
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    addData();
            }
        });

    }

    private void addData(){
        String addlogin = Objects.requireNonNull(til_login.getEditText()).getText().toString();
        String addpasword = Objects.requireNonNull(til_password.getEditText()).getText().toString();
        String addwesite = Objects.requireNonNull(til_website.getEditText()).getText().toString();
        if (addlogin.equals("") || addpasword.equals("") || addwesite.equals("")) {
            tv_error.setVisibility(View.VISIBLE);
            tv_error.setText("Please enter all Fields");
            tv_error.setTextColor(Color.RED);
        } else {
            String e_addlogin = "", e_addpassword = "", e_addwebsite = "";
            String sign_mobile = sharedPreferences.getString(SignUpActivity.KEY_USER_MOBILE, null);
            System.out.println("ShearedPreference " + sign_mobile);
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference addDataRef = firebaseDatabase.getReference("addpassworddata").child(sign_mobile).child(addwesite);
                AES aes = new AES();
            aes.initFromStrings("CHuO1Fjd8YgJqTyapibFBQ==", "e3IYYJC2hxe24/EO");
            try {
                e_addlogin = aes.encrypt(addlogin);
                e_addpassword = aes.encrypt(addpasword);
                e_addwebsite = aes.encrypt(addwesite);
            } catch (Exception e) {
                e.printStackTrace();
            }
            addDataHelperClass addDataHelperClass = new addDataHelperClass(e_addlogin, e_addpassword, e_addwebsite);
            GetNextNumber getNextNumber = new GetNextNumber(0);
            getnumber = Integer.toString(getNextNumber.getNext());

            String fullString = getnumber;
            addDataRef.child(addlogin).setValue(addDataHelperClass);
            Log.d(SignUpActivity.TAG, "done");
            Toast.makeText(addPasswordData.this, "Done", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(addPasswordData.this, HomeActivity.class));
            finish();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //add_password_data.setText("");

        if (requestCode == REQUEST_DETAIL_CODE) {
//            if(resultCode == 1){
            System.err.println("requestCode: " + requestCode + "resultCode: " + resultCode);
            assert data != null;
            String resultdata = data.getStringExtra("saved_Password");
            System.err.println("result: " + resultdata);
            tiet_addpassworddata.setText(resultdata);
//            }
        }
    }

}