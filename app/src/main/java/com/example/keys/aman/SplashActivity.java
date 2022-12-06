package com.example.keys.aman;

import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.keys.R;
import com.example.keys.aman.home.PasswordGeneratorActivity;
import com.example.keys.aman.signin_login.LogInActivity;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import java.util.Arrays;

public class SplashActivity extends AppCompatActivity {
    public static InterstitialAd mInterstitialAd;
    private final String mInterstitialAdId = "ca-app-pub-3752721223259598/1110148878";

    public static RewardedAd mRewardedAd;
    private final String rewardedAdId = "ca-app-pub-3752721223259598/1273800402";

    private String[] PERMISSIONS;
    public static boolean isConnected = false;
    ConnectivityManager connectivityManager;
    public static boolean isForeground = false;
    public static boolean isBackground = false;
    ImageView imageView, imageView1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        imageView = findViewById(R.id.imageView);
        imageView1 = findViewById(R.id.imageView1);

        createShortcutOfApp();
        showInterstialAd();
        showRewardedAd();

//        Intent serviceIntent = new Intent(SplashActivity.this, MyForegroundService.class);
//        startForegroundService(serviceIntent);
//        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//        for (ActivityManager.RunningServiceInfo serviceInfo : activityManager.getRunningServices(Integer.MAX_VALUE)) {
//            if (MyForegroundService.class.getName().equals(serviceInfo.service.getClassName())) {
//
//            }
//        }


        isForeground = true;
        Intent i = new Intent(SplashActivity.this, LogInActivity.class);
        startActivity(i);
        finish();

    }

    private void showInterstialAd() {
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this, mInterstitialAdId, adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;

                        SplashActivity.isForeground = true;
                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when fullscreen content is dismissed.
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                // Called when fullscreen content failed to show.
                                Toast.makeText(SplashActivity.this, "The ad failed to show.", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when fullscreen content is shown.
                                // Make sure to set your reference to null so you don't
                                // show it a second time.
                                mInterstitialAd = null;
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Toast.makeText(SplashActivity.this, "Slow Internet or No connection. Please Wait!", Toast.LENGTH_LONG).show();
                        mInterstitialAd = null;
                    }
                });
    }

    private void showRewardedAd() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {

            }
        });

        AdRequest adRequest = new AdRequest.Builder().build();

        RewardedAd.load(this, rewardedAdId,
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error.
                        Toast.makeText(SplashActivity.this, "Slow Internet or No connection. Please Wait!", Toast.LENGTH_LONG).show();
                        mRewardedAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        mRewardedAd = rewardedAd;
                        mRewardedAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                super.onAdDismissedFullScreenContent();
                                SplashActivity.isForeground = true;
                            }
                        });
                    }
                });
    }


    public void createShortcutOfApp() {

        ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);

        Intent intent = new Intent(SplashActivity.this, PasswordGeneratorActivity.class);
        intent.setAction(Intent.ACTION_VIEW);
        ShortcutInfo shortcutInfo = new ShortcutInfo.Builder(this, "short1").
                setShortLabel("Gen Password").
                setLongLabel("Open PassGenActivity ").
                setIcon(Icon.createWithResource(SplashActivity.this, R.drawable.add)).
                setIntent(intent).
                build();
        shortcutManager.setDynamicShortcuts(Arrays.asList(shortcutInfo));
    }


}