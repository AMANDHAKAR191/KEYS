package com.keys.aman;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.keys.aman.home.addpassword.PasswordHelperClass;

public class MyPasswordViewModel extends AndroidViewModel {
    private final MutableLiveData<PasswordHelperClass> passwordData;

    public MyPasswordViewModel(@NonNull Application application) {
        super(application);
        passwordData = new MutableLiveData<>();
    }

    public void setPasswordData(PasswordHelperClass value) {
        passwordData.setValue(value);
    }

    public LiveData<PasswordHelperClass> getPasswordData() {
        return passwordData;
    }
}

