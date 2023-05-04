package com.keys.aman.settings;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.autofill.AutofillManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.keys.aman.MyPreference;
import com.keys.aman.R;
import com.keys.aman.SplashActivity;
import com.keys.aman.authentication.PinLockActivity;
import com.keys.aman.base.TabLayoutActivity;
import com.keys.aman.signin_login.LogInActivity;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingFragment extends Fragment {

    private static final String TAG = "SettingFragment";

    Context context;
    Activity activity;

    TextView tvAppInfo, tvContactUs, tvPrivacyPolicy, tvTermsAndConditions,
            tvProfileName, tvProfileEmail, tvChangePin, tvDevicesList, tvTutorial,
            tvLockApp, tvLockAppResult;
    ImageView imgBack;
    LinearLayout llDeviceList, llLockApp;
    TextView tvDevice1, tvDevice2, tvDevice3;
    SharedPreferences sharedPreferences;
    Button btnLogout;
    CircleImageView cimgProfile;
    AutofillManager mAutofillManager;
    private static final int REQUEST_CODE_SET_DEFAULT = 1;
    public static final String REQUEST_ID = "SettingFragment";
    private String s1;
    int checkedItem = 0;
    LogInActivity logInActivity = new LogInActivity();
    PinLockActivity pinLockActivity = new PinLockActivity();
    TabLayoutActivity tabLayoutActivity = new TabLayoutActivity();
    Intent serviceIntent;
    String currentUserName, currentUserEmail;
    private GoogleSignInApi mGoogleSignInClient;
    MyPreference myPreference;


    public SettingFragment(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    public SettingFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_setting, container, false);

        myPreference = MyPreference.getInstance(context);
//        serviceIntent = new Intent(context, MyForegroundService.class);
        /*---------------Hooks--------------*/
        tvAppInfo = view.findViewById(R.id.tv_app_info);
        tvContactUs = view.findViewById(R.id.tv_contectus);
        tvPrivacyPolicy = view.findViewById(R.id.tv_privacy_policy);
        tvTermsAndConditions = view.findViewById(R.id.tv_terms_and_conditions);
        tvChangePin = view.findViewById(R.id.tv_use_pin);
        tvLockApp = view.findViewById(R.id.tv_lock_app);
        tvLockAppResult = view.findViewById(R.id.tv_lock_app_result);
        tvDevicesList = view.findViewById(R.id.tv_devices_list);
        llDeviceList = view.findViewById(R.id.ll_device_list);
        llLockApp = view.findViewById(R.id.ll_lock_app);
        tvDevice1 = view.findViewById(R.id.tv_devices1);
        tvDevice2 = view.findViewById(R.id.tv_devices2);
        tvDevice3 = view.findViewById(R.id.tv_devices3);
//        imgBack = view.findViewById(R.id.img_back);
        btnLogout = view.findViewById(R.id.btn_logout);
        cimgProfile = view.findViewById(R.id.img_profile);
        tvProfileName = view.findViewById(R.id.tv_profile_name);
        tvProfileEmail = view.findViewById(R.id.tv_profile_email);
        tvTutorial = view.findViewById(R.id.tv_tutorial);
        checkedItem = myPreference.getLockAppSelectedOption();
        String[] Item = {"Immediately", "After 1 minute", "Never"};
        tvLockAppResult.setText(Item[checkedItem]);
        currentUserName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        currentUserEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SplashActivity.isForeground = true;
                FirebaseAuth.getInstance().signOut();
                myPreference.setUserLoggedIn(false);
                Intent intent = new Intent(context, LogInActivity.class);
                startActivity(intent);
            }
        });

        tvChangePin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //todo 4 if app is going to another activity make isForeground = true
                SplashActivity.isForeground = true;
                Intent intent = new Intent(context, PinLockActivity.class);
                intent.putExtra(logInActivity.REQUEST_CODE_NAME, REQUEST_ID);
                intent.putExtra("title", "Enter Old Pin");
                startActivity(intent);
            }
        });
        llLockApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialAlertDialogBuilder alertDialogBuilder = new MaterialAlertDialogBuilder(context)
                        .setTitle("Lock App")
                        .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Log.d(TAG, "=> " + i);
                                dialogInterface.dismiss();
                            }
                        })
                        .setSingleChoiceItems(Item, checkedItem, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int result) {
                                Log.d(TAG, "=> " + result);
                                tvLockAppResult.setText(Item[result]);
                                myPreference.setLockAppSelectedOption(result);
                                checkedItem = myPreference.getLockAppSelectedOption();
                                dialogInterface.dismiss();
                            }
                        });
                alertDialogBuilder.show();
            }
        });
        tvDevicesList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AutofillManager autofillManager = activity.getSystemService(AutofillManager.class);
                if (!autofillManager.hasEnabledAutofillServices()) {
                    SplashActivity.isForeground = true;
                    Intent intent = new Intent(Settings.ACTION_REQUEST_SET_AUTOFILL_SERVICE);
                    intent.setData(Uri.parse("package:" + activity.getPackageName()));
                    startActivityForResult(intent, 0);
                }else {
                    Toast.makeText(context, "Autofill Service is disabled", Toast.LENGTH_SHORT).show();
                    autofillManager.disableAutofillServices();
                }

            }
        });
        tvTutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SplashActivity.isForeground = true;
                Intent intent = new Intent(context, TutorialActivity.class);
                startActivity(intent);
            }
        });
        tvAppInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SplashActivity.isForeground = true;
                startActivity(new Intent(context, AppInfoActivity.class));
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        tvContactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContactUsFragment contactUs = new ContactUsFragment();
                contactUs.show(requireActivity().getSupportFragmentManager(), "contact_us");
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

//        tvStartForegroundService.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (!isForegroundServiceRunning()) {
//                    Toast.makeText(context, "MyForegroundService Starting...", Toast.LENGTH_SHORT).show();
////                    activity.startForegroundService(serviceIntent);
//                    activity.startService(serviceIntent);
//                }
//            }
//        });
//        tvStopForegroundService.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (isForegroundServiceRunning()) {
//                    Toast.makeText(context, "MyForegroundService Stopping...", Toast.LENGTH_SHORT).show();
//                    activity.stopService(serviceIntent);
//                }
//            }
//        });

        tvDevice1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                ImportData importData = new ImportData();
//                importData.importPasswordData(context,activity);
//                Log.d("SettingFragment","Import done");
            }
        });
        return view;

    }

    @Override
    public void onStart() {
        super.onStart();
        Uri currentUser = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getPhotoUrl();

        if (currentUser == null) {
            // No user is signed in
        } else {
            // User logged in
            Glide.with(this).load(currentUser).into(cimgProfile);
            tvProfileName.setText(currentUserName);
            tvProfileEmail.setText(currentUserEmail);
        }
    }


    public boolean isForegroundServiceRunning() {


        return false;
    }


    public void onSendResult(RadioGroup radioGroup, int iResult) {
        tvLockAppResult.setText(radioGroup.getCheckedRadioButtonId() + " | " + iResult);
    }
}