package com.example.keys.aman.app.home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keys.R;
import com.example.keys.aman.app.PrograceBar;
import com.example.keys.aman.app.home.addpassword.addDataHelperClass;
import com.example.keys.aman.app.home.addpassword.addPasswordData;
import com.example.keys.aman.app.notes.addNotesActivity;
import com.example.keys.aman.app.notes.notesActivity;
import com.example.keys.aman.app.settings.SettingActivity;
import com.example.keys.aman.app.signin_login.LogInActivity;
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
import java.util.Objects;


public class HomeActivity extends AppCompatActivity {

    private InterstitialAd mInterstitialAd;
    ScrollView scrollView;
    ExtendedFloatingActionButton exFABtn;
    FloatingActionButton AddPasswordFab, PasswordGenratorFab, ShowpersonalInfofab;
    TextView textView_addpassword, textView_passgen, textView_ShowpersonalInfo, tv_NOTE;
    Boolean isAllFabsVisible;
    RecyclerView recview;
    LinearLayout ll_fab;
    SearchView searchView;
    BottomNavigationView bottomNavigationView;

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
        sharedPreferences = getSharedPreferences(LogInActivity.SHARED_PREF_ALL_DATA, MODE_PRIVATE);
        uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        //Hooks
        exFABtn = findViewById(R.id.ExtendedFloatingActionButton);
        AddPasswordFab = findViewById(R.id.add_alarm_fab);
        textView_addpassword = findViewById(R.id.tv_add_password);
        PasswordGenratorFab = findViewById(R.id.add_person_fab);
        textView_passgen = findViewById(R.id.tv_pass_gen);
        ShowpersonalInfofab = findViewById(R.id.show_personal_info_fab);
        textView_ShowpersonalInfo = findViewById(R.id.tv_personal_info);
        tv_NOTE = findViewById(R.id.tv_NOTE);
        scrollView = findViewById(R.id.scrollView);
        recview = findViewById(R.id.recview);
        ll_fab = findViewById(R.id.ll_fab);
        searchView = findViewById(R.id.search_bar);
        bottomNavigationView = findViewById(R.id.bottom_nav);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                System.out.println("onQueryTextChange...");
                adaptor.getFilter().filter(s);
                System.out.println();
                adaptor.notifyDataSetChanged();

                return false;
            }
        });
        ll_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddPasswordFab.hide();
                PasswordGenratorFab.hide();
                ShowpersonalInfofab.hide();
                textView_addpassword.setVisibility(View.GONE);
                textView_passgen.setVisibility(View.GONE);
                textView_ShowpersonalInfo.setVisibility(View.GONE);
                exFABtn.setIconResource(R.drawable.add);
                exFABtn.extend();
                ll_fab.setBackground(getDrawable(R.drawable.fully_transparent_background));
                ll_fab.setVisibility(View.INVISIBLE);
//                // Gets linearlayout
//                ViewGroup.LayoutParams params = ll_fab.getLayoutParams();
//                params.height = 240;
//                params.width = 260;
//                ll_fab.setLayoutParams(params);
                isAllFabsVisible = false;
            }
        });

        MobileAds.initialize(HomeActivity.this);
        showinterstialAd();


        recyclerviewsetdata();

        setfabVisblity();

        home_bottom_nav();


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
                        //Log.i(TAG, "onAdLoaded");
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
                        //Log.i(TAG, loadAdError.getMessage());
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
                            exFABtn.setTranslationZ(1000);
                            ll_fab.setBackground(getDrawable(R.drawable.transparent_background));
                            ll_fab.setVisibility(View.VISIBLE);
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
                            ll_fab.setVisibility(View.INVISIBLE);
                            ll_fab.setBackground(getDrawable(R.drawable.transparent_background));

//                            // Gets linearlayout
//                            ViewGroup.LayoutParams params = ll_fab.getLayoutParams();
//                            params.height = 5000;
//                            params.width = 1000;
//                            ll_fab.setLayoutParams(params);
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
                        intent1.putExtra(LogInActivity.REQUEST_CODE_NAME,"HomeActivity");
                        startActivity(intent1);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        return true;
                    case R.id.menu_notes:
                        Intent intent2 = new Intent(HomeActivity.this, notesActivity.class);
                        intent2.putExtra(LogInActivity.REQUEST_CODE_NAME,"HomeActivity");
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
        Intent intent = new Intent(HomeActivity.this, addPasswordData.class);
        intent.putExtra(LogInActivity.REQUEST_CODE_NAME,"HomeActivity");
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_up);
    }

    public void PasswordGenratorFab(View view) {
        if (mInterstitialAd != null) {
            mInterstitialAd.show(HomeActivity.this);
        } else {
            Toast.makeText(HomeActivity.this, "The interstitial ad wasn't ready yet.", Toast.LENGTH_LONG).show();
        }
        Intent intent = new Intent(HomeActivity.this, PassGenActivity.class);
        intent.putExtra(LogInActivity.REQUEST_CODE_NAME,"HomeActivity");
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_up);
    }

    public void Add_Note(View view) {
        Intent intent = new Intent(HomeActivity.this, addNotesActivity.class);
        intent.putExtra(LogInActivity.REQUEST_CODE_NAME,"HomeActivity");
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_up);

    }

}
