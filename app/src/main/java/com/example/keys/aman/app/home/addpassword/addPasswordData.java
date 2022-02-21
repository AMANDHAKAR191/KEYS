package com.example.keys.aman.app.home.addpassword;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.keys.aman.app.AES;
import com.example.keys.aman.app.home.PassGenActivity;
import com.example.keys.R;
import com.example.keys.aman.app.home.ShowCardviewDataActivity;
import com.example.keys.aman.app.home.myadaptor;
import com.example.keys.aman.app.signin_login.SignUpActivity;
import com.example.keys.aman.app.home.HomeActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class addPasswordData extends AppCompatActivity {

    private static final int REQUEST_DETAIL_CODE = 1;
    TextInputLayout til_login, til_password, til_website;
    TextInputEditText tiet_addlogindata, tiet_addpassworddata, tiet_addwebsitedata;
    Button btn_submit, bt_genrate_password;
    String getnumber;
    ImageView img_back;
    TextView tv_error;

    SharedPreferences sharedPreferences;
    String comingrequestcode;
    String coming_loginname, coming_loginpassword, coming_loginwebsite;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_password_data);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);
        sharedPreferences = getSharedPreferences(SignUpActivity.SHARED_PREF_ALL_DATA, MODE_PRIVATE);


        til_login = findViewById(R.id.til_addlogindata);
        til_password = findViewById(R.id.til_addpassworddata);
        til_website = findViewById(R.id.til_addwebsitedata);
        btn_submit = findViewById(R.id.btn_submit);
        btn_submit.setText("Submit");
        img_back = findViewById(R.id.img_back);
        tiet_addlogindata = findViewById(R.id.tiet_addlogindata);
        tiet_addpassworddata = findViewById(R.id.tiet_addpassworddata);
        tiet_addwebsitedata = findViewById(R.id.tiet_addwebsitedata);
        bt_genrate_password = findViewById(R.id.bt_genrate_password);
        tv_error = findViewById(R.id.tv_error);
        //TODO Check 7: clean password field every time
        //TODO Check: enable generate password feature
        bt_genrate_password.setVisibility(View.GONE);


        //Hide mobile no and
        Intent intent = getIntent();
        comingrequestcode = intent.getStringExtra("request_code");
        if (comingrequestcode == null) {
            comingrequestcode = "this";
        }
        Toast.makeText(addPasswordData.this, comingrequestcode, Toast.LENGTH_LONG).show();
        coming_loginname = intent.getStringExtra("loginname");
        coming_loginpassword = intent.getStringExtra("loginpassowrd");
        coming_loginwebsite = intent.getStringExtra("loginwebsite");
        Toast.makeText(addPasswordData.this, comingrequestcode, Toast.LENGTH_SHORT).show();
        if (comingrequestcode.equals("ShowCardviewDataActivity")) {
            btn_submit.setText("Update");
            Toast.makeText(addPasswordData.this, coming_loginpassword, Toast.LENGTH_SHORT).show();
            tiet_addlogindata.setText(coming_loginname);
            tiet_addpassworddata.setText(coming_loginpassword);
            tiet_addwebsitedata.setText(coming_loginwebsite);
            tiet_addwebsitedata.setEnabled(false);
        }else if (comingrequestcode.equals("from_website_adaptor")){
            tiet_addwebsitedata.setText("Hello");
        }

        tiet_addpassworddata.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                bt_genrate_password.setVisibility(View.VISIBLE);
                return false;
            }
        });
        recyclerviewsetdata();


    }

    private void addData() {
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


            addDataRef.child(addlogin).setValue(addDataHelperClass);
            Log.d(SignUpActivity.TAG, "done");
            Toast.makeText(addPasswordData.this, "Done", Toast.LENGTH_SHORT).show();

            if (comingrequestcode.equals("ShowCardviewDataActivity")) {
                Intent intent = new Intent(addPasswordData.this, ShowCardviewDataActivity.class);
                intent.putExtra("resultlogin", addlogin);
                intent.putExtra("resultpassword", addpasword);
                intent.putExtra("resultwebsite", addwesite);
                startActivity(intent);
                finish();
            }

            startActivity(new Intent(addPasswordData.this, HomeActivity.class));
            finish();
        }


    }

    public void recyclerviewsetdata() {
        RecyclerView recyclerView;
        DatabaseReference databaseReference;
        websiteListAdaptor websiteListAdaptor;
        ArrayList<websiteHelper> dataholder;


        recyclerView = findViewById(R.id.recview);
        String mobile = sharedPreferences.getString(SignUpActivity.KEY_USER_MOBILE, null);
        databaseReference = FirebaseDatabase.getInstance().getReference("websitelist");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dataholder = new ArrayList<>();
        websiteListAdaptor = new websiteListAdaptor(dataholder, getApplicationContext());
        recyclerView.setAdapter(websiteListAdaptor);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true);
        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.setItemAnimator(new DefaultItemAnimator());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        websiteHelper data = ds.getValue(websiteHelper.class);
                        System.out.println(data);
                        //Toast.makeText(addPasswordData.this,data,Toast.LENGTH_SHORT).show();
                        assert data != null;
                        dataholder.add(data);
                    }
                    websiteListAdaptor.notifyDataSetChanged();

                } else {
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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

    public void sumbit_or_updatedata(View view) {
        addData();
    }

    public void goback(View view) {
        overridePendingTransition(R.anim.downward, R.anim.upward);
        finish();
    }

    public void genratepassword(View view) {
        String REQUEST_CODE = "addPasswordData";
        Toast.makeText(addPasswordData.this, "PassGenActivity", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(addPasswordData.this, PassGenActivity.class);
        intent.putExtra("requestCode", REQUEST_CODE);
        startActivityForResult(intent, REQUEST_DETAIL_CODE);
    }
}