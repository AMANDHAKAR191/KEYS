package com.example.keys;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

public class dialogForDBTakingLoingTime {
    Activity activity;
    AlertDialog dialog;

    dialogForDBTakingLoingTime(Activity thisActivity){
        activity = thisActivity;
    }
    void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.dialogdbtakinglongtime,null));
        dialog = builder.create();
        dialog.show();
    }
    void dismissbar(){
        dialog.dismiss();
    }
}
