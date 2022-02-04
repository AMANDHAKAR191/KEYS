package com.example.keys;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.keys.baseactivitys.HomeActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ShowCardviewDataActivity extends AppCompatActivity {
    TextView /*dis_title*/ dis_login, dis_website, tv_img_title;
    TextInputEditText tiet_pass;
    TextInputLayout til_displaypassword;
    ImageButton img_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_cardview_data);
        //Hooks
        tv_img_title = findViewById(R.id.tv_img_title);
        //dis_title = findViewById(R.id.title);
        dis_login = findViewById(R.id.displaylogin);
        tiet_pass = findViewById(R.id.displaypassword);
        til_displaypassword = findViewById(R.id.til_displaypassword);
        dis_website = findViewById(R.id.displaywebsite);
        img_back = findViewById(R.id.img_back);
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ShowCardviewDataActivity.this, HomeActivity.class));
                finish();
            }
        });
//        til_displaypassword.setEndIconOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(ShowCardviewDataActivity.this,"Show Password",Toast.LENGTH_SHORT).show();
//            }
//        });
        //dis_title.setText(getIntent().getStringExtra("loginwebsite"));
        dis_login.setText(getIntent().getStringExtra("loginname"));
        tiet_pass.setText(getIntent().getStringExtra("loginpassowrd"));
        String website = getIntent().getStringExtra("loginwebsite");
        //String Title = website.substring(0, 1).toUpperCase() + website.substring(1, 2);
        String Title = website.substring(0,1).toUpperCase() + website.substring(1);
        dis_website.setText("www." + website + ".com");
        tv_img_title.setText(Title);
//        tv_img_title.setTextColor(0xFF00FF00);
//        tv_img_title.setBackgroundColor(0xFF00FF00);

    }
}