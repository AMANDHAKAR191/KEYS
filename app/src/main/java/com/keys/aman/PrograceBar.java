package com.keys.aman;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/* TODO Checked on 12/10/2022
    All is ok
 */
public class PrograceBar {
    final Activity activity;
    AlertDialog dialog;
    TextView tvProgressStatus;

    public PrograceBar(Activity thisActivity){
        activity = thisActivity;
    }
    public void showDialog(){
        View view = LayoutInflater.from(activity).inflate(R.layout.prograce_bar_layout,null);
        tvProgressStatus = view.findViewById(R.id.tv_progress_status);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//        LayoutInflater inflater = activity.getLayoutInflater();
//        builder.setView(inflater.inflate(R.layout.prograce_bar_layout,null));
        builder.setView(view);
        dialog = builder.create();
        dialog.show();
    }
    public void updateProgress(String message){
        tvProgressStatus.setText(message);
    }
    public void dismissbar(){
        dialog.dismiss();
    }
}
