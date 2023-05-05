package com.keys.aman.signin_login;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "signupdata")
public class UserHelperClass {
    @ColumnInfo(name = "name")
    String name;
    @ColumnInfo(name = "email")
    String email;
    @ColumnInfo(name = "aesKey")
    String aesKey;
    @ColumnInfo(name = "aesIv")
    String aesIv;
    @ColumnInfo(name = "privateUid")
    @PrimaryKey
    @NonNull
    String privateUid;
    @ColumnInfo(name = "publicUid")
    String publicUid ;

    @Ignore
    public UserHelperClass() {

    }

    public UserHelperClass(String name, String email, String aesKey, String aesIv, String privateUid, String publicUid) {
        this.name = name;
        this.email = email;
        this.aesKey = aesKey;
        this.aesIv = aesIv;
        this.privateUid = privateUid;
        this.publicUid = publicUid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAesKey() {
        return aesKey;
    }

    public void setAesKey(String aesKey) {
        this.aesKey = aesKey;
    }

    public String getAesIv() {
        return aesIv;
    }

    public void setAesIv(String aesIv) {
        this.aesIv = aesIv;
    }

    public String getPrivateUid() {
        return privateUid;
    }

    public void setPrivateUid(String privateUid) {
        this.privateUid = privateUid;
    }

    public String getPublicUid() {
        return publicUid;
    }

    public void setPublicUid(String publicUid) {
        this.publicUid = publicUid;
    }
}
