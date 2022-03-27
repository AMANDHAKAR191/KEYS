package com.example.keys.aman.app.home.addpassword;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.keys.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class wesiteListFragment extends DialogFragment {
    RecyclerView recyclerView;
    Context context;
    private OnCompleteListener mListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wesite_list, container, false);
        recyclerView = view.findViewById(R.id.recview);
        recyclerviewsetdata();
        return view;
    }

    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        this.context = activity;
        try {
            this.mListener = (OnCompleteListener) activity;
        } catch (final ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnCompleteListener");
        }

    }
    public static interface OnCompleteListener {
        public abstract void onComplete(String time);
    }



    public void recyclerviewsetdata() {

        DatabaseReference databaseReference;
        websiteListAdaptor websiteListAdaptor;
        ArrayList<websiteHelper> dataholder;




        databaseReference = FirebaseDatabase.getInstance().getReference("websitelist");
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        dataholder = new ArrayList<>();
        websiteListAdaptor = new websiteListAdaptor(dataholder, context);
        recyclerView.setAdapter(websiteListAdaptor);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.hasFixedSize();
//        recyclerView.setItemAnimator(new DefaultItemAnimator());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        websiteHelper data = ds.getValue(websiteHelper.class);
                        System.out.println(data);
                        //Toast.makeText(addPasswordData.this,data,Toast.LENGTH_SHORT).show();
                        assert data != null;
                        dataholder.add(data);
                    }
                    websiteListAdaptor.notifyDataSetChanged();

                } else {
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}