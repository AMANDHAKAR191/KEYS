package com.keys.aman.notes;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.keys.aman.MyNoteViewModel;
import com.keys.aman.R;
import com.keys.aman.data.MyPreference;
import com.keys.aman.messages.MessagesFragment;
import com.keys.aman.notes.addnote.NoteHelperClass;
import com.keys.aman.signin_login.LogInActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class NotesFragment extends Fragment implements iIconChangeListener {

    public static final String shareNoteCode = "noteData";
    public static final String REQUEST_ID = "NotesFragment";
    public static DatabaseReference reference;
    public static NoteAdapter adaptorUnpinned;
    public ArrayList<NoteHelperClass> dataHolder;
    Context context;
    Activity activity;
    RecyclerView recyclerViewPinned, recyclerViewUnpinned;
    LogInActivity logInActivity = new LogInActivity();
    SharedPreferences sharedPreferences;
    TextView tvNote, tvPinned, tvUnpinned;
    SearchView searchView;
    MyPreference myPreference;
    private String uid;
    public NotesFragment(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }


    public NotesFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_notes, container, false);
        myPreference = MyPreference.getInstance(context);

        //Hooks
//        searchView = view.findViewById(R.id.search_bar);
        tvNote = view.findViewById(R.id.tv_NOTE);
        recyclerViewUnpinned = view.findViewById(R.id.recview_unpinned);
        recyclerViewUnpinned.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int i, int i1, int i2, int i3) {

//                System.out.println(recyclerViewUnpinned.computeVerticalScrollExtent());
//                System.out.println(recyclerViewUnpinned.computeHorizontalScrollOffset());
//                System.out.println(recyclerViewUnpinned.computeVerticalScrollRange());

//                if (i3 > 30) {
//                    recyclerViewPinned.setVisibility(View.VISIBLE);
//                    tvPinned.setVisibility(View.VISIBLE);
//                } else if (i3 < -30) {
//                    recyclerViewPinned.setVisibility(View.GONE);
//                    tvPinned.setVisibility(View.GONE);
//
//                }
            }
        });


        uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        recyclerViewSetData();
        return view;
    }

    public void recyclerViewSetData() {
        MyNoteViewModel viewNoteModel;
        viewNoteModel = new ViewModelProvider(requireActivity()).get(MyNoteViewModel.class);
        reference = FirebaseDatabase.getInstance().getReference("NotesData").child(uid);
        recyclerViewUnpinned.setLayoutManager(new LinearLayoutManager(context));

        dataHolder = new ArrayList<>();
        adaptorUnpinned = new NoteAdapter(dataHolder, context, activity) {
            @Override
            public void resetAdaptor() {
                dataHolder.clear();
                Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void refreshRecView() {
                dataholder.clear();
                recyclerViewSetData();
            }

            @Override
            public void shareNotes(NoteHelperClass noteData) {
                super.shareNotes(noteData);
                //
                viewNoteModel.setNoteData(noteData);

                Log.e("shareNote", "Check2: NotesFragment: " + noteData);
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
        recyclerViewUnpinned.setAdapter(adaptorUnpinned);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        NoteHelperClass data = ds.getValue(NoteHelperClass.class);
                        assert data != null;
                        if (data.isHideNote()) {

                        } else {
                            dataHolder.add(data);
                        }

                    }
                    Collections.sort(dataHolder, NoteHelperClass.addDNoteHelperClassComparator);
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

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void changeIcon(int resourceId) {
        
    }
}