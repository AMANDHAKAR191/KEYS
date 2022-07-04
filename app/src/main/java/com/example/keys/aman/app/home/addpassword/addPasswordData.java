package com.example.keys.aman.app.home.addpassword;

import static com.example.keys.aman.app.SplashActivity.mInterstitialAd;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
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
    TextInputLayout til_login, til_password, til_website, til_websitelink;
    TextInputEditText tiet_addlogindata, tiet_addpassworddata, tiet_addwebsitedata, tiet_addwebsitelinkdata;
    Button btn_submit, bt_genrate_password;
    ImageView img_back;
    TextView tv_error;
    ScrollView scrollView1, scrollView2;
    SharedPreferences sharedPreferences;
    String uid;
    String comingrequestcode;
    String coming_date, coming_loginname, coming_loginpassword, coming_loginwebsite_name, coming_loginwebsite_link;
//    public static String addWebsiteLink;
    private String currentDateandTime;
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
    private PrograceBar prograce_bar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_password_data);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        sharedPreferences = getSharedPreferences(LogInActivity.SHARED_PREF_ALL_DATA, MODE_PRIVATE);


        til_login = findViewById(R.id.til_addlogindata);
        til_password = findViewById(R.id.til_addpassworddata);
        til_website = findViewById(R.id.til_addwebsitedata);
        til_websitelink = findViewById(R.id.til_addwebsitelinkdata);
        btn_submit = findViewById(R.id.btn_submit);
        btn_submit.setText("Submit");
        img_back = findViewById(R.id.img_back);
        tiet_addlogindata = findViewById(R.id.tiet_addlogindata);
        tiet_addpassworddata = findViewById(R.id.tiet_addpassworddata);
        tiet_addwebsitedata = findViewById(R.id.tiet_addwebsitedata);
        tiet_addwebsitelinkdata = findViewById(R.id.tiet_addwebsitelinkdata);
        bt_genrate_password = findViewById(R.id.bt_genrate_password);
        tv_error = findViewById(R.id.tv_error);
        scrollView1 = findViewById(R.id.scrollview1);
        scrollView2 = findViewById(R.id.scrollview2);

        bt_genrate_password.setVisibility(View.GONE);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();


        //Hide mobile no and
        Intent intent = getIntent();
        comingrequestcode = intent.getStringExtra(LogInActivity.REQUEST_CODE_NAME);
        if (comingrequestcode == null) {
            comingrequestcode = "this";
        }
        coming_date = intent.getStringExtra("date");
        coming_loginname = intent.getStringExtra("loginname");
        coming_loginpassword = intent.getStringExtra("loginpassowrd");
        coming_loginwebsite_name = intent.getStringExtra("loginwebsite_name");
        coming_loginwebsite_link = intent.getStringExtra("loginwebsite_link");
        if (comingrequestcode.equals("ShowCardviewDataActivity")) {
            if (coming_loginwebsite_link.equals("")){
                tiet_addwebsitelinkdata.setEnabled(true);
            }else {
                tiet_addwebsitelinkdata.setEnabled(false);
            }
            btn_submit.setText("Update");
            tiet_addlogindata.setText(coming_loginname);
            tiet_addpassworddata.setText(coming_loginpassword);
            tiet_addwebsitedata.setText(coming_loginwebsite_name);
            tiet_addwebsitelinkdata.setText(coming_loginwebsite_link);
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
        }
//        else if (comingrequestcode.equals("this")) {
//            Intent intent2 = new Intent(addPasswordData.this, pinLockFragment.class);
//            intent2.putExtra(LogInActivity.REQUEST_CODE_NAME,"this");
//            intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//            startActivity(intent2);
//        }
//        else if (comingrequestcode.equals("BiometricActivity")) {
//            scrollView1.setVisibility(View.VISIBLE);
//            scrollView2.setVisibility(View.GONE);
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
//            currentDateandTime = sdf.format(new Date());
//
//        }
        else if (comingrequestcode.equals("HomeActivity")) {
            scrollView1.setVisibility(View.VISIBLE);
            scrollView2.setVisibility(View.GONE);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
            currentDateandTime = sdf.format(new Date());

        }


        tiet_addpassworddata.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                bt_genrate_password.setVisibility(View.VISIBLE);
                return false;
            }
        });


        recyclerviewsetdata();

        if (mInterstitialAd != null) {
            mInterstitialAd.show(addPasswordData.this);
        } else {
            Toast.makeText(addPasswordData.this, "The interstitial ad wasn't ready yet.", Toast.LENGTH_LONG).show();
        }
    }

    private void addData() {
        String addlogin = Objects.requireNonNull(til_login.getEditText()).getText().toString().trim();
        String addpasword = Objects.requireNonNull(til_password.getEditText()).getText().toString();
        String addwesitename = Objects.requireNonNull(til_website.getEditText()).getText().toString().toLowerCase().trim();
        String addwebsitelink = Objects.requireNonNull(til_websitelink.getEditText()).getText().toString().toLowerCase().trim();

        if (addwebsitelink.equals("")){
            addwebsitelink = "aman";
        }


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


            if (comingrequestcode.equals("ShowCardviewDataActivity")) {
                addDataHelperClass = new addDataHelperClass(coming_date, e_addlogin, e_addpassword, addwesitename,coming_loginwebsite_link);
                addDataRef.child(coming_date).setValue(addDataHelperClass);
                Toast.makeText(addPasswordData.this, "Done + ShowCardviewDataActivity", Toast.LENGTH_SHORT).show();


                Intent intent = new Intent(addPasswordData.this, HomeActivity.class);
                intent.putExtra(LogInActivity.REQUEST_CODE_NAME, "addPasswordData");
//                intent.putExtra("resultlogin", addlogin);
//                intent.putExtra("resultpassword", addpasword);
//                intent.putExtra("resultwebsite", addwesitename);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
            } else if (comingrequestcode.equals("HomeActivity")) {
                Toast.makeText(this, "addWebsiteLink " + addwebsitelink, Toast.LENGTH_SHORT).show();
                addDataHelperClass = new addDataHelperClass(currentDateandTime, e_addlogin, e_addpassword, addwesitename, addwebsitelink);
                addDataRef.child(currentDateandTime).setValue(addDataHelperClass);
                Toast.makeText(addPasswordData.this, "Done + HomeActivity", Toast.LENGTH_SHORT).show();
            }


            Intent intent1 = new Intent(addPasswordData.this, HomeActivity.class);
            intent1.putExtra(LogInActivity.REQUEST_CODE_NAME, "addPasswordData");
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

            String resultdata = data.getStringExtra("saved_Password");
            tiet_addpassworddata.setText(resultdata);
//            }
        }
    }


    public void sumbit_or_updatedata(View view) {
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
                Toast.makeText(context, "coming_date = " + coming_date, Toast.LENGTH_SHORT).show();
                scrollView1.setVisibility(View.INVISIBLE);
                scrollView2.setVisibility(View.VISIBLE);
                tiet_addwebsitedata.setText(dwebsitename);
//                addWebsiteLink = dwebsiteLink;
                if (!dwebsitename.equals("other")){
                    tiet_addwebsitelinkdata.setText(dwebsiteLink);
                    tiet_addwebsitelinkdata.setEnabled(false);
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
        Bitmap website_logo1 = null,website_logo2 = null, website_logo3 = null, website_logo4 = null, website_logo5 = null,
                website_logo6 = null, website_logo7 = null,
                website_logo8 = null, website_logo9 = null,
                website_logo10 = null, website_logo11 = null;



        websiteHelper data1 = new websiteHelper(fun(s1), s1, website_logo1);
        databaseReference.child(data1.getWebsite_name()).setValue(data1);


        websiteHelper data2 = new websiteHelper(fun(s2), s2, website_logo2);
        databaseReference.child(data2.getWebsite_name()).setValue(data2);


        websiteHelper data3 = new websiteHelper(fun(s3), s3, website_logo3);
        databaseReference.child(data3.getWebsite_name()).setValue(data3);


        websiteHelper data4 = new websiteHelper(fun(s4), s4, website_logo4);
        databaseReference.child(data4.getWebsite_name()).setValue(data4);


        websiteHelper data5 = new websiteHelper(fun(s5), s5, website_logo5);
        databaseReference.child(data5.getWebsite_name()).setValue(data5);


        websiteHelper data6 = new websiteHelper(fun(s6), s6, website_logo6);
        databaseReference.child(data6.getWebsite_name()).setValue(data6);


        websiteHelper data7 = new websiteHelper(fun(s7), s7, website_logo7);
        databaseReference.child(data7.getWebsite_name()).setValue(data7);


        websiteHelper data8 = new websiteHelper(fun(s8), s8, website_logo8);
        databaseReference.child(data8.getWebsite_name()).setValue(data8);


        websiteHelper data9 = new websiteHelper(fun(s9), s9, website_logo9);
        databaseReference.child(data9.getWebsite_name()).setValue(data9);


        websiteHelper data10 = new websiteHelper(fun(s10), s10, website_logo10);
        databaseReference.child(data10.getWebsite_name()).setValue(data10);

        websiteHelper data11 = new websiteHelper(fun(s11), s11, website_logo11);
        databaseReference.child(data11.getWebsite_name()).setValue(data11);
        prograce_bar.dismissbar();
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
        prograce_bar = new PrograceBar(addPasswordData.this);
        prograce_bar.showDialog();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        }, 500);
    }
}
