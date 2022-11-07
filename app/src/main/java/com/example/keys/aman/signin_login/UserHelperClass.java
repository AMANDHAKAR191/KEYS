package com.example.keys.aman.signin_login;

public class UserHelperClass {
    String name, email, aes_key, aes_iv, private_uid, public_uid ;

    public UserHelperClass() {

    }

    public UserHelperClass(String name, String email, String aes_key, String aes_iv, String private_uid, String public_uid) {
        this.name = name;
        this.email = email;
        this.aes_key = aes_key;
        this.aes_iv = aes_iv;
        this.private_uid = private_uid;
        this.public_uid = public_uid;
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

    public String getAes_key() {
        return aes_key;
    }

    public void setAes_key(String aes_key) {
        this.aes_key = aes_key;
    }

    public String getAes_iv() {
        return aes_iv;
    }

    public void setAes_iv(String aes_iv) {
        this.aes_iv = aes_iv;
    }

    public String getPrivate_uid() {
        return private_uid;
    }

    public void setPrivate_uid(String private_uid) {
        this.private_uid = private_uid;
    }

    public String getPublic_uid() {
        return public_uid;
    }

    public void setPublic_uid(String public_uid) {
        this.public_uid = public_uid;
    }
}
