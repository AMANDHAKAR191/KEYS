package com.keys.aman.import_data;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;

public class ImportData {
    public String getPassword(Context context){
        AccountManager accountManager = AccountManager.get(context);
        Account[] accounts = accountManager.getAccountsByType("com.google");
        if (accounts.length == 0){
            return null;
        }
        Account account = accounts[0];
        return accountManager.getPassword(account);
    }

}
