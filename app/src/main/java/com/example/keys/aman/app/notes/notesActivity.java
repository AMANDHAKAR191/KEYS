package com.example.keys.aman.app.notes;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keys.R;
import com.example.keys.aman.app.signin_login.LogInActivity;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class notesActivity extends Fragment {

    Context context;
    Activity activity;

    public notesActivity(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    private static final String TAG = "notesActivity";
    SharedPreferences sharedPreferences;
    public static DatabaseReference reference;
    public static myadaptorfornote adaptor;
    private boolean turn = false;
    TextView tvNote;
    SearchView searchView;
    ExtendedFloatingActionButton exFABtn;
    private String uid;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_notes,container,false);
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);
        sharedPreferences = activity.getSharedPreferences(LogInActivity.SHARED_PREF_ALL_DATA, MODE_PRIVATE);

        //Hooks
        searchView = view.findViewById(R.id.search_bar);
        tvNote = view.findViewById(R.id.tv_NOTE);
        exFABtn = view.findViewById(R.id.ExtendedFloatingActionButton);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
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
        exFABtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, addNotesActivity.class);
                intent.putExtra(LogInActivity.REQUEST_CODE_NAME,"notesActivity");
                startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_up);
            }
        });

        recyclerViewSetData(view);
        return view;
    }

    public void recyclerViewSetData(View view) {
        RecyclerView recyclerView;
        ArrayList<addDNoteHelperClass> dataholder;
        recyclerView = view.findViewById(R.id.recview);

        reference = FirebaseDatabase.getInstance().getReference("notes").child(uid);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        dataholder = new ArrayList<>();
        adaptor = new myadaptorfornote(dataholder, context, activity);
        recyclerView.setAdapter(adaptor);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        addDNoteHelperClass data = ds.getValue(addDNoteHelperClass.class);
                        assert data != null;
                        if (data.isHideNote()){

                        }else {
                            dataholder.add(data);
                        }

                    }
                    Collections.sort(dataholder,addDNoteHelperClass.addDNoteHelperClassComparator);
                    adaptor.notifyDataSetChanged();
                } else {
                    tvNote.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        recyclerView.setAdapter(adaptor);

    }

}