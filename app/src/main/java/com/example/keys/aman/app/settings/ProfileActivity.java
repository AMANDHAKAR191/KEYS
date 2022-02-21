package com.example.keys.aman.app.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.keys.aman.app.AES;
import com.example.keys.R;
import com.example.keys.aman.app.signin_login.LogInActivity;
import com.example.keys.aman.app.signin_login.SignUpActivity;

public class ProfileActivity extends AppCompatActivity {

    //Variable
    TextView tv_profiletext;
    TextView textView_signin_name, textView_signin_mobile, textView_signin_password;
    Button button_logout;
    String mobile, name, email, title_text;
    String dc_name, dc_mobile, dc_email;
    public final String REQUEST_CODE = "ProfileActivity";
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        sharedPreferences = getSharedPreferences(SignUpActivity.SHARED_PREF_ALL_DATA, MODE_PRIVATE);

        //Link to id
        button_logout = findViewById(R.id.btn_logout);
        textView_signin_name = findViewById(R.id.tv_signin_name);
        textView_signin_mobile = findViewById(R.id.tv_signin_mobile);
        textView_signin_password = findViewById(R.id.tv_sigin_password);
        tv_profiletext = findViewById(R.id.tv_profile_text);

        shwDatafromDB();

        button_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor1 = sharedPreferences.edit();
                editor1.clear();
                editor1.apply();
                Toast.makeText(ProfileActivity.this, "Log Out Successfully!", Toast.LENGTH_SHORT).show();
                //startActivity(new Intent(ProfileActivity.this, LogInActivity.class));
                Intent intent = new Intent(ProfileActivity.this, LogInActivity.class);
                startActivityForResult(intent, 1);
                finish();
            }
        });

    }

    private void shwDatafromDB() {


        //for signin data
        mobile = sharedPreferences.getString(SignUpActivity.KEY_USER_MOBILE, null);
        name = sharedPreferences.getString(SignUpActivity.KEY_USER_NAME, null);
        email = sharedPreferences.getString(SignUpActivity.KEY_USER_EMAIL, null);


        try {
            AES aes = new AES();
            aes.initFromStrings("CHuO1Fjd8YgJqTyapibFBQ==", "e3IYYJC2hxe24/EO");
            dc_name = aes.decrypt(name);
            //dc_mobile = aes.decrypt(mobile);
            dc_email = aes.decrypt(email);
            title_text = dc_name.substring(0, 1).toUpperCase();
            tv_profiletext.setText(title_text);
            if (dc_name != null & mobile != null & dc_email != null) {
                textView_signin_name.setText("Name: " + dc_name);
                textView_signin_mobile.setText("Mobile No: " + mobile);
                //TODO Check 3: Remove The password data from profile
                textView_signin_password.setText("Email: " + dc_email);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void editsigndata(View view) {
        Intent intent = new Intent(ProfileActivity.this, SignUpActivity.class);
        intent.putExtra("request_code", REQUEST_CODE);
        intent.putExtra("signname", dc_name);
        intent.putExtra("signmobile", mobile);
        intent.putExtra("signemail", dc_email);
        startActivity(intent);
    }

    public void gocencal(View view) {
        finish();
    }
}