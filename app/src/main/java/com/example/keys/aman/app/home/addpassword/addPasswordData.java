package com.example.keys.aman.app.home.addpassword;

import static com.example.keys.aman.app.SplashActivity.mInterstitialAd;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
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
import com.example.keys.aman.app.PrograceBar;
import com.example.keys.aman.app.base.tabLayoutActivity;
import com.example.keys.aman.app.home.HomeActivity;
import com.example.keys.aman.app.home.PassGenActivity;
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
    TextInputLayout tilLogin, tilPassword, tilWebsite, tilWebsitelink;
    TextInputEditText tietAddLoginData, tietAddPasswordData, tietAddWebsiteData, tietAddWebsiteLinkData;
    Button btnSubmit, btnGenratePassword;
    ImageView imgBack;
    TextView tvError;
    ScrollView scrollView1, scrollView2;
    SharedPreferences sharedPreferences;
    String uid;
    String comingRequestCode;
    String comingDate, comingLoginName, comingLoginPassword, comingLoginWebsiteName, comingLoginWebsiteLink;
//    public static String addWebsiteLink;
    private String currentDateAndTime;
    public static myadaptorforaddpassword adaptor;

    String s1 = "https://github.com/";
    String s2 = "https://www.linkedin.com/";
    String s3 = "https://account.microsoft.com/account?lang=en-us";
    String s4 = "https://www.amazon.in/ap/signin?openid.pape.max_auth_age=0&openid.return_to=https%3A%2F%2Fwww.amazon.in%2F%3Fref_%3Dnav_signin&openid.identity=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0%2Fidentifier_select&openid.assoc_handle=inflex&openid.mode=checkid_setup&openid.claimed_id=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0%2Fidentifier_select&openid.ns=http%3A%2F%2Fspecs.openid.net%2Fauth%2F2.0&";
    String s5 = "https://technophilia.in/my-account/";
    String s6 = "https://www.flipkart.com/";
    String s7 = "https://www.netflix.com/in/login?nextpage=https%3A%2F%2Fwww.netflix.com%2FYourAccount";
    String s8 = "https://accounts.google.com/AddSession/identifier?service=accountsettings&continue=https%3A%2F%2Fmyaccount.google.com%2F%3Futm_source%3Dsign_in_no_continue%26pli%3D1&ec=GAlAwAE&flowName=GlifWebSignIn&flowEntry=AddSession";
    String s9 = "https://www.facebook.com/";
    String s10 = "https://www.instagram.com/";
    String s11 = "other";


    private addDataHelperClass addDataHelperClass;
    private DatabaseReference databaseReference;
    private PrograceBar prograceBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_password_data);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        sharedPreferences = getSharedPreferences(LogInActivity.SHARED_PREF_ALL_DATA, MODE_PRIVATE);


        tilLogin = findViewById(R.id.til_addlogindata);
        tilPassword = findViewById(R.id.til_addpassworddata);
        tilWebsite = findViewById(R.id.til_addwebsitedata);
        tilWebsitelink = findViewById(R.id.til_addwebsitelinkdata);
        btnSubmit = findViewById(R.id.btn_submit);
        btnSubmit.setText("Submit");
        imgBack = findViewById(R.id.img_back);
        tietAddLoginData = findViewById(R.id.tiet_addlogindata);
        tietAddPasswordData = findViewById(R.id.tiet_addpassworddata);
        tietAddWebsiteData = findViewById(R.id.tiet_addwebsitedata);
        tietAddWebsiteLinkData = findViewById(R.id.tiet_addwebsitelinkdata);
        btnGenratePassword = findViewById(R.id.bt_genrate_password);
        tvError = findViewById(R.id.tv_error);
        scrollView1 = findViewById(R.id.sv_recview);
        scrollView2 = findViewById(R.id.sv_filldata);

        btnGenratePassword.setVisibility(View.GONE);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();


        //Getting intent
        Intent intent = getIntent();
        comingRequestCode = intent.getStringExtra(LogInActivity.REQUEST_CODE_NAME);
        if (comingRequestCode == null) {
            comingRequestCode = "this";
        }
        comingDate = intent.getStringExtra("date");
        comingLoginName = intent.getStringExtra("loginname");
        comingLoginPassword = intent.getStringExtra("loginpassowrd");
        comingLoginWebsiteName = intent.getStringExtra("loginwebsite_name");
        comingLoginWebsiteLink = intent.getStringExtra("loginwebsite_link");

        if (comingRequestCode.equals("ShowCardviewDataActivity")) {
            if (comingLoginWebsiteLink.equals("")){
                tietAddWebsiteLinkData.setEnabled(true);
            }else {
                tietAddWebsiteLinkData.setEnabled(false);
            }
            btnSubmit.setText("Update");
            tietAddLoginData.setText(comingLoginName);
            tietAddPasswordData.setText(comingLoginPassword);
            tietAddWebsiteData.setText(comingLoginWebsiteName);
            tietAddWebsiteLinkData.setText(comingLoginWebsiteLink);
            tietAddWebsiteData.setEnabled(false);
            scrollView1.setVisibility(View.GONE);
            scrollView2.setVisibility(View.VISIBLE);
        } else if (comingRequestCode.equals("myadaptorforaddpassword")) {
            Intent intent1 = getIntent();
            String name = intent1.getStringExtra("loginname");
            String website = intent1.getStringExtra("loginwebsite");
            scrollView1.setVisibility(View.GONE);
            scrollView2.setVisibility(View.VISIBLE);
            tietAddWebsiteData.setText(website);
        } else if (comingRequestCode.equals("HomeActivity")) {
            scrollView1.setVisibility(View.VISIBLE);
            scrollView2.setVisibility(View.GONE);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
            currentDateAndTime = sdf.format(new Date());

        }


        tietAddPasswordData.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                btnGenratePassword.setVisibility(View.VISIBLE);
                return false;
            }
        });

        recyclerViewSetData();

        if (mInterstitialAd != null) {
            mInterstitialAd.show(addPasswordData.this);
        } else {
            Toast.makeText(addPasswordData.this, "The interstitial ad wasn't ready yet.", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == REQUEST_DETAIL_CODE) {
            // TODO : Check Point
//            if(resultCode == 1){

            String resultdata = data.getStringExtra("saved_Password");
            tietAddPasswordData.setText(resultdata);
//            }
        }
    }

    private void addData() {
        String addlogin = Objects.requireNonNull(tilLogin.getEditText()).getText().toString().trim();
        String addpasword = Objects.requireNonNull(tilPassword.getEditText()).getText().toString();
        String addwesitename = Objects.requireNonNull(tilWebsite.getEditText()).getText().toString().toLowerCase().trim();
        String addwebsitelink = Objects.requireNonNull(tilWebsitelink.getEditText()).getText().toString().toLowerCase().trim();

        if (addwebsitelink.equals("")){
            addwebsitelink = "_";
        }


        if (addlogin.equals("") || addpasword.equals("") || addwesitename.equals("")) {
            tvError.setVisibility(View.VISIBLE);
            tvError.setText("Please enter all Fields");
            tvError.setTextColor(Color.RED);
        } else {
            String e_addlogin = "", e_addpassword = "", e_addwebsite = "";

            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference addDataRef = firebaseDatabase.getReference("addpassworddata").child(uid).child(addwesitename);
            AES aes = new AES();
            aes.initFromStrings(sharedPreferences.getString(LogInActivity.AES_KEY, null), sharedPreferences.getString(LogInActivity.AES_IV, null));
            try {
                // Double encryption
                // TODO : in future, (if needed) give two key to user for double encryption
                e_addlogin = aes.encrypt(addlogin);
                e_addlogin = aes.encrypt(e_addlogin);
                e_addpassword = aes.encrypt(addpasword);
                e_addpassword = aes.encrypt(e_addpassword);
//                e_addwebsite = aes.encrypt(addwesitename);
            } catch (Exception e) {
                e.printStackTrace();
            }


            if (comingRequestCode.equals("ShowCardviewDataActivity")) {
                addDataHelperClass = new addDataHelperClass(comingDate, e_addlogin, e_addpassword, addwesitename, comingLoginWebsiteLink);
                addDataRef.child(comingDate).setValue(addDataHelperClass);
                Toast.makeText(addPasswordData.this, "Done + ShowCardviewDataActivity", Toast.LENGTH_SHORT).show();


                Intent intent = new Intent(addPasswordData.this, HomeActivity.class);
                intent.putExtra(LogInActivity.REQUEST_CODE_NAME, "addPasswordData");
//                intent.putExtra("resultlogin", addlogin);
//                intent.putExtra("resultpassword", addpasword);
//                intent.putExtra("resultwebsite", addwesitename);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
            } else if (comingRequestCode.equals("HomeActivity")) {
                Toast.makeText(this, "addWebsiteLink " + addwebsitelink, Toast.LENGTH_SHORT).show();
                addDataHelperClass = new addDataHelperClass(currentDateAndTime, e_addlogin, e_addpassword, addwesitename, addwebsitelink);
                addDataRef.child(currentDateAndTime).setValue(addDataHelperClass);
                Toast.makeText(addPasswordData.this, "Done + HomeActivity", Toast.LENGTH_SHORT).show();
            }


            Intent intent1 = new Intent(addPasswordData.this, tabLayoutActivity.class);
            intent1.putExtra(LogInActivity.REQUEST_CODE_NAME, "addPasswordData");
            startActivity(intent1);
            finish();
            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
        }


    }
    public void sumbitOrUpdateData(View view) {
        addData();
    }
    public void goBack(View view) {
        finish();
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
    }
    public void genratePassword(View view) {
        Toast.makeText(addPasswordData.this, "PassGenActivity", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(addPasswordData.this, PassGenActivity.class);
        intent.putExtra(LogInActivity.REQUEST_CODE_NAME, "addPasswordData");
        startActivityForResult(intent, REQUEST_DETAIL_CODE);
    }
    public void recyclerViewSetData() {
        progressbar();
        databaseReference = FirebaseDatabase.getInstance().getReference("website_list");

//        addWebsiteList(databaseReference);


        RecyclerView recyclerView;
        ArrayList<websiteHelper> dataholder;


        recyclerView = findViewById(R.id.recview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dataholder = new ArrayList<>();
        adaptor = new myadaptorforaddpassword(dataholder, getApplicationContext(), this) {
            @Override
            public void onPictureClick(String dwebsiteLink, String dwebsitename) {
                Toast.makeText(context, "comingDate = " + comingDate, Toast.LENGTH_SHORT).show();
                scrollView1.setVisibility(View.INVISIBLE);
                scrollView2.setVisibility(View.VISIBLE);
                tietAddWebsiteData.setText(dwebsitename);
//                addWebsiteLink = dwebsiteLink;
                if (!dwebsitename.equals("other")){
                    tietAddWebsiteLinkData.setText(dwebsiteLink);
                    tietAddWebsiteLinkData.setEnabled(false);
                }
            }
        };
        recyclerView.setAdapter(adaptor);
        recyclerView.hasFixedSize();



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

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
    private void addWebsiteList(DatabaseReference databaseReference) {



        websiteHelper data1 = new websiteHelper(fun(s1), s1);
        databaseReference.child(data1.getWebsite_name()).setValue(data1);


        websiteHelper data2 = new websiteHelper(fun(s2), s2);
        databaseReference.child(data2.getWebsite_name()).setValue(data2);


        websiteHelper data3 = new websiteHelper(fun(s3), s3);
        databaseReference.child(data3.getWebsite_name()).setValue(data3);


        websiteHelper data4 = new websiteHelper(fun(s4), s4);
        databaseReference.child(data4.getWebsite_name()).setValue(data4);


        websiteHelper data5 = new websiteHelper(fun(s5), s5);
        databaseReference.child(data5.getWebsite_name()).setValue(data5);


        websiteHelper data6 = new websiteHelper(fun(s6), s6);
        databaseReference.child(data6.getWebsite_name()).setValue(data6);


        websiteHelper data7 = new websiteHelper(fun(s7), s7);
        databaseReference.child(data7.getWebsite_name()).setValue(data7);


        websiteHelper data8 = new websiteHelper(fun(s8), s8);
        databaseReference.child(data8.getWebsite_name()).setValue(data8);


        websiteHelper data9 = new websiteHelper(fun(s9), s9);
        databaseReference.child(data9.getWebsite_name()).setValue(data9);


        websiteHelper data10 = new websiteHelper(fun(s10), s10);
        databaseReference.child(data10.getWebsite_name()).setValue(data10);

        websiteHelper data11 = new websiteHelper(fun(s11), s11);
        databaseReference.child(data11.getWebsite_name()).setValue(data11);
        prograceBar.dismissbar();
        Toast.makeText(addPasswordData.this, "website added successfully!!", Toast.LENGTH_SHORT).show();


    }
    protected String fun(String str) {
//        String str= "This#string%contains^special*characters&.";
        if (str.equals("other")){
            return str;
        }
        System.out.println(str);
        String[] str1 = str.split("/",4);
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
    public void progressbar() {
        prograceBar = new PrograceBar(addPasswordData.this);
        prograceBar.showDialog();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        }, 500);
    }
}
