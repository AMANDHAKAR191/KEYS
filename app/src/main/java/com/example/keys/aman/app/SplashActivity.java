package com.example.keys.aman.app;

import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.keys.R;
import com.example.keys.aman.app.home.PassGenActivity;
import com.example.keys.aman.app.signin_login.LogInActivity;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        createShortcutOfApp();

        showinterstialAd();

        showRewardedAd();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashActivity.this, LogInActivity.class);
                startActivity(i);
                finish();
            }
        },1000);
//        checkInternet();


        //TODO Check 5: check internet in this Activity also internet stability
        //TODO Check 6: Cehck internet spped in every Activity if speed is low then show Error to user

//        PERMISSIONS = new String[]{
//                Manifest.permission.READ_EXTERNAL_STORAGE,
////                Manifest.permission.ACCESS_NETWORK_STATE,
////                Manifest.permission.USE_BIOMETRIC,
////                Manifest.permission.VIBRATE
//        };
//
//        new Handler().postDelayed(new Runnable() {
//            @RequiresApi(api = Build.VERSION_CODES.P)
//            @Override
//            public void run() {
//                hasPermissions(SplashActivity.this, PERMISSIONS);
//                if (!hasPermissions(SplashActivity.this, PERMISSIONS)) {
//                    ActivityCompat.requestPermissions(SplashActivity.this, PERMISSIONS, 1);
//                }else {
//                    Intent i = new Intent(SplashActivity.this, LogInActivity.class);
//                    startActivity(i);
//                    finish();
//                }
//            }
//        }, 1000);


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
                        Toast.makeText(SplashActivity.this, loadAdError.getMessage(), Toast.LENGTH_LONG).show();
                        mRewardedAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        mRewardedAd = rewardedAd;
                        Toast.makeText(SplashActivity.this, "Ad was loaded.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void showinterstialAd() {
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this, mInterstitialAdId, adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Toast.makeText(SplashActivity.this, "Ad Loaded", Toast.LENGTH_SHORT).show();
                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when fullscreen content is dismissed.
                                Toast.makeText(SplashActivity.this, "The ad was dismissed.", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(SplashActivity.this, "Ad Loaded", Toast.LENGTH_SHORT).show();
                        mInterstitialAd = null;
                    }
                });
    }

    public void createShortcutOfApp() {

        ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);

        Intent intent = new Intent(SplashActivity.this, PassGenActivity.class);
        intent.setAction(Intent.ACTION_VIEW);
        ShortcutInfo shortcutInfo = new ShortcutInfo.Builder(this,"short1").
                setShortLabel("Gen Password").
                setLongLabel("Open PassGenActivity ").
                setIcon(Icon.createWithResource(SplashActivity.this,R.drawable.add)).
                setIntent(intent).
                build();
        shortcutManager.setDynamicShortcuts(Arrays.asList(shortcutInfo));
    }

//    public void checkInternet(){
//        if (isConnected){
//            Intent intent = new Intent(SplashActivity.this, LogInActivity.class);
//            intent.putExtra(LogInActivity.REQUEST_CODE_NAME,"SplashActivity");
//            startActivity(intent);
//            finish();
//        }else {
//            Intent intent = new Intent(getApplicationContext(),checkInternetFragment.class);
//            startActivity(intent);
//        }
//    }

//    private void registetNetworkCallBack(){
//
//        try {
//
//            connectivityManager = (ConnectivityManager) getApplicationContext()
//                    .getSystemService(Context.CONNECTIVITY_SERVICE);
//            connectivityManager.registerDefaultNetworkCallback(
//                    new ConnectivityManager.NetworkCallback(){
//                        @Override
//                        public void onAvailable(@NonNull Network network) {
//                            super.onAvailable(network);
//                            isConnected = true;
//                        }
//
//                        @Override
//                        public void onLost(@NonNull Network network) {
//                            super.onLost(network);
//                            isConnected = false;
//                        }
//                    });
//
//
//
//        }catch (Exception e){
//            isConnected = false;
//        }
//    }
//
//    private void unRegistetNetworkCallBack(){
//        connectivityManager.unregisterNetworkCallback(
//                new ConnectivityManager.NetworkCallback());
//    }
//
//    @Override
//    protected void onPostResume() {
//        super.onPostResume();
//        registetNetworkCallBack();
//    }
//
////    @Override
////    protected void onPause() {
////        super.onPause();
////        unRegistetNetworkCallBack();
////    }
//    //    private boolean hasPermissions(Context context, String... permissions) {
////        if (context != null && PERMISSIONS != null) {
////            for (String permission : PERMISSIONS) {
////                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
////                    return false;
////                }
////            }
////        }
////        return true;
////    }
////
////
////    @Override
////    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
////        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
////        if (requestCode == 1) {
////            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
////                Toast.makeText(this, "External Storage Permission GRANTED", Toast.LENGTH_SHORT).show();
////                Intent i = new Intent(SplashActivity.this, LogInActivity.class);
////                startActivity(i);
////                finish();
////            }else {
////                Toast.makeText(this, "External Storage Permission DENIED", Toast.LENGTH_SHORT).show();
////            }
////
//////            if (grantResults[1] == PackageManager.PERMISSION_GRANTED){
//////                Toast.makeText(this, "Network Access Permission GRANTED", Toast.LENGTH_SHORT).show();
//////                Intent i = new Intent(SplashActivity.this, LogInActivity.class);
//////                startActivity(i);
//////                finish();
//////            }else {
//////                Toast.makeText(this, "Network Access Permission DENIED", Toast.LENGTH_SHORT).show();
//////            }
//////
//////            if (grantResults[2] == PackageManager.PERMISSION_GRANTED){
//////                Toast.makeText(this, "Biometric Permission GRANTED", Toast.LENGTH_SHORT).show();
//////            }else {
//////                Toast.makeText(this, "Biometric Permission DENIED", Toast.LENGTH_SHORT).show();
//////            }
//////
//////            if (grantResults[3] == PackageManager.PERMISSION_GRANTED){
//////                Toast.makeText(this, "Vibration Permission GRANTED", Toast.LENGTH_SHORT).show();
//////            }else {
//////                Toast.makeText(this, "Vibration Permission DENIED", Toast.LENGTH_SHORT).show();
//////            }
////        }
////    }

}