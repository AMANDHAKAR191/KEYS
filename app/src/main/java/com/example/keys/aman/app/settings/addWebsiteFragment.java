package com.example.keys.aman.app.settings;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.keys.R;
import com.example.keys.aman.app.home.addpassword.addPasswordData;
import com.example.keys.aman.app.home.addpassword.websiteHelper;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class addWebsiteFragment extends DialogFragment {
    TextInputLayout til_addwebsite_name, til_addwebsite_url;
    Button addWebsite;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_website, container, false);
        //Hooks
        til_addwebsite_name = view.findViewById(R.id.til_addwebsitename);
        til_addwebsite_url = view.findViewById(R.id.til_addwebsiteurl);
        addWebsite = view.findViewById(R.id.addWebsite);

        addWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String website_name = Objects.requireNonNull(til_addwebsite_name.getEditText()).getText().toString();
                String website_url = Objects.requireNonNull(til_addwebsite_url.getEditText()).getText().toString();

                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference addDataRef = firebaseDatabase.getReference("websitelist").child(website_name);

                websiteHelper websiteHelper = new websiteHelper(website_name,website_url);
                addDataRef.setValue(websiteHelper);
                dismiss();
            }
        });
        return view;
    }
}