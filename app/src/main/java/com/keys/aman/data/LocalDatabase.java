package com.keys.aman.data;

import android.content.Context;

public class LocalDatabase {
    Context context;
    static LocalDatabase sInstance;


    public LocalDatabase(Context context) {
        this.context = context;
    }
    public static LocalDatabase getInstance(Context context){
        if (sInstance == null){
            sInstance = new LocalDatabase(context);
            System.out.println("Connection Successful");
        }
        return sInstance;
    }
    public void getDatabases(){
        System.out.println("Database List: ");
    }
}
