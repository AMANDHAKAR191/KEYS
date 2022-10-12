package com.example.keys.aman.notes;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keys.R;
import com.example.keys.aman.notes.addnote.AddNoteDataHelperClass;
import com.example.keys.aman.signin_login.LogInActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class NotesFragment extends Fragment {

    Context context;
    Activity activity;
    //    private SwipeRefreshLayout swipeRefreshLayout;
    public ArrayList<AddNoteDataHelperClass> dataHolderPinned, dataHolderUnpinned;
    RecyclerView recyclerViewPinned, recyclerViewUnpinned;
    LogInActivity logInActivity = new LogInActivity();

    public NotesFragment(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    public NotesFragment() {
    }

    SharedPreferences sharedPreferences;
    public static DatabaseReference reference;
    public static myadaptorfornote adaptorUnpinned;
    public static myAdaptorForPinnedNote adaptorPinned;
    TextView tvNote, tvPinned, tvUnpinned;
    SearchView searchView;


    private String uid;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_notes, container, false);
        sharedPreferences = activity.getSharedPreferences(logInActivity.getSHARED_PREF_ALL_DATA(), MODE_PRIVATE);

        //Hooks
        searchView = view.findViewById(R.id.search_bar);
        tvNote = view.findViewById(R.id.tv_NOTE);
        tvPinned = view.findViewById(R.id.tv_pinned);
        tvUnpinned = view.findViewById(R.id.tv_unpinned);
        recyclerViewPinned = view.findViewById(R.id.recview_pinned);
        recyclerViewUnpinned = view.findViewById(R.id.recview_unpinned);
        recyclerViewUnpinned.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
                if (i3 > 30) {
                    recyclerViewPinned.setVisibility(View.VISIBLE);
                    tvPinned.setVisibility(View.VISIBLE);
                } else if (i3 < -30) {
                    recyclerViewPinned.setVisibility(View.GONE);
                    tvPinned.setVisibility(View.GONE);

                }
            }
        });


        uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String s) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String s) {
//                adaptor.getFilter().filter(s);
//
//                adaptor.notifyDataSetChanged();
//
//                return false;
//            }
//        });


        recyclerViewSetPinnedData();
        recyclerViewSetData();
        return view;
    }

    public void recyclerViewSetData() {
        reference = FirebaseDatabase.getInstance().getReference("notes").child(uid);
        recyclerViewUnpinned.setLayoutManager(new LinearLayoutManager(context));

        dataHolderUnpinned = new ArrayList<>();
        adaptorUnpinned = new myadaptorfornote(dataHolderUnpinned, context, activity) {
            @Override
            public void resetAdaptor() {
                dataHolderUnpinned.clear();
                dataHolderPinned.clear();
                Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void refreshRecView() {
                dataHolderUnpinned.clear();
                dataHolderPinned.clear();
            }
        };
        recyclerViewUnpinned.setAdapter(adaptorUnpinned);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        AddNoteDataHelperClass data = ds.getValue(AddNoteDataHelperClass.class);
                        assert data != null;
                        if (data.isHideNote()) {

                        } else {
                            if (data.isPinned()) {

                            } else {
                                dataHolderUnpinned.add(data);
                            }
                        }

                    }
                    Collections.sort(dataHolderUnpinned, AddNoteDataHelperClass.addDNoteHelperClassComparator);
                    adaptorUnpinned.notifyDataSetChanged();
                } else {
                    tvNote.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        recyclerViewUnpinned.setAdapter(adaptorUnpinned);

    }

    public void recyclerViewSetPinnedData() {
        reference = FirebaseDatabase.getInstance().getReference("notes").child(uid);
        recyclerViewPinned.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

        dataHolderPinned = new ArrayList<>();
        adaptorPinned = new myAdaptorForPinnedNote(dataHolderPinned, context, activity) {
            @Override
            public void resetPinnedAdaptor() {
                dataHolderPinned.clear();
                dataHolderUnpinned.clear();
                Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void refreshRecView() {
                dataHolderPinned.clear();
                dataHolderUnpinned.clear();
            }
        };
        recyclerViewPinned.setAdapter(adaptorPinned);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        AddNoteDataHelperClass data = ds.getValue(AddNoteDataHelperClass.class);
                        assert data != null;
                        if (data.isHideNote()) {
                        } else {
                            if (data.isPinned()) {
                                dataHolderPinned.add(data);
                            } else {

                            }

                        }

                    }
                    Collections.sort(dataHolderPinned, AddNoteDataHelperClass.addDNoteHelperClassComparator);
                    adaptorPinned.notifyDataSetChanged();
                } else {
                    tvNote.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        recyclerViewPinned.setAdapter(adaptorPinned);

    }

}