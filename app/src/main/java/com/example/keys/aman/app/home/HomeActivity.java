package com.example.keys.aman.app.home;

import static com.example.keys.aman.app.signin_login.SignUpActivity.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keys.R;
import com.example.keys.aman.app.AES;
import com.example.keys.aman.app.PrograceBar;
import com.example.keys.aman.app.home.addpassword.addDataHelperClass;
import com.example.keys.aman.app.home.addpassword.addPasswordData;
import com.example.keys.aman.app.notes.addNotesActivity;
import com.example.keys.aman.app.notes.notesActivity;
import com.example.keys.aman.app.settings.SettingActivity;
import com.example.keys.aman.app.signin_login.SignUpActivity;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;


public class HomeActivity extends AppCompatActivity {

    private InterstitialAd mInterstitialAd;
    TextToSpeech textToSpeech;
    ScrollView scrollView;
    ExtendedFloatingActionButton exFABtn;
    FloatingActionButton AddPasswordFab, PasswordGenratorFab, ShowpersonalInfofab;
    TextView welcomename, textView_addpassword, textView_passgen, textView_ShowpersonalInfo, tv_NOTE;
    Boolean isAllFabsVisible;

    //Shared Preference
    SharedPreferences sharedPreferences;
    public static DatabaseReference databaseReference;
    public static myadaptor adaptor;
    private PrograceBar prograce_bar;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);
        sharedPreferences = getSharedPreferences(SignUpActivity.SHARED_PREF_ALL_DATA, MODE_PRIVATE);
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //Hooks
        welcomename = findViewById(R.id.welcome_name);
        exFABtn = findViewById(R.id.ExtendedFloatingActionButton);
        AddPasswordFab = findViewById(R.id.add_alarm_fab);
        textView_addpassword = findViewById(R.id.tv_add_password);
        PasswordGenratorFab = findViewById(R.id.add_person_fab);
        textView_passgen = findViewById(R.id.tv_pass_gen);
        ShowpersonalInfofab = findViewById(R.id.show_personal_info_fab);
        textView_ShowpersonalInfo = findViewById(R.id.tv_personal_info);
        tv_NOTE = findViewById(R.id.tv_NOTE);
        scrollView = findViewById(R.id.scrollView);

        MobileAds.initialize(HomeActivity.this);
        showinterstialAd();

        //set Welcome name on top of the Home Screen
        AES aes = new AES();
        SignUpActivity.aes_key = SignUpActivity.AES_KEY;
        SignUpActivity.aes_iv = SignUpActivity.AES_IV;
        aes.initFromStrings(sharedPreferences.getString(SignUpActivity.AES_KEY,null),sharedPreferences.getString(SignUpActivity.AES_IV,null));
        String name = sharedPreferences.getString(SignUpActivity.KEY_USER_NAME, null);
        try {
            welcomename.setText("Hello " + aes.decrypt(name));
        } catch (Exception e) {
            e.printStackTrace();
        }

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {
                    //Select Language
                    int lang = textToSpeech.setLanguage(Locale.ENGLISH);
                }
            }
        });


        recyclerviewsetdata();

        setfabVisblity();

        home_bottom_nav();

//        Toast.makeText(HomeActivity.this,"UI",Toast.LENGTH_SHORT).show();
//        Handler handler = new Handler();
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                Toast.makeText(HomeActivity.this,"Fetching Data From DB",Toast.LENGTH_SHORT).show();
//                recyclerviewsetdata();
//
//            }
//        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        textToSpeech.speak("Welcome to KEYS, Sir", TextToSpeech.QUEUE_ADD, null);
    }

    private void showinterstialAd() {
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this, "ca-app-pub-3940256099942544/1033173712", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                        Log.i(TAG, "onAdLoaded");
                        Toast.makeText(HomeActivity.this, "onAdLoaded", Toast.LENGTH_SHORT).show();
                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when fullscreen content is dismissed.
                                Log.d("TAG", "The ad was dismissed.");
                                Toast.makeText(HomeActivity.this, "The ad was dismissed.", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                                // Called when fullscreen content failed to show.
                                Log.d("TAG", "The ad failed to show.");
                                Toast.makeText(HomeActivity.this, "The ad failed to show.", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when fullscreen content is shown.
                                // Make sure to set your reference to null so you don't
                                // show it a second time.
                                mInterstitialAd = null;
                                Log.d("TAG", "The ad was shown.");
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.i(TAG, loadAdError.getMessage());
                        mInterstitialAd = null;
                    }
                });
    }

    private void setfabVisblity() {
        isAllFabsVisible = false;

        AddPasswordFab.setVisibility(View.GONE);
        textView_addpassword.setVisibility(View.GONE);
        PasswordGenratorFab.setVisibility(View.GONE);
        textView_passgen.setVisibility(View.GONE);
        ShowpersonalInfofab.setVisibility(View.GONE);
        textView_ShowpersonalInfo.setVisibility(View.GONE);


        exFABtn.extend();
        exFABtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!isAllFabsVisible) {
                            AddPasswordFab.show();
                            PasswordGenratorFab.show();
                            ShowpersonalInfofab.show();
                            textView_addpassword.setVisibility(View.VISIBLE);
                            textView_passgen.setVisibility(View.VISIBLE);
                            textView_ShowpersonalInfo.setVisibility(View.VISIBLE);
                            exFABtn.shrink();
                            exFABtn.setIconResource(R.drawable.close);
                            isAllFabsVisible = true;
                        } else {
                            AddPasswordFab.hide();
                            PasswordGenratorFab.hide();
                            ShowpersonalInfofab.hide();
                            textView_addpassword.setVisibility(View.GONE);
                            textView_passgen.setVisibility(View.GONE);
                            textView_ShowpersonalInfo.setVisibility(View.GONE);
                            exFABtn.setIconResource(R.drawable.add);
                            exFABtn.extend();
                            isAllFabsVisible = false;
                        }

                    }
                });
    }

    public void home_bottom_nav() {
        // initialize And Assign Variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav);
        //set Home Selected
        bottomNavigationView.setSelectedItemId(R.id.menu_home);
        //Perform ItemSelectorListener
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_home:
                        return true;
                    case R.id.menu_setting:
                        Intent intent1 = new Intent(HomeActivity.this, SettingActivity.class);
                        startActivity(intent1);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        return true;
                    case R.id.menu_notes:
                        Intent intent2 = new Intent(HomeActivity.this, notesActivity.class);
                        startActivity(intent2);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        return true;
                }
                return false;
            }
        });
    }

    public void recyclerviewsetdata() {
        RecyclerView recyclerView;
        ArrayList<addDataHelperClass> dataholder;


        recyclerView = findViewById(R.id.recview);
        String mobile = sharedPreferences.getString(SignUpActivity.KEY_USER_MOBILE, null);
        databaseReference = FirebaseDatabase.getInstance().getReference("addpassworddata")
                .child(uid);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dataholder = new ArrayList<>();
        adaptor = new myadaptor(dataholder, getApplicationContext(), this);
        recyclerView.setAdapter(adaptor);
        recyclerView.hasFixedSize();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {

                        for (DataSnapshot ds1 : ds.getChildren()) {

                            addDataHelperClass data = ds1.getValue(addDataHelperClass.class);
                            assert data != null;
                            dataholder.add(data);
                        }
                    }
                    Collections.sort(dataholder, addDataHelperClass.addDataHelperClassComparator);
                    adaptor.notifyDataSetChanged();
                } else {
                    tv_NOTE.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void AddPasswordFab(View view) {
        if (mInterstitialAd != null) {
            mInterstitialAd.show(HomeActivity.this);
        } else {
            Toast.makeText(HomeActivity.this, "The interstitial ad wasn't ready yet.", Toast.LENGTH_LONG).show();
        }
        startActivity(new Intent(HomeActivity.this, addPasswordData.class));
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_up);
    }

    public void PasswordGenratorFab(View view) {
        if (mInterstitialAd != null) {
            mInterstitialAd.show(HomeActivity.this);
        } else {
            Toast.makeText(HomeActivity.this, "The interstitial ad wasn't ready yet.", Toast.LENGTH_LONG).show();
        }
        String REQUEST_CODE = "HomeActivity";
        Intent intent = new Intent(HomeActivity.this, PassGenActivity.class);
        intent.putExtra("requestCode", REQUEST_CODE);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_up);
    }

    public void Add_Note(View view) {
        startActivity(new Intent(HomeActivity.this, addNotesActivity.class));
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_up);

    }

}
