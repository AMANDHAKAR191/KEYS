package com.example.keys.aman.app.signin_login;

public class UserHelperClass {
    String name, mobile, email, aes_key, aes_iv, uid;

    public UserHelperClass(){

    }

    public UserHelperClass(String name, String mobile, String email , String eas_key, String aes_iv, String uid) {
        this.name = name;
        this.mobile = mobile;
        this.email = email;
        this.aes_key = eas_key;
        this.aes_iv = aes_iv;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public String getMobile() {
        return mobile;
    }

    public String getEmail() {
        return email;
    }


    public String getAes_key() {
        return aes_key;
    }

    public String getAes_iv() {
        return aes_iv;
    }

    public String getUid() {
        return uid;
    }
}
