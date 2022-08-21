package com.example.keys.aman;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.keys.R;

public class checkInternetFragment extends AppCompatActivity {

    private ConnectivityManager connectivityManager;
    Button btn_retry;
    private boolean isConnected;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_internet_dialog);
        btn_retry = findViewById(R.id.btn_retry);

    }


    private void registetNetworkCallBack(){

        try {

            connectivityManager = (ConnectivityManager) getApplicationContext()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            connectivityManager.registerDefaultNetworkCallback(
                    new ConnectivityManager.NetworkCallback(){
                        @Override
                        public void onAvailable(@NonNull Network network) {
                            super.onAvailable(network);
                            isConnected = true;
                        }

                        @Override
                        public void onLost(@NonNull Network network) {
                            super.onLost(network);
                            isConnected = false;
                        }
                    });



        }catch (Exception e){
            isConnected = false;
        }
    }

    private void unRegistetNetworkCallBack(){
        connectivityManager.unregisterNetworkCallback(
                new ConnectivityManager.NetworkCallback());
    }

    @Override
    protected void onResume() {
        super.onResume();
        registetNetworkCallBack();
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        unRegistetNetworkCallBack();
//    }


    public void checkInternetFragment(View view) {
        if (isConnected){
            Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(),SplashActivity.class);
            startActivity(intent);
            finish();
        }else {
            Toast.makeText(getApplicationContext(), "Not Connected", Toast.LENGTH_SHORT).show();
        }
    }
}