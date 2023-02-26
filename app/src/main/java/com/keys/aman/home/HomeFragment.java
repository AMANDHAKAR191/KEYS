package com.keys.aman.home;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.keys.aman.MyPasswordViewModel;
import com.keys.aman.R;
import com.keys.aman.home.addpassword.PasswordHelperClass;
import com.keys.aman.messages.MessagesFragment;
import com.keys.aman.signin_login.LogInActivity;

import java.util.ArrayList;
import java.util.Objects;


public class HomeFragment extends Fragment {
    Context context;
    Activity activity;


    public HomeFragment(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    public HomeFragment() {
    }

    ProgressBar progressBar;
    ScrollView scrollView;
    TextView tvNOTE;
    RecyclerView recview;
    SearchView searchView;
    public static SwipeRefreshLayout swipeRefreshLayout;
    SharedPreferences sharedPreferences;
    public static DatabaseReference databaseReference;
    public static PasswordAdapter adaptor;
    ArrayList<PasswordHelperClass> dataholder;

    //    ArrayList<String> parentdataholder;
    String uid;
    private MyPasswordViewModel viewPasswordModel;
    LogInActivity logInActivity = new LogInActivity();
    public static final String REQUEST_ID = "HomeFragment";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home, container, false);
        sharedPreferences = activity.getSharedPreferences(logInActivity.SHARED_PREF_ALL_DATA, MODE_PRIVATE);
        viewPasswordModel = new ViewModelProvider(requireActivity()).get(MyPasswordViewModel.class);

        //Hooks
        progressBar = view.findViewById(R.id.progressBar);
        recview = view.findViewById(R.id.recview_website_list);
        searchView = view.findViewById(R.id.search_bar);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        tvNOTE = view.findViewById(R.id.tv_NOTE);
        progressBar.setVisibility(View.VISIBLE);


        uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();


        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                adaptor.getFilter().filter(s);
                System.out.println();
                adaptor.notifyDataSetChanged();

                return false;
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                dataholder.clear();
                recyclerviewsetdata();
                swipeRefreshLayout.setRefreshing(false);
                progressBar.setVisibility(View.VISIBLE);
            }
        });


        threadRunnableHomeFragment threadRunnableHomeFragment = new threadRunnableHomeFragment(view);
        new Thread(threadRunnableHomeFragment).start();

        return view;
    }

    public class threadRunnableHomeFragment implements Runnable {
        Handler handler = new Handler();
        View view;

        public threadRunnableHomeFragment(View view) {
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

        recview.hasFixedSize();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    System.out.println(dataSnapshot);
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        System.out.println("\t" + ds);
                        for (DataSnapshot ds1 : ds.getChildren()) {
                            System.out.println("\t\t" + ds1);
                            PasswordHelperClass data = ds1.getValue(PasswordHelperClass.class);
                            dataholder.add(data);
                        }
                    }
                    adaptor.notifyDataSetChanged();

                } else {
                    tvNOTE.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        progressBar.setVisibility(View.INVISIBLE);
        adaptor = new PasswordAdapter(dataholder, context, activity) {
            @Override
            public void resetAdaptor() {
                dataholder.clear();
                Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void showCardViewFragment(String currentDate, String tempLogin, String tempPassword,
                                             String dWebsiteName, String dWebsiteLink) {

                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fl_container, new ShowCardViewDataDialog(context, activity, currentDate, tempLogin,
                        tempPassword, dWebsiteName, dWebsiteLink));
                fragmentTransaction.commit();
            }

            @Override
            public void sharePassword(PasswordHelperClass passwordData) {
                super.sharePassword(passwordData);
                viewPasswordModel.setPasswordData(passwordData);

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                MessagesFragment messagesFragment = new MessagesFragment(context, activity);
//                // Create a new bundle to store the data
//                Bundle data = new Bundle();
//                // Put the note data in the bundle
//                data.putString(logInActivity.REQUEST_CODE_NAME,REQUEST_ID);
//                data.putParcelable(shareNoteCode, noteData);
//
//                // Set the arguments on the fragment
//                messagesFragment.setArguments(data);
                fragmentTransaction.add(R.id.fl_user_list_container, messagesFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        };
        recview.setAdapter(adaptor);
        adaptor.notifyDataSetChanged();
    }
}
