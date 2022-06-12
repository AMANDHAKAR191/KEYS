package com.example.keys.aman.app.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.keys.R;
import com.example.keys.aman.app.home.addpassword.addPasswordData;
import com.example.keys.aman.app.signin_login.LogInActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ShowCardviewDataActivity extends AppCompatActivity {
    TextView /*dis_title*/ dis_login, dis_website, tv_img_title;
    TextInputEditText tiet_pass;
    TextInputLayout til_displaypassword;
    ImageButton img_back;
    public final String REQUEST_CODE = "ShowCardviewDataActivity";
    private String comingdate, loginname, loginpassowrd, loginwebsite;


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

        Intent intent = getIntent();
        comingdate =  intent.getStringExtra("date");
        Toast.makeText(this, "comingdate = " + comingdate, Toast.LENGTH_SHORT).show();
        loginname = getIntent().getStringExtra("loginname");
        loginpassowrd = getIntent().getStringExtra("loginpassowrd");
        loginwebsite = getIntent().getStringExtra("loginwebsite");
        dis_login.setText(loginname);
        tiet_pass.setText(loginpassowrd);
        String Title = loginwebsite.substring(0,1).toUpperCase() + loginwebsite.substring(1);
        dis_website.setText("www." + loginwebsite + ".com");
        tv_img_title.setText(Title);



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
}