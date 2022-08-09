package com.example.keys.aman.app.settings;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.keys.R;

public class TutorialActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}