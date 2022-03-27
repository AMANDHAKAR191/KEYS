package com.example.keys.aman.app.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.DialogFragment;

import com.example.keys.R;
import com.example.keys.aman.app.home.addpassword.websiteHelper;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class addWebsiteFragment extends DialogFragment {
    TextInputLayout til_addwebsite_img, til_addwebsite_name, til_addwebsite_url;
    Button addWebsite;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_website, container, false);
        //Hooks
        til_addwebsite_img = view.findViewById(R.id.til_addwebsiteimg);
        til_addwebsite_name = view.findViewById(R.id.til_addwebsitename);
        til_addwebsite_url = view.findViewById(R.id.til_addwebsiteurl);
        addWebsite = view.findViewById(R.id.addWebsite);

        addWebsite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String website_img = Objects.requireNonNull(til_addwebsite_img.getEditText()).getText().toString();
                String website_name = Objects.requireNonNull(til_addwebsite_name.getEditText()).getText().toString();
                String website_url = Objects.requireNonNull(til_addwebsite_url.getEditText()).getText().toString();

                FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                DatabaseReference addDataRef;

                addDataRef = firebaseDatabase.getReference("websitelist").child((website_name));

                websiteHelper websiteHelper = new websiteHelper(website_img, website_name, website_url);
                addDataRef.setValue(websiteHelper);
                dismiss();
            }
        });
        return view;
    }
//    public String getHostName(String url) throws URISyntaxException {
//        URI uri = new URI(url);
//        String hostname = uri.getHost();
//        // to provide faultproof result, check if not null then return only hostname, without www.
//        if (hostname != null) {
//            return hostname.startsWith("www.") ? hostname.substring(4) : hostname;
//        }
//        return hostname;
//    }
}