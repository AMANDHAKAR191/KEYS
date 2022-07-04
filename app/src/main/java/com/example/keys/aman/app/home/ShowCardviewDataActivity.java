package com.example.keys.aman.app.home;

import static com.example.keys.aman.app.SplashActivity.mRewardedAd;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.keys.R;
import com.example.keys.aman.app.home.addpassword.addPasswordData;
import com.example.keys.aman.app.notes.pinLockFragment;
import com.example.keys.aman.app.signin_login.LogInActivity;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class ShowCardviewDataActivity extends AppCompatActivity {
    TextView /*dis_title*/ dis_login, dis_website, tv_img_title;
    TextInputEditText tiet_pass;
    TextInputLayout til_displaypassword;
    ImageView img_back;
    ImageView website_logo, open_website;
    private String comingdate, loginname, loginpassowrd, loginwebsite_name, loginwebsite_link;
    private Bitmap website_logo1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_show_cardview_data);
        //Hooks
        tv_img_title = findViewById(R.id.tv_img_title);
        dis_login = findViewById(R.id.displaylogin);
        tiet_pass = findViewById(R.id.displaypassword);
        til_displaypassword = findViewById(R.id.til_displaypassword);
        dis_website = findViewById(R.id.displaywebsite);
        img_back = findViewById(R.id.img_back);
        website_logo = findViewById(R.id.website_logo);
        open_website = findViewById(R.id.open_website);

        Intent intent = getIntent();
        comingdate = intent.getStringExtra("date");
        loginname = getIntent().getStringExtra("loginname");
        loginpassowrd = getIntent().getStringExtra("loginpassowrd");
        loginwebsite_name = getIntent().getStringExtra("loginwebsite_name");
        loginwebsite_link = getIntent().getStringExtra("loginwebsite_link");
        dis_login.setText(loginname);
        tiet_pass.setText(loginpassowrd);
        String Title = loginwebsite_name.substring(0, 1).toUpperCase() + loginwebsite_name.substring(1);
        dis_website.setText(loginwebsite_link);
        tv_img_title.setText(Title.replace("_","."));

        til_displaypassword.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println(tiet_pass.getInputType());
                if (tiet_pass.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {


                    tiet_pass.setInputType(InputType.TYPE_CLASS_TEXT |
                            InputType.TYPE_TEXT_VARIATION_PASSWORD);

                } else {
                    if (mRewardedAd != null) {
                        Activity activityContext = ShowCardviewDataActivity.this;
                        mRewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                            @Override
                            public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                                // Handle the reward.
                                int rewardAmount = rewardItem.getAmount();
                                String rewardType = rewardItem.getType();
                            }
                        });
                    } else {
                        Toast.makeText(ShowCardviewDataActivity.this, "The rewarded ad wasn't ready yet.", Toast.LENGTH_SHORT).show();
                    }
                    Intent intent1 = new Intent(getApplicationContext(), pinLockFragment.class);
                    intent1.putExtra(LogInActivity.REQUEST_CODE_NAME, "ShowCardviewDataActivity");
                    intent1.putExtra("title", "Enter Pin");
                    startActivityForResult(intent1, 123);


                }
                tiet_pass.setSelection(tiet_pass.getText().length());

            }
        });
        
        website_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("website_logo1 " +  website_logo1);
                Toast.makeText(ShowCardviewDataActivity.this, "logo fetched!!" + website_logo1, Toast.LENGTH_SHORT).show();
            }
        });

        cardViewDataThreadRunnable dataThreadRunnable = new cardViewDataThreadRunnable(loginwebsite_link);
        new Thread(dataThreadRunnable).start();


//        if (addPasswordData.addWebsiteLink.equals("")){
//            open_website.setVisibility(View.INVISIBLE);
//        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            String result = data.getStringExtra("result");
            if (result.equals("yes")) {
                tiet_pass.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            }
        }
    }

    public void editdata(View view) {
        Intent intent = new Intent(ShowCardviewDataActivity.this, addPasswordData.class);
        intent.putExtra(LogInActivity.REQUEST_CODE_NAME, "ShowCardviewDataActivity");
        intent.putExtra("date", comingdate);
        intent.putExtra("loginname", loginname);
        intent.putExtra("loginpassowrd", loginpassowrd);
        intent.putExtra("loginwebsite_name", loginwebsite_name);
        intent.putExtra("loginwebsite_link", loginwebsite_link);
        startActivity(intent);
    }

    public void goback(View view) {
        finish();
        overridePendingTransition(0, R.anim.slide_out_down);
    }

//    public void openWebsite(View view) {
//        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(addPasswordData.addWebsiteLink));
//        Toast.makeText(this, "Opening Website", Toast.LENGTH_SHORT).show();
//        startActivity(intent);
//    }

    private static Bitmap fetchFavicon(Uri uri) {
        final Uri iconUri = uri.buildUpon().path("favicon.ico").build();

        InputStream is = null;
        BufferedInputStream bis = null;
        try {
            URLConnection conn = new URL(iconUri.toString()).openConnection();
            conn.connect();
            is = conn.getInputStream();
            bis = new BufferedInputStream(is, 8192);
            return BitmapFactory.decodeStream(bis);
        } catch (IOException e) {
            return null;
        }
    }

    public class cardViewDataThreadRunnable implements Runnable {

        private String loginwebsite_link1;

        public cardViewDataThreadRunnable (String loginwebsite_link){
            this.loginwebsite_link1 = loginwebsite_link;
        }

        Handler handler = new Handler();
        @Override
        public void run() {
            website_logo1 = fetchFavicon(Uri.parse(loginwebsite_link1));
            handler.post(new Runnable() {
                @Override
                public void run() {

                    website_logo.setImageBitmap(website_logo1);
                }
            });
        }
    }


}