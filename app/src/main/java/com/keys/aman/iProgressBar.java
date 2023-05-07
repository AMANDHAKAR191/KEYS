package com.keys.aman;

public interface iProgressBar {
    void showDialog();
    void updateProgressBar(String message);
    void dismissDialog();

    boolean isDialogShowing();

}
