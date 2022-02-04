package com.example.keys.baseactivitys;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.keys.AES;
import com.example.keys.LogInActivity;
import com.example.keys.R;
import com.example.keys.SignUpActivity;
import com.example.keys.UserHelperClass;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    //Variable
    TextView tv_profiletext;
    TextView textView_signin_name, textView_signin_mobile, textView_signin_password;
    Button button_logout;
    String sign_name, sign_mobile, sign_password, title_text;

    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        sharedPreferences = getSharedPreferences(SignUpActivity.SHARED_PREF_ALL_DATA, MODE_PRIVATE);
        profil_bottom_nav();

        //Link to id
        button_logout = (Button) findViewById(R.id.btn_logout);
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

        String mobile, name, email;
        String dc_name, dc_mobile, dc_email;
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

    public void profil_bottom_nav() {
        // initialize And Assign Variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        //set Home Selected
        bottomNavigationView.setSelectedItemId(R.id.menu_profile);
        //Perform ItemSelectorListener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_home:
                        startActivity(new Intent(ProfileActivity.this, HomeActivity.class));
                        return true;
                    case R.id.menu_setting:
                        startActivity(new Intent(ProfileActivity.this, SettingActivity.class));

                        return true;
                    case R.id.menu_profile:
                        return true;
                }
                return false;
            }
        });
    }

}