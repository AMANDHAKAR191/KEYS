package com.example.keys.aman.home;

import static com.example.keys.aman.SplashActivity.mRewardedAd;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.keys.R;
import com.example.keys.aman.SplashActivity;
import com.example.keys.aman.home.addpassword.AddPasswordDataActivity;
import com.example.keys.aman.authentication.PinLockActivity;
import com.example.keys.aman.signin_login.LogInActivity;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;

public class ShowCardViewDataActivity extends Fragment {
    TextView tvDisplayLogin, tvDisplayWebsite, tvTitle, tvWebsiteTitle;
    TextInputEditText tietDisplayPassword;
    TextInputLayout tilDisplayPassword;
    ImageView imgBack, imgEdit;
    ConstraintLayout clBackground;
    ImageView imgWebsiteLogo, imgOpenWebsite;
    Context context;
    Activity activity;
    ActivityResultLauncher<Intent> getResult;
    LogInActivity logInActivity = new LogInActivity();

    String comingDate, comingLoginName, comingLoginPassword, comingLoginWebsiteName, comingLoginWebsiteLink;
    private Bitmap bmWebsiteLogo;
    private Bitmap emptyBitmap;


    public ShowCardViewDataActivity(Context context, Activity activity, String currentDate, String tempLogin, String tempPassword,
                                    String dWebsiteName, String dWebsiteLink) {
        this.context = context;
        this.activity = activity;
        this.comingDate = currentDate;
        this.comingLoginName = tempLogin;
        this.comingLoginPassword = tempPassword;
        this.comingLoginWebsiteName = dWebsiteName;
        this.comingLoginWebsiteLink = dWebsiteLink;
    }
    public ShowCardViewDataActivity() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.activity_show_cardview_data,container,false);
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        SplashActivity.isForeground = false;
        //Hooks
        tvTitle = view.findViewById(R.id.tv_img_title);
        tvDisplayLogin = view.findViewById(R.id.displaylogin);
        tietDisplayPassword = view.findViewById(R.id.displaypassword);
        tilDisplayPassword = view.findViewById(R.id.til_displaypassword);
        tvDisplayWebsite = view.findViewById(R.id.displaywebsite);
        imgBack = view.findViewById(R.id.img_back);
        imgEdit = view.findViewById(R.id.img_edit);
        clBackground = view.findViewById(R.id.cl_background);
        imgWebsiteLogo = view.findViewById(R.id.img_website_logo);
        tvWebsiteTitle = view.findViewById(R.id.tv_website_title);
        imgOpenWebsite = view.findViewById(R.id.open_website);

        tvDisplayLogin.setText(comingLoginName);
        tietDisplayPassword.setText(comingLoginPassword);
        String Title = comingLoginWebsiteName.substring(0, 1).toUpperCase() + comingLoginWebsiteName.substring(1);
        tvDisplayWebsite.setText(comingLoginWebsiteLink);
        tvTitle.setText(Title.replace("_","."));

        clBackground.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                goBack();
                return false;
            }
        });
        clBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBack();
            }
        });
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBack();
            }
        });
        imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SplashActivity.isForeground = true;
                editdata();
                goBack();
            }
        });
        tilDisplayPassword.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println(tietDisplayPassword.getInputType());
                if (tietDisplayPassword.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    tietDisplayPassword.setInputType(InputType.TYPE_CLASS_TEXT |
                            InputType.TYPE_TEXT_VARIATION_PASSWORD);

                } else {
                    SplashActivity.isForeground = true;
                    if (mRewardedAd != null) {
                        Activity activityContext = activity;
                        mRewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                            @Override
                            public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                                // Handle the reward.
                                int rewardAmount = rewardItem.getAmount();
                                String rewardType = rewardItem.getType();
                            }
                        });
                    } else {
                        Toast.makeText(context, "The rewarded ad wasn't ready yet.", Toast.LENGTH_SHORT).show();
                    }
                    Intent intent1 = new Intent(context, PinLockActivity.class);
                    intent1.putExtra(logInActivity.getREQUEST_CODE_NAME(), "ShowCardViewDataActivity");
                    intent1.putExtra("title", "Enter Pin");
                    getResult.launch(intent1);


                }
                tietDisplayPassword.setSelection(Objects.requireNonNull(tietDisplayPassword.getText()).length());

            }
        });

        imgWebsiteLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("website_logo1 " + bmWebsiteLogo);
                Toast.makeText(context, "logo fetched!!" + bmWebsiteLogo, Toast.LENGTH_SHORT).show();
            }
        });

        getResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        Intent data = result.getData();
                        String tempResult = Objects.requireNonNull(data).getStringExtra("result");
                        if (tempResult.equals("yes")) {
                            tietDisplayPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        }
                    }
                }
        );

        cardViewDataThreadRunnable dataThreadRunnable = new cardViewDataThreadRunnable(comingLoginWebsiteLink,comingLoginWebsiteName);
        new Thread(dataThreadRunnable).start();
        return view;
    }

    public void editdata() {
        Intent intent = new Intent(context, AddPasswordDataActivity.class);
        intent.putExtra(logInActivity.getREQUEST_CODE_NAME(), "ShowCardViewDataActivity");
        intent.putExtra("date", comingDate);
        intent.putExtra("loginname", comingLoginName);
        intent.putExtra("loginpassowrd", comingLoginPassword);
        intent.putExtra("loginwebsite_name", comingLoginWebsiteName);
        intent.putExtra("loginwebsite_link", comingLoginWebsiteLink);
        startActivity(intent);
    }
    public void goBack() {
//        getFragmentManager().beginTransaction().remove(ShowCardviewDataActivity.this).commit();
        getParentFragmentManager().beginTransaction().remove(ShowCardViewDataActivity.this).commit();
    }
//    public void openWebsite(View view) {
//        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(addPasswordData.addWebsiteLink));
//        Toast.makeText(this, "Opening Website", Toast.LENGTH_SHORT).show();
//        startActivity(intent);
//    }
    private static Bitmap fetchFavicon(Uri uri) {
        final Uri iconUri = uri.buildUpon().path("favicon.ico").build();

        InputStream is = null;
        BufferedInputStream bis = null;
        try {
            URLConnection conn = new URL(iconUri.toString()).openConnection();
            conn.connect();
            is = conn.getInputStream();
            bis = new BufferedInputStream(is, 8192);
            return BitmapFactory.decodeStream(bis);
        } catch (IOException e) {
            return null;
        }
    }
    public class cardViewDataThreadRunnable implements Runnable {

        private final String tempLoginWebsiteLink;
        private final String tempLoginWebsiteName;

        public cardViewDataThreadRunnable (String loginWebsiteLink, String loginWebsiteName){
            this.tempLoginWebsiteLink = loginWebsiteLink;
            this.tempLoginWebsiteName = loginWebsiteName;
        }

        Handler handler = new Handler();
        @Override
        public void run() {
            try {
                bmWebsiteLogo = fetchFavicon(Uri.parse(tempLoginWebsiteLink));
                emptyBitmap = Bitmap.createBitmap(Objects.requireNonNull(bmWebsiteLogo).getWidth(),bmWebsiteLogo.getHeight(),bmWebsiteLogo.getConfig());

            }catch (NullPointerException e){
                
            }
            
            String[] title1 = tempLoginWebsiteName.split("_",3);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    imgWebsiteLogo.setImageBitmap(bmWebsiteLogo);
                    try {
                        if (bmWebsiteLogo.sameAs(emptyBitmap)){

                        }
                    }catch (NullPointerException e){
                        if (title1.length == 3){
                            imgWebsiteLogo.setVisibility(View.INVISIBLE);
                            tvWebsiteTitle.setVisibility(View.VISIBLE);
                            tvWebsiteTitle.setText(title1[1]);
                        }else if (title1.length == 2){
                            imgWebsiteLogo.setVisibility(View.INVISIBLE);
                            tvWebsiteTitle.setVisibility(View.VISIBLE);
                            tvWebsiteTitle.setText(title1[0]);
                        }
                    }
                }
            });
        }
    }
}