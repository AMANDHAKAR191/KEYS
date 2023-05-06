package com.keys.aman.home.addpassword;


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

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.keys.aman.MyPreference;
import com.keys.aman.PrograceBar;
import com.keys.aman.R;
import com.keys.aman.SplashActivity;
import com.keys.aman.authentication.AppLockCounterClass;
import com.keys.aman.base.TabLayoutActivity;
import com.keys.aman.data.Firebase;
import com.keys.aman.home.HomeFragment;
import com.keys.aman.home.PasswordGeneratorActivity;
import com.keys.aman.home.ShowCardViewDataDialog;
import com.keys.aman.iAES;
import com.keys.aman.iActivityTracking;
import com.keys.aman.signin_login.LogInActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class AddPasswordActivity extends AppCompatActivity implements iActivityTracking {

    public static final String REQUEST_ID = "AddPasswordActivity";
    public static WebsiteListAdaptor adaptor;
    TextInputLayout tilUsername, tilPassword, tilWebsiteName, tilWebsitelink;
    TextInputEditText tietUsername, tietPassword, tietWebsiteName, tietWebsitelink;
    Button btnSubmit, btnGenratePassword;
    ImageView imgBack;
    TextView tvError;
    ScrollView scrollView1, scrollView2;
    SharedPreferences sharedPreferences;
    ActivityResultLauncher<Intent> getResult;
    LogInActivity logInActivity = new LogInActivity();
    TabLayoutActivity tabLayoutActivity = new TabLayoutActivity();
    //todo 2 object calling of AppLockCounterClass
    AppLockCounterClass appLockCounterClass = new AppLockCounterClass(AddPasswordActivity.this, AddPasswordActivity.this);
    MyPreference myPreference;
    String comingRequestCode;
    String comingDate, comingUserName, comingPassword, comingWebsiteName, comingWebsiteLink;
    //    public static String addWebsiteLink;
    String currentDateAndTime;
    iActivityTracking iActivityTracking = new AddPasswordActivity();
    private PrograceBar prograceBar;
    private iAES iAES;
    private RecyclerView recyclerView;
    private ArrayList<WebsiteHelperClass> dataHolder;

    public static String reverseFun(String str) {
//        String str= "This#string%contains^special*characters&.";

        str = str.replaceAll("_", ".");
        System.out.println(str);
        return str;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_password_data);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        //initialize local database
        myPreference = MyPreference.getInstance(this);
        //todo 3 when is coming from background or foreground always isForeground false
        SplashActivity.isForeground = false;
        iActivityTracking.onComingFromActivity();

        //Hooks
        tilUsername = findViewById(R.id.til_username);
        tilPassword = findViewById(R.id.til_password);
        tilWebsiteName = findViewById(R.id.til_website_name);
        tilWebsitelink = findViewById(R.id.til_website_link);
        btnSubmit = findViewById(R.id.btn_submit);
        btnSubmit.setText("Submit");
        imgBack = findViewById(R.id.img_back);
        tietUsername = findViewById(R.id.tiet_website_name);
        tietPassword = findViewById(R.id.tiet_password);
        tietWebsiteName = findViewById(R.id.tiet_website_name);
        tietWebsitelink = findViewById(R.id.tiet_website_link);
        btnGenratePassword = findViewById(R.id.btn_generate_password);
        tvError = findViewById(R.id.tv_error);
        scrollView1 = findViewById(R.id.sv_website_list);
        scrollView2 = findViewById(R.id.sv_fill_data);
        recyclerView = findViewById(R.id.recview_website_list);

        btnGenratePassword.setVisibility(View.GONE);
        tietPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                btnGenratePassword.setVisibility(View.VISIBLE);
                return false;
            }
        });

        getResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    private Intent data;

                    @Override
                    public void onActivityResult(ActivityResult result) {
                        try {
                            data = result.getData();
                            String resultdata = Objects.requireNonNull(data).getStringExtra("saved_Password");
                            tietPassword.setText(resultdata);
                        } catch (Exception e) {
                            String resultdata = " ";
                            tietPassword.setText(resultdata);
                        }


                    }
                });


    }




    public void goBack(View view) {
        //todo 5 if app is going to another activity make isForeground = true
        SplashActivity.isForeground = true;
        finish();
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
    }

    public void generatePassword(View view) {
        //todo 5 if app is going to another activity make isForeground = true
        SplashActivity.isForeground = true;
        Intent intent = new Intent(AddPasswordActivity.this, PasswordGeneratorActivity.class);
        intent.putExtra(logInActivity.REQUEST_CODE_NAME, "addPasswordData");
        getResult.launch(intent);
    }

    public void recyclerViewSetData() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.hasFixedSize();

        Firebase.getInstance(this).getWebsiteListData(new Firebase.WebsiteListCallback() {
            @Override
            public void onWebsiteListLoaded(ArrayList<WebsiteHelperClass> dataHolderWebsiteList) {
                System.out.println("dataHolderWebsiteList: " + dataHolderWebsiteList);
                adaptor = new WebsiteListAdaptor(dataHolder, getApplicationContext(), AddPasswordActivity.this) {
                    @Override
                    public void onPictureClick(String dwebsiteLink, String dwebsitename) {

                        scrollView1.setVisibility(View.INVISIBLE);
                        scrollView2.setVisibility(View.VISIBLE);
                        tietWebsiteName.setText(dwebsitename);
                        if (!dwebsitename.equals("other")) {
                            tietWebsitelink.setText(dwebsiteLink);
                            tietWebsitelink.setEnabled(false);
                        }
                    }

                };
                recyclerView.setAdapter(adaptor);
                adaptor.notifyDataSetChanged();
            }
        });

    }

    protected String fun(String str) {
//        String str= "This#string%contains^special*characters&.";
        if (str.equals("other")) {
            return str;
        }
        System.out.println(str);
        String[] str1 = str.split("/", 4);
        System.out.println(str1[2]);
        str = str1[2].replaceAll("[^a-zA-Z0-9]", "_");
        System.out.println(str);
        return str;
    }

    public void progressbar() {
        prograceBar = new PrograceBar(AddPasswordActivity.this);
        prograceBar.showDialog();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        }, 500);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //todo 9 onStartOperation, it will check app is
        // coming from foreground or background.
        appLockCounterClass.onStartOperation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //todo 10 onPauseOperation, it will check app is
        // going to foreground or background.
        // if UI component made isForeground = true then it
        // is going to another activity then this method will make
        // isForeground = false, so user will not be verified.
        // if UI component is not clicked then it
        // is going in background then this method will make
        // isBackground = true and timer will started,
        // at time of return, user will be verified.
        appLockCounterClass.checkedItem = myPreference.getLockAppSelectedOption();
        appLockCounterClass.onPauseOperation();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //todo 11 app is going to close no to do anything
        SplashActivity.isForeground = true;
        finish();
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
    }

    @Override
    public void onComingFromActivity() {
        //Getting intent
        Intent intent = getIntent();
        comingRequestCode = intent.getStringExtra(logInActivity.REQUEST_CODE_NAME);
        if (comingRequestCode == null) {
            comingRequestCode = "this";
        }
        comingDate = intent.getStringExtra("date");
        comingUserName = intent.getStringExtra("loginname");
        comingPassword = intent.getStringExtra("loginpassowrd");
        comingWebsiteName = intent.getStringExtra("loginwebsite_name");
        comingWebsiteLink = intent.getStringExtra("loginwebsite_link");

        switch (comingRequestCode) {
            case ShowCardViewDataDialog.REQUEST_ID: // for editing the password
                tietWebsitelink.setEnabled(comingWebsiteLink.equals(""));
                btnSubmit.setText("Update");
                tietUsername.setText(comingUserName);
                tietPassword.setText(comingPassword);
                tietWebsiteName.setText(comingWebsiteName);
                tietWebsitelink.setText(comingWebsiteLink);
                tietWebsiteName.setEnabled(false);
                scrollView1.setVisibility(View.GONE);
                scrollView2.setVisibility(View.VISIBLE);
                break;
            case WebsiteListAdaptor.REQUEST_ID:
                Intent intent1 = getIntent();
                String name = intent1.getStringExtra("loginname");
                String website = intent1.getStringExtra("loginwebsite");
                scrollView1.setVisibility(View.GONE);
                scrollView2.setVisibility(View.VISIBLE);
                tietWebsiteName.setText(website);
                break;
            case TabLayoutActivity.REQUEST_ID:
                scrollView1.setVisibility(View.VISIBLE);
                scrollView2.setVisibility(View.GONE);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
                currentDateAndTime = sdf.format(new Date());
                break;
        }
    }
}