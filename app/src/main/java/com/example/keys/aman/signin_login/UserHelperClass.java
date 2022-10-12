package com.example.keys.aman.signin_login;

public class UserHelperClass {
    String name, email, aes_key, aes_iv, uid;

    public UserHelperClass() {

    }

    public UserHelperClass(String name, String email, String aes_key, String aes_iv, String uid) {
        this.name = name;

        this.email = email;
        this.aes_key = aes_key;
        this.aes_iv = aes_iv;
        this.uid = uid;
    }

    public String getName() {
        return name;
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
