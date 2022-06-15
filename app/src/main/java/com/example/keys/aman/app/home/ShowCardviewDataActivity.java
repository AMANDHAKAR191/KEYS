package com.example.keys.aman.app.home;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.keys.R;
import com.example.keys.aman.app.home.addpassword.addPasswordData;
import com.example.keys.aman.app.signin_login.LogInActivity;
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
    ImageButton img_back;
    ImageView website_logo;
    private String comingdate, loginname, loginpassowrd, loginwebsite;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_show_cardview_data);
        //Hooks
        tv_img_title = findViewById(R.id.tv_img_title);
        dis_login = findViewById(R.id.displaylogin);
        tiet_pass = findViewById(R.id.displaypassword);
        til_displaypassword = findViewById(R.id.til_displaypassword);
        dis_website = findViewById(R.id.displaywebsite);
        img_back = findViewById(R.id.img_back);
        website_logo = findViewById(R.id.website_logo);

        Intent intent = getIntent();
        comingdate =  intent.getStringExtra("date");
        loginname = getIntent().getStringExtra("loginname");
        loginpassowrd = getIntent().getStringExtra("loginpassowrd");
        loginwebsite = getIntent().getStringExtra("loginwebsite");
        dis_login.setText(loginname);
        tiet_pass.setText(loginpassowrd);
        String Title = loginwebsite.substring(0,1).toUpperCase() + loginwebsite.substring(1);
        dis_website.setText(addPasswordData.reverseFun(loginwebsite));
        tv_img_title.setText(Title);

        // fetching logo
        website_logo.setImageBitmap(fetchFavicon(Uri.parse(loginwebsite)));



    }

    public void editdata(View view) {
        Intent intent = new Intent(ShowCardviewDataActivity.this, addPasswordData.class);
        intent.putExtra(LogInActivity.REQUEST_CODE_NAME,"ShowCardviewDataActivity");
        intent.putExtra("date",comingdate);
        intent.putExtra("loginname",loginname);
        intent.putExtra("loginpassowrd",loginpassowrd);
        intent.putExtra("loginwebsite",loginwebsite);
        startActivity(intent);
    }

    public void goback(View view) {
        startActivity(new Intent(ShowCardviewDataActivity.this, HomeActivity.class));
        finish();
        overridePendingTransition(0, R.anim.slide_out_down);
    }

    public void openWebsite(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(addPasswordData.addWebsiteLink));
        Toast.makeText(this, "Opening Website", Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }

    private Bitmap fetchFavicon(Uri uri) {
        final Uri iconUri = uri.buildUpon().path("favicon.ico").build();

        InputStream is = null;
        BufferedInputStream bis = null;
        try
        {
            URLConnection conn = new URL(iconUri.toString()).openConnection();
            conn.connect();
            is = conn.getInputStream();
            bis = new BufferedInputStream(is, 8192);
            return BitmapFactory.decodeStream(bis);
        } catch (IOException e) {
            return null;
        }
    }
}