package com.keys.aman.home;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.keys.aman.MyPasswordViewModel;
import com.keys.aman.MyPreference;
import com.keys.aman.R;
import com.keys.aman.data.Firebase;
import com.keys.aman.data.iFirebaseDAO;
import com.keys.aman.home.addpassword.PasswordHelperClass;
import com.keys.aman.messages.MessagesFragment;

import java.util.ArrayList;
import java.util.Objects;


public class HomeFragment extends Fragment {
    public static final String REQUEST_ID = "HomeFragment";
    public static SwipeRefreshLayout swipeRefreshLayout;
    public static DatabaseReference databaseReference;
    public static PasswordAdapter adaptor;
    Context context;
    Activity activity;
    Button btnSharedPassword;
    LinearProgressIndicator progressBar;
    TextView tvNOTE;
    RecyclerView recview;
    SearchView searchView;
    ArrayList<PasswordHelperClass> dataholder;
    MyPreference myPreference;
    String uid;
    private MyPasswordViewModel viewPasswordModel;
    private iFirebaseDAO iFirebase;

    public HomeFragment(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }
    public HomeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home, container, false);
        //initialize local database
        myPreference = MyPreference.getInstance(context);
        viewPasswordModel = new ViewModelProvider(requireActivity()).get(MyPasswordViewModel.class);
        iFirebase = Firebase.getInstance(context);

        //Hooks
        recview = view.findViewById(R.id.recview_passwords_list);
        searchView = view.findViewById(R.id.search_bar);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        tvNOTE = view.findViewById(R.id.tv_NOTE);
        progressBar = view.findViewById(R.id.linear_progress_indicator);
        progressBar.setVisibility(View.VISIBLE);
        btnSharedPassword = view.findViewById(R.id.btn_shared_password);


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
        btnSharedPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fl_shared_password_container, new SharedPasswordFragment(context, activity));
                fragmentTransaction.commit();
            }
        });


        threadRunnableHomeFragment threadRunnableHomeFragment = new threadRunnableHomeFragment(view);
        new Thread(threadRunnableHomeFragment).start();

        return view;
    }

    public void recyclerviewsetdata() {

        databaseReference = FirebaseDatabase.getInstance().getReference("addpassworddata").child(uid);
        recview.setLayoutManager(new LinearLayoutManager(context));
        dataholder = new ArrayList<>();
        recview.hasFixedSize();

//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                        for (DataSnapshot ds1 : ds.getChildren()) {
//                            PasswordHelperClass data = ds1.getValue(PasswordHelperClass.class);
//                            dataholder.add(data);
//                        }
//                    }
//                    adaptor.notifyDataSetChanged();
//
//                } else {
//                    tvNOTE.setVisibility(View.VISIBLE);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
        iFirebase.loadPasswordsData(new Firebase.iLoadPasswordDataCallback() {
            @Override
            public void onPasswordDataReceivedCallback(ArrayList<PasswordHelperClass> dataHolderPassword) {
                if (dataHolderPassword.isEmpty()) {
                    tvNOTE.setVisibility(View.VISIBLE);
                }
                dataholder = dataHolderPassword;
                progressBar.setVisibility(View.INVISIBLE);
                System.out.println("check1");
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
        });
    }

    @Override
    public void onStart() {
        super.onStart();
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
}