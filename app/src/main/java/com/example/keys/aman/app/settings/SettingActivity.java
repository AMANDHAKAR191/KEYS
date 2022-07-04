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
import com.example.keys.aman.app.notes.pinLockFragment;
import com.example.keys.aman.app.signin_login.LogInActivity;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends Fragment {

    Context context;
    Activity activity;

    TextView tv_app_info, tv_contectus, tv_privacy_policy, tv_terms_and_conditions, tv_profile_name, tv_profile_email, tv_use_pin, tv_devices_list;
    ImageView img_back;
    LinearLayout ll_device_list;
    TextView tv_device1, tv_device2, tv_device3;
    TextInputLayout save_website_link;
    SharedPreferences sharedPreferences;
    public static boolean ischecked;
    Button button_logout;
    CircleImageView img_profile;
    AutofillManager mAutofillManager;
    private static final int REQUEST_CODE_SET_DEFAULT = 1;
    private String s1;

    public SettingActivity(Context context, Activity activity){
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
        tv_app_info = view.findViewById(R.id.tv_app_info);
        tv_contectus = view.findViewById(R.id.tv_contectus);
        tv_privacy_policy = view.findViewById(R.id.tv_privacy_policy);
        tv_terms_and_conditions = view.findViewById(R.id.tv_terms_and_conditions);
        tv_use_pin = view.findViewById(R.id.tv_use_pin);
        tv_devices_list = view.findViewById(R.id.tv_devices_list);
        save_website_link = view.findViewById(R.id.save_website_link);
        ll_device_list = view.findViewById(R.id.ll_device_list);
        tv_device1 = view.findViewById(R.id.tv_devices1);
        tv_device2 = view.findViewById(R.id.tv_devices2);
        tv_device3 = view.findViewById(R.id.tv_devices3);
        img_back = view.findViewById(R.id.img_back);
        button_logout = view.findViewById(R.id.btn_logout);
        img_profile = view.findViewById(R.id.img_profile);
        tv_profile_name = view.findViewById(R.id.tv_profile_name);
        tv_profile_email = view.findViewById(R.id.tv_profile_email);
        //TODO Check: add Security textview in this
        //TODO Check: let user choose weather user want to use fingerprint lock or not
        //TODO Check: ask user to use biometric info in this Activity only




        tv_app_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(context, AppInfo.class));
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        tv_contectus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                contactUsFragment contactUs = new contactUsFragment();
//                contactUs.show(getSupportFragmentManager(), "contact_us");
            }
        });
        tv_privacy_policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent open_privacy_policy = new Intent(Intent.ACTION_VIEW, Uri.parse("https://amandhakar.blogspot.com/2022/02/privacy-policy-keys.html"));
                startActivity(open_privacy_policy);
            }
        });
        tv_terms_and_conditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent open_terms_conditions = new Intent(Intent.ACTION_VIEW, Uri.parse("https://amandhakar.blogspot.com/2022/02/terms-conditions-keys.html"));
                startActivity(open_terms_conditions);
            }
        });

        tv_use_pin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean ispin_set =  sharedPreferences.getBoolean(LogInActivity.ISPIN_SET,false);
                if (!ispin_set){
                    Intent intent = new Intent(context, pinLockFragment.class);
                    intent.putExtra(LogInActivity.REQUEST_CODE_NAME,"setpin");
                    intent.putExtra("title","Set Pin");
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(context, pinLockFragment.class);
                    intent.putExtra(LogInActivity.REQUEST_CODE_NAME,"setpin");
                    intent.putExtra("title","Set Pin");
                    startActivity(intent);
                }
            }
        });

        tv_devices_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ll_device_list.setVisibility(View.VISIBLE);
                tv_device2.setVisibility(View.VISIBLE);
                tv_device3.setVisibility(View.VISIBLE);
            }
        });

        button_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();

                SharedPreferences.Editor editor1 = sharedPreferences.edit();
                editor1.putBoolean(LogInActivity.ISLOGIN, false);
                editor1.apply();
                System.out.println(sharedPreferences.getBoolean(LogInActivity.ISLOGIN,false));

                Intent intent = new Intent(context,LogInActivity.class);
                startActivity(intent);
            }
        });

        save_website_link.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                s1 =  save_website_link.getEditText().getText().toString();
            }
        });
        return view;

    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_setting);
////        button_logout.setOnLongClickListener(new View.OnLongClickListener() {
////            @Override
////            public boolean onLongClick(View view) {
////                mAutofillManager = getSystemService(AutofillManager.class);
////               if (mAutofillManager.hasEnabledAutofillServices()){
////                   Toast.makeText(SettingActivity.this, "AutoFill Enabled", Toast.LENGTH_SHORT).show();
////               } else {
////                   Intent intent = new Intent(Settings.ACTION_REQUEST_SET_AUTOFILL_SERVICE);
////                   intent.setData(Uri.parse("package:com.example.android.autofill.service"));
////                   startActivityForResult(intent, 123);
////               }
////                return false;
////            }
////        });
//
////        button_logout.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
////                throw new RuntimeException("Test Crash"); // Force a crash
////            }
////        });
//    }


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
            Glide.with(this).load(currentUser).into(img_profile);
            tv_profile_name.setText(currentUserName);
            tv_profile_email.setText(currentUserEmail);
        }
    }

    public void img_back(View view) {
        activity.finish();
        activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }



}