package com.example.keys.aman.app.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.keys.R;
import com.example.keys.aman.app.home.addpassword.addPasswordData;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ShowCardviewDataActivity extends AppCompatActivity {
    TextView /*dis_title*/ dis_login, dis_website, tv_img_title;
    TextInputEditText tiet_pass;
    TextInputLayout til_displaypassword;
    ImageButton img_back;
    public final String REQUEST_CODE = "ShowCardviewDataActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_show_cardview_data);
        //Hooks
        tv_img_title = findViewById(R.id.tv_img_title);
        //dis_title = findViewById(R.id.title);
        dis_login = findViewById(R.id.displaylogin);
        tiet_pass = findViewById(R.id.displaypassword);
        til_displaypassword = findViewById(R.id.til_displaypassword);
        dis_website = findViewById(R.id.displaywebsite);
        img_back = findViewById(R.id.img_back);

        dis_login.setText(getIntent().getStringExtra("loginname"));
        tiet_pass.setText(getIntent().getStringExtra("loginpassowrd"));
        String website = getIntent().getStringExtra("loginwebsite");
        String Title = website.substring(0,1).toUpperCase() + website.substring(1);
        dis_website.setText("www." + website + ".com");
        tv_img_title.setText(Title);



    }

    public void editdata(View view) {
        Intent intent = new Intent(ShowCardviewDataActivity.this, addPasswordData.class);
        String loginname = getIntent().getStringExtra("loginname");
        String loginpassowrd = getIntent().getStringExtra("loginpassowrd");
        String loginwebsite = getIntent().getStringExtra("loginwebsite");
        intent.putExtra("request_code",REQUEST_CODE);
        intent.putExtra("loginname",loginname);
        intent.putExtra("loginpassowrd",loginpassowrd);
        intent.putExtra("loginwebsite",loginwebsite);
        startActivity(intent);
    }

    public void goback(View view) {
        startActivity(new Intent(ShowCardviewDataActivity.this, HomeActivity.class));
        finish();
    }
}