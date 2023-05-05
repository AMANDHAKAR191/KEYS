package com.keys.aman.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.keys.aman.signin_login.UserHelperClass;

@Dao
public interface UserHelperDAO {
    @Insert
    void saveUserData(UserHelperClass userHelperClass);

    @Query("select * from signupdata where privateUid==:Uid")
    UserHelperClass getUserData(String Uid);
}
