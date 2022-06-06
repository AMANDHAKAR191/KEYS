package com.example.keys.aman.app.settings;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.keys.R;
import com.example.keys.aman.app.AES;
import com.example.keys.aman.app.signin_login.LogInActivity;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileActivity extends AppCompatActivity {

    //Variable
    ImageView img_profileimage;
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
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);
        sharedPreferences = getSharedPreferences(LogInActivity.SHARED_PREF_ALL_DATA, MODE_PRIVATE);

        //Link to id
        button_logout = findViewById(R.id.btn_logout);
        textView_signin_name = findViewById(R.id.tv_signin_name);
        textView_signin_mobile = findViewById(R.id.tv_signin_mobile);
        textView_signin_password = findViewById(R.id.tv_sigin_password);
        img_profileimage = findViewById(R.id.img_profile_image);

        shwDatafromDB();



    }

    @Override
    protected void onStart() {
        super.onStart();

        Uri currentUser = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();
        String currentUserName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        String currentUserPhone = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        System.out.println(currentUser);
        if (currentUser == null) {
            // No user is signed in
        } else {
            // User logged in
            Glide.with(this).load(currentUser).into(img_profileimage);
//            tv_profile_name.setText(currentUserName);
            textView_signin_name.setText("Name: " + currentUserName);
            textView_signin_mobile.setText("Mobile no.: " + currentUserPhone);
            textView_signin_password.setText("Email: " + currentUserEmail);

        }
    }

    private void shwDatafromDB() {


        //for signin data
        mobile = sharedPreferences.getString(LogInActivity.KEY_USER_MOBILE, null);
        name = sharedPreferences.getString(LogInActivity.KEY_USER_NAME, null);
        email = sharedPreferences.getString(LogInActivity.KEY_USER_EMAIL, null);


        try {
            AES aes = new AES();
            aes.initFromStrings("CHuO1Fjd8YgJqTyapibFBQ==", "e3IYYJC2hxe24/EO");
            dc_name = aes.decrypt(name);
            //dc_mobile = aes.decrypt(mobile);
            dc_email = aes.decrypt(email);
            title_text = dc_name.substring(0, 1).toUpperCase();

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



//    public void editsigndata(View view) {
//        Intent intent = new Intent(ProfileActivity.this, SignUpActivity.class);
//        intent.putExtra("request_code", REQUEST_CODE);
//        intent.putExtra("signname", dc_name);
//        intent.putExtra("signmobile", mobile);
//        intent.putExtra("signemail", dc_email);
//        startActivity(intent);
//        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
//    }

    public void gocencal(View view) {
        finish();
        overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
    }
}