package com.example.keys.aman.app.home;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.keys.R;
import com.example.keys.aman.app.PrograceBar;
import com.example.keys.aman.app.home.addpassword.addDataHelperClass;
import com.example.keys.aman.app.home.addpassword.addPasswordData;
import com.example.keys.aman.app.notes.addNotesActivity;
import com.example.keys.aman.app.signin_login.LogInActivity;
import com.google.android.gms.ads.MobileAds;
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


public class HomeActivity extends Fragment {
    Context context;
    Activity activity;


    public HomeActivity(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    //    public static InterstitialAd mInterstitialAd;
    ScrollView scrollView;
    ExtendedFloatingActionButton exFABtn;
    FloatingActionButton AddPasswordFab, PasswordGenratorFab, AddNotefab;
    TextView textView_addpassword, textView_passgen, textView_ShowpersonalInfo, tv_NOTE;
    Boolean isAllFabsVisible;
    RecyclerView recview;
    LinearLayout ll_fab;
    SearchView searchView;
    SwipeRefreshLayout swipeRefreshLayout;

    //Shared Preference
    SharedPreferences sharedPreferences;
    public static DatabaseReference databaseReference;
    public static myadaptor adaptor;
    private PrograceBar prograce_bar;
    RecyclerView recyclerView;
    ArrayList<addDataHelperClass> dataholder;
//    private final String mInterstitialAdId = "ca-app-pub-3752721223259598/1110148878";
    String uid;
    ArrayList<addDataHelperClass> offlineDataholder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home,container,false);
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);
        sharedPreferences = activity.getSharedPreferences(LogInActivity.SHARED_PREF_ALL_DATA, MODE_PRIVATE);
        uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        //Hooks
        recyclerView = view.findViewById(R.id.recview);
        exFABtn = view.findViewById(R.id.ExtendedFloatingActionButton);
        AddPasswordFab = view.findViewById(R.id.add_password_fab);
        textView_addpassword = view.findViewById(R.id.tv_add_password);
        PasswordGenratorFab = view.findViewById(R.id.password_gen_fab);
        textView_passgen = view.findViewById(R.id.tv_pass_gen);
        AddNotefab = view.findViewById(R.id.add_note_fab);
        textView_ShowpersonalInfo = view.findViewById(R.id.tv_personal_info);
        tv_NOTE = view.findViewById(R.id.tv_NOTE);
        scrollView = view.findViewById(R.id.scrollView);
        recview = view.findViewById(R.id.recview);
        ll_fab = view.findViewById(R.id.ll_fab);
        searchView = view.findViewById(R.id.search_bar);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String s) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String s) {
//                System.out.println("onQueryTextChange...");
//                adaptor.getFilter().filter(s);
//                System.out.println();
//                adaptor.notifyDataSetChanged();
//
//                return false;
//            }
//        });
        ll_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddPasswordFab.hide();
                PasswordGenratorFab.hide();
                AddNotefab.hide();
                textView_addpassword.setVisibility(View.GONE);
                textView_passgen.setVisibility(View.GONE);
                textView_ShowpersonalInfo.setVisibility(View.GONE);
                exFABtn.setIconResource(R.drawable.add);
                exFABtn.extend();
                ll_fab.setBackground(activity.getDrawable(R.drawable.fully_transparent_background));
                ll_fab.setVisibility(View.INVISIBLE);
//                // Gets linearlayout
//                ViewGroup.LayoutParams params = ll_fab.getLayoutParams();
//                params.height = 240;
//                params.width = 260;
//                ll_fab.setLayoutParams(params);
                isAllFabsVisible = false;
            }
        });
        recview.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        AddPasswordFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, addPasswordData.class);
                intent.putExtra(LogInActivity.REQUEST_CODE_NAME,"HomeActivity");
                startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_up);
            }
        });
        PasswordGenratorFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PassGenActivity.class);
                intent.putExtra(LogInActivity.REQUEST_CODE_NAME,"HomeActivity");
                startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_up);
            }
        });
        AddNotefab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, addNotesActivity.class);
                intent.putExtra(LogInActivity.REQUEST_CODE_NAME,"HomeActivity");
                startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_up);
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                dataholder.clear();
                recyclerviewsetdata();
                swipeRefreshLayout.setRefreshing(false);
            }
        });


        MobileAds.initialize(context);

        threadRunnable threadRunnable = new threadRunnable(view);
        new Thread(threadRunnable).start();

        setfabVisblity();


        String device_name = Build.MANUFACTURER + " | " + Build.DEVICE ;
        Toast.makeText(context, "Device Name: " + device_name, Toast.LENGTH_SHORT).show();


        return view;
    }

    public class threadRunnable implements Runnable{
        Handler handler = new Handler();
        View view;

        public threadRunnable(View view) {
            this.view = view;
        }

        @Override
        public void run() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    recyclerviewsetdata();
                }
            });
        }
    }

    private void setfabVisblity() {
        isAllFabsVisible = false;

        AddPasswordFab.setVisibility(View.GONE);
        textView_addpassword.setVisibility(View.GONE);
        PasswordGenratorFab.setVisibility(View.GONE);
        textView_passgen.setVisibility(View.GONE);
        AddNotefab.setVisibility(View.GONE);
        textView_ShowpersonalInfo.setVisibility(View.GONE);


        exFABtn.extend();
        exFABtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!isAllFabsVisible) {
                            AddPasswordFab.show();
                            PasswordGenratorFab.show();
                            AddNotefab.show();
                            textView_addpassword.setVisibility(View.VISIBLE);
                            textView_passgen.setVisibility(View.VISIBLE);
                            textView_ShowpersonalInfo.setVisibility(View.VISIBLE);
                            exFABtn.shrink();
                            exFABtn.setIconResource(R.drawable.close);
                            exFABtn.setTranslationZ(1000);
                            ll_fab.setBackground(activity.getDrawable(R.drawable.transparent_background));
                            ll_fab.setVisibility(View.VISIBLE);
                            isAllFabsVisible = true;
                        } else {
                            AddPasswordFab.hide();
                            PasswordGenratorFab.hide();
                            AddNotefab.hide();
                            textView_addpassword.setVisibility(View.GONE);
                            textView_passgen.setVisibility(View.GONE);
                            textView_ShowpersonalInfo.setVisibility(View.GONE);
                            exFABtn.setIconResource(R.drawable.add);
                            exFABtn.extend();
                            ll_fab.setVisibility(View.INVISIBLE);
                            ll_fab.setBackground(activity.getDrawable(R.drawable.transparent_background));

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

    public void recyclerviewsetdata() {

        databaseReference = FirebaseDatabase.getInstance().getReference("addpassworddata")
                .child(uid);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        dataholder = new ArrayList<>();
        adaptor = new myadaptor(dataholder, context, activity);
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

}
