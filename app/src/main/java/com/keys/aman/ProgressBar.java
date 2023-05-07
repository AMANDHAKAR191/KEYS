/*created by AMAN DHAKAR
Last update 07/05/2025*/
package com.keys.aman;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
public class ProgressBar implements iProgressBar{
    final Activity activity;
    AlertDialog dialog;
    TextView tvProgressStatus;

    public ProgressBar(Activity thisActivity){
        activity = thisActivity;
    }
    public void showDialog(){
        View view = LayoutInflater.from(activity).inflate(R.layout.prograce_bar_layout,null);
        tvProgressStatus = view.findViewById(R.id.tv_progress_status);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(view);
        dialog = builder.create();
        dialog.show();
    }
    public void updateProgressBar(String message){
        tvProgressStatus.setText(message);
    }

    @Override
    public void dismissDialog() {
        dialog.dismiss();
    }
    public boolean isDialogShowing() {
        return dialog != null && dialog.isShowing();
    }

}
