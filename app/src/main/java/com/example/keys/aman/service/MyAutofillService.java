package com.example.keys.aman.service;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Service;
import android.os.Bundle;

import com.example.keys.R;

import java.security.Provider;

public class MyAutofillService extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_autofill_service);
    }
}