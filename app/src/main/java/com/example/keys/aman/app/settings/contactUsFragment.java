package com.example.keys.aman.app.settings;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.keys.R;


public class contactUsFragment extends DialogFragment {
    ImageButton img_gmail, img_facebook, img_instagram;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_us,container,false);
        img_gmail = view.findViewById(R.id.img_gmail);
        img_facebook = view.findViewById(R.id.img_facebook);
        img_instagram = view.findViewById(R.id.img_instagram);
        img_gmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openemail = new Intent(Intent.ACTION_VIEW, Uri.parse("mailto:amandhaker191@gmail.com"));
                startActivity(openemail);
            }
        });
        img_facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openfacebook = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/aman.dhaker.543/"));
                startActivity(openfacebook);
            }
        });
        img_instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openimstagram = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/amandhaker123/"));
                startActivity(openimstagram);
            }
        });
        return view;
    }
}