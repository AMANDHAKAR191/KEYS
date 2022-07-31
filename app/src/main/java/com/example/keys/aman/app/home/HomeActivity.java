package com.example.keys.aman.app.home;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.keys.aman.app.home.addpassword.addDataHelperClass;
import com.example.keys.aman.app.signin_login.LogInActivity;
import com.google.android.gms.ads.MobileAds;
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

    ScrollView scrollView;
    TextView tvNOTE;
    RecyclerView recview;
    SearchView searchView;
    public static SwipeRefreshLayout swipeRefreshLayout;
    SharedPreferences sharedPreferences;
    public static DatabaseReference databaseReference;
    public static myadaptor adaptor;
    ArrayList<addDataHelperClass> dataholder;
    String uid;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home, container, false);
        sharedPreferences = activity.getSharedPreferences(LogInActivity.SHARED_PREF_ALL_DATA, MODE_PRIVATE);

        //Hooks

        scrollView = view.findViewById(R.id.scrollView);
        recview = view.findViewById(R.id.recview);
        searchView = view.findViewById(R.id.search_bar);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        tvNOTE = view.findViewById(R.id.tv_NOTE);



        uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

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

        return view;
    }

    public class threadRunnable implements Runnable {
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



    public void recyclerviewsetdata() {

        databaseReference = FirebaseDatabase.getInstance().getReference("addpassworddata")
                .child(uid);
        recview.setLayoutManager(new LinearLayoutManager(context));

        dataholder = new ArrayList<>();
        adaptor = new myadaptor(dataholder, context, activity){
            @Override
            public void resetAdaptor() {
                dataholder.clear();
                Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
            }
        };
        recview.setAdapter(adaptor);
        recview.hasFixedSize();

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
                    tvNOTE.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
