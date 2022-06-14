package com.example.keys.aman.app.home.addpassword;

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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keys.R;
import com.example.keys.aman.app.AES;
import com.example.keys.aman.app.home.HomeActivity;
import com.example.keys.aman.app.home.PassGenActivity;
import com.example.keys.aman.app.notes.BiometricActivity;
import com.example.keys.aman.app.signin_login.LogInActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class addPasswordData extends AppCompatActivity {

    private static final int REQUEST_DETAIL_CODE = 1;
    TextInputLayout til_login, til_password, til_website;
    TextInputEditText tiet_addlogindata, tiet_addpassworddata, tiet_addwebsitedata;
    Button btn_submit, bt_genrate_password;
    ImageView img_back;
    TextView tv_error;
    ScrollView scrollView1, scrollView2;
    SharedPreferences sharedPreferences;
    String uid;
    String comingrequestcode;
    String coming_date, coming_loginname, coming_loginpassword, coming_loginwebsite;
    public static String addWebsiteLink;
    private String currentDateandTime;
    public static myadaptorforaddpassword adaptor;

    String s1 = "";
    String s2 = "https://material.io/components/menus/android#theming-menus";
    String s3 = "https://www.youtube.com/feed/history";
    private addDataHelperClass addDataHelperClass;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_password_data);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        sharedPreferences = getSharedPreferences(LogInActivity.SHARED_PREF_ALL_DATA, MODE_PRIVATE);


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
        scrollView1 = findViewById(R.id.scrollview1);
        scrollView2 = findViewById(R.id.scrollview2);

        bt_genrate_password.setVisibility(View.GONE);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        System.out.println("uid: " + uid);
        Toast.makeText(addPasswordData.this, "Uid: " + uid, Toast.LENGTH_SHORT).show();


        //Hide mobile no and
        Intent intent = getIntent();
        comingrequestcode = intent.getStringExtra(LogInActivity.REQUEST_CODE_NAME);
        if (comingrequestcode == null) {
            comingrequestcode = "this";
        }
        Toast.makeText(addPasswordData.this, comingrequestcode, Toast.LENGTH_LONG).show();
        coming_date = intent.getStringExtra("date");
        coming_loginname = intent.getStringExtra("loginname");
        coming_loginpassword = intent.getStringExtra("loginpassowrd");
        coming_loginwebsite = intent.getStringExtra("loginwebsite");
        Toast.makeText(addPasswordData.this, comingrequestcode, Toast.LENGTH_SHORT).show();
        if (comingrequestcode.equals("ShowCardviewDataActivity")) {
            btn_submit.setText("Update");
            Toast.makeText(addPasswordData.this, coming_date, Toast.LENGTH_SHORT).show();
            Toast.makeText(addPasswordData.this, coming_loginname, Toast.LENGTH_SHORT).show();
            Toast.makeText(addPasswordData.this, coming_loginpassword, Toast.LENGTH_SHORT).show();
            Toast.makeText(addPasswordData.this, coming_loginwebsite, Toast.LENGTH_SHORT).show();
            tiet_addlogindata.setText(coming_loginname);
            tiet_addpassworddata.setText(coming_loginpassword);
            tiet_addwebsitedata.setText(coming_loginwebsite);
            tiet_addwebsitedata.setEnabled(false);
            scrollView1.setVisibility(View.GONE);
            scrollView2.setVisibility(View.VISIBLE);
        } else if (comingrequestcode.equals("myadaptorforaddpassword")) {
            Intent intent1 = getIntent();
            String name = intent1.getStringExtra("loginname");
            String website = intent1.getStringExtra("loginwebsite");
            scrollView1.setVisibility(View.GONE);
            scrollView2.setVisibility(View.VISIBLE);
            tiet_addwebsitedata.setText(website);
        } else if (comingrequestcode.equals("this")) {
            Intent intent2 = new Intent(addPasswordData.this, BiometricActivity.class);
            intent2.putExtra(LogInActivity.REQUEST_CODE_NAME,"this");
            intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent2);
        }
        else if (comingrequestcode.equals("BiometricActivity")) {
            scrollView1.setVisibility(View.VISIBLE);
            scrollView2.setVisibility(View.GONE);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
            currentDateandTime = sdf.format(new Date());
            System.out.println("Dateandtime: " + currentDateandTime);
            Toast.makeText(addPasswordData.this, "Date_time: " + currentDateandTime, Toast.LENGTH_LONG).show();
        }else if (comingrequestcode.equals("HomeActivity")) {
            scrollView1.setVisibility(View.VISIBLE);
            scrollView2.setVisibility(View.GONE);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
            currentDateandTime = sdf.format(new Date());
            System.out.println("Dateandtime: " + currentDateandTime);
            Toast.makeText(addPasswordData.this, "Date_time: " + currentDateandTime, Toast.LENGTH_LONG).show();
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
        String addlogin = Objects.requireNonNull(til_login.getEditText()).getText().toString().trim();
        String addpasword = Objects.requireNonNull(til_password.getEditText()).getText().toString();
        String addwesitename = Objects.requireNonNull(til_website.getEditText()).getText().toString().toLowerCase().trim();

        if (addlogin.equals("") || addpasword.equals("") || addwesitename.equals("")) {
            tv_error.setVisibility(View.VISIBLE);
            tv_error.setText("Please enter all Fields");
            tv_error.setTextColor(Color.RED);
        } else {
            String e_addlogin = "", e_addpassword = "", e_addwebsite = "";

            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference addDataRef = firebaseDatabase.getReference("addpassworddata").child(uid).child(addwesitename);
            AES aes = new AES();
            aes.initFromStrings(sharedPreferences.getString(LogInActivity.AES_KEY, null), sharedPreferences.getString(LogInActivity.AES_IV, null));
            try {
                e_addlogin = aes.encrypt(addlogin);
                e_addpassword = aes.encrypt(addpasword);
//                e_addwebsite = aes.encrypt(addwesitename);
            } catch (Exception e) {
                e.printStackTrace();
            }


            if (comingrequestcode.equals("ShowCardviewDataActivity")) {
                addDataHelperClass = new addDataHelperClass(coming_date, e_addlogin, e_addpassword, addwesitename);
                addDataRef.child(coming_date).setValue(addDataHelperClass);
                Log.d(LogInActivity.TAG, "done");
                Toast.makeText(addPasswordData.this, "Done", Toast.LENGTH_SHORT).show();


                Intent intent = new Intent(addPasswordData.this, HomeActivity.class);
                intent.putExtra(LogInActivity.REQUEST_CODE_NAME,"addPasswordData");
//                intent.putExtra("resultlogin", addlogin);
//                intent.putExtra("resultpassword", addpasword);
//                intent.putExtra("resultwebsite", addwesitename);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
            }else if (comingrequestcode.equals("HomeActivity")){
                addDataHelperClass = new addDataHelperClass(currentDateandTime, e_addlogin, e_addpassword, addwesitename);
                addDataRef.child(currentDateandTime).setValue(addDataHelperClass);
                Log.d(LogInActivity.TAG, "done");
                Toast.makeText(addPasswordData.this, "Done", Toast.LENGTH_SHORT).show();
            }


            Intent intent1 = new Intent(addPasswordData.this, HomeActivity.class);
            intent1.putExtra(LogInActivity.REQUEST_CODE_NAME,"addPasswordData");
            startActivity(intent1);
            finish();
            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == REQUEST_DETAIL_CODE) {
            // TODO : Check Point
//            if(resultCode == 1){
            System.err.println("requestCode: " + requestCode + "resultCode: " + resultCode);
            String resultdata = data.getStringExtra("saved_Password");
            System.err.println("result: " + resultdata);
            tiet_addpassworddata.setText(resultdata);
//            }
        }
    }


    protected void sumbit_or_updatedata(View view) {
        addData();
    }

    public void goback(View view) {
        finish();
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
    }

    public void genratepassword(View view) {
        Toast.makeText(addPasswordData.this, "PassGenActivity", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(addPasswordData.this, PassGenActivity.class);
        intent.putExtra(LogInActivity.REQUEST_CODE_NAME, "addPasswordData");
        startActivityForResult(intent, REQUEST_DETAIL_CODE);
    }

    public void recyclerviewsetdata() {
        databaseReference = FirebaseDatabase.getInstance().getReference("website_list");


        RecyclerView recyclerView;
        ArrayList<websiteHelper> dataholder;


        recyclerView = findViewById(R.id.recview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dataholder = new ArrayList<>();
        adaptor = new myadaptorforaddpassword(dataholder, getApplicationContext(), this){
            @Override
            public void onPictureClick(String dwebsiteLink, String dwebsitename){
                Toast.makeText(context, "coming_date = " + coming_date, Toast.LENGTH_SHORT).show();
                scrollView1.setVisibility(View.INVISIBLE);
                scrollView2.setVisibility(View.VISIBLE);
                tiet_addwebsitedata.setText(dwebsitename);
                addWebsiteLink = dwebsiteLink;
            }
        };
        recyclerView.setAdapter(adaptor);
        recyclerView.hasFixedSize();

//        addWebsiteList(databaseReference);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            websiteHelper data = ds.getValue(websiteHelper.class);
                            dataholder.add(data);

                    }
                    Collections.sort(dataholder, websiteHelper.addDataHelperClassComparator);
                    adaptor.notifyDataSetChanged();

                } else {
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }

    private void addWebsiteList(DatabaseReference databaseReference) {
        websiteHelper data = new websiteHelper(fun(s1),s1);
        databaseReference.child(data.getWebsite_name()).setValue(data);
    }


    protected String fun(String str) {
//        String str= "This#string%contains^special*characters&.";
        String[] str1 = str.split("/");
        System.out.println(str1[2]);
        str = str1[2].replaceAll("[^a-zA-Z0-9]", "_");
        System.out.println(str);
        return str;
    }
    public static String reverseFun(String str) {
//        String str= "This#string%contains^special*characters&.";

        str = str.replaceAll("_", ".");
        System.out.println(str);
        return str;
    }
}
