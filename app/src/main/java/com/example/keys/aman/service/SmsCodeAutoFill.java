package com.example.keys.aman.service;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.phone.SmsCodeAutofillClient;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.internal.ApiKey;
import com.google.android.gms.tasks.Task;

public class SmsCodeAutoFill implements SmsCodeAutofillClient {


    @NonNull
    @Override
    public Task<Integer> checkPermissionState() {
        
        return null;
    }

    @NonNull
    @Override
    public Task<Boolean> hasOngoingSmsRequest(@NonNull String s) {

        return null;
    }

    @NonNull
    @Override
    public Task<Void> startSmsCodeRetriever() {
        return null;
    }

    @NonNull
    @Override
    public ApiKey<Api.ApiOptions.NoOptions> getApiKey() {
        return null;
    }
}
