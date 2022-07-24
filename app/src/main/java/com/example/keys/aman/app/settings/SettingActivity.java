package com.example.keys.aman.app.settings;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.autofill.AutofillManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.keys.R;
import com.example.keys.aman.app.SplashActivity;
import com.example.keys.aman.app.notes.pinLockFragment;
import com.example.keys.aman.app.signin_login.LogInActivity;
import com.google.firebase.auth.FirebaseAuth;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends Fragment {

    Context context;
    Activity activity;

    TextView tvAppInfo, tvContactUs, tvPrivacyPolicy, tvTermsAndConditions, tvProfileName, tvProfileEmail, tvChangePin, tvDevicesList;
    ImageView imgBack;
    LinearLayout llDeviceList;
    TextView tvDevice1, tvDevice2, tvDevice3;
    SharedPreferences sharedPreferences;
    Button btnLogout;
    CircleImageView cimgProfile;
    AutofillManager mAutofillManager;
    private static final int REQUEST_CODE_SET_DEFAULT = 1;
    private String s1;


    public SettingActivity(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_setting, container, false);

        sharedPreferences = activity.getSharedPreferences(LogInActivity.SHARED_PREF_ALL_DATA, MODE_PRIVATE);
        /*---------------Hooks--------------*/
        tvAppInfo = view.findViewById(R.id.tv_app_info);
        tvContactUs = view.findViewById(R.id.tv_contectus);
        tvPrivacyPolicy = view.findViewById(R.id.tv_privacy_policy);
        tvTermsAndConditions = view.findViewById(R.id.tv_terms_and_conditions);
        tvChangePin = view.findViewById(R.id.tv_use_pin);
        tvDevicesList = view.findViewById(R.id.tv_devices_list);
        llDeviceList = view.findViewById(R.id.ll_device_list);
        tvDevice1 = view.findViewById(R.id.tv_devices1);
        tvDevice2 = view.findViewById(R.id.tv_devices2);
        tvDevice3 = view.findViewById(R.id.tv_devices3);
        imgBack = view.findViewById(R.id.img_back);
        btnLogout = view.findViewById(R.id.btn_logout);
        cimgProfile = view.findViewById(R.id.img_profile);
        tvProfileName = view.findViewById(R.id.tv_profile_name);
        tvProfileEmail = view.findViewById(R.id.tv_profile_email);


        tvAppInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SplashActivity.isForeground = true;
                startActivity(new Intent(context, AppInfo.class));
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        tvContactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                contactUsFragment contactUs = new contactUsFragment();
//                contactUs.show(getSupportFragmentManager(), "contact_us");
            }
        });
        tvPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent open_privacy_policy = new Intent(Intent.ACTION_VIEW, Uri.parse("https://amandhakar.blogspot.com/2022/02/privacy-policy-keys.html"));
                startActivity(open_privacy_policy);
            }
        });
        tvTermsAndConditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent open_terms_conditions = new Intent(Intent.ACTION_VIEW, Uri.parse("https://amandhakar.blogspot.com/2022/02/terms-conditions-keys.html"));
                startActivity(open_terms_conditions);
            }
        });
        tvChangePin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean ispin_set = sharedPreferences.getBoolean(LogInActivity.IS_PIN_SET, false);
                Intent intent = new Intent(context, pinLockFragment.class);
                intent.putExtra(LogInActivity.REQUEST_CODE_NAME, "changepin");
                intent.putExtra("title", "Enter Old Pin");
                startActivity(intent);
            }
        });
        tvDevicesList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llDeviceList.setVisibility(View.VISIBLE);
                tvDevice2.setVisibility(View.VISIBLE);
                tvDevice3.setVisibility(View.VISIBLE);
            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SplashActivity.isForeground = true;
                FirebaseAuth.getInstance().signOut();

                SharedPreferences.Editor editor1 = sharedPreferences.edit();
                editor1.putBoolean(LogInActivity.IS_LOGIN, false);
                editor1.apply();
                System.out.println(sharedPreferences.getBoolean(LogInActivity.IS_LOGIN, false));

                Intent intent = new Intent(context, LogInActivity.class);
                startActivity(intent);
            }
        });
//        btnLogout.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//                mAutofillManager = activity.getSystemService(AutofillManager.class);
//               if (mAutofillManager.hasEnabledAutofillServices()){
//                   Toast.makeText(context, "AutoFill Enabled", Toast.LENGTH_SHORT).show();
//               } else {
//                   Intent intent = new Intent(Settings.ACTION_REQUEST_SET_AUTOFILL_SERVICE);
//                   intent.setData(Uri.parse("package:com.example.android.autofill.service"));
//                   startActivityForResult(intent, 123);
//               }
//                return false;
//            }
//        });
        return view;

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            String result = data.getStringExtra("result");
            if (result.equals("yes")) {
            }
        }

        switch (requestCode) {
            case REQUEST_CODE_SET_DEFAULT:
                onDefaultServiceSet(resultCode);
                break;
        }
    }

    private void onDefaultServiceSet(int resultCode) {
        switch (resultCode) {
            case RESULT_OK:
                Toast.makeText(context, "Autofill service set.", Toast.LENGTH_SHORT).show();
                break;
            case RESULT_CANCELED:
                Toast.makeText(context, "Autofill service not selected.", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        Uri currentUser = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();
        String currentUserName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        String currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        if (currentUser == null) {
            // No user is signed in
        } else {
            // User logged in
            Glide.with(this).load(currentUser).into(cimgProfile);
            tvProfileName.setText(currentUserName);
            tvProfileEmail.setText(currentUserEmail);
        }
    }



}