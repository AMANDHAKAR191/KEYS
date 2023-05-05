package com.keys.aman.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.keys.aman.signin_login.UserHelperClass;

@Database(entities = {UserHelperClass.class}, version = 1)
public abstract class AppLocalDatabase extends RoomDatabase {
    public abstract UserHelperDAO userHelperDao();
}
