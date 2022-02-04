package com.example.keys;

public class addDataHelperClass {
    String addDataLogin, addDataPassword, addWebsite;
    AES aes = new AES();
    public addDataHelperClass() {
    }

    public addDataHelperClass(String addDataLogin, String addDataPassword, String addWebsite) {

            this.addDataLogin = (addDataLogin);
            this.addDataPassword = (addDataPassword);
            this.addWebsite = (addWebsite);

    }

    public String getAddDataLogin() {
        return addDataLogin;
    }

    public void setAddDataLogin(String addDataLogin) {
        this.addDataLogin = addDataLogin;
    }

    public String getAddDataPassword() {
        return addDataPassword;
    }

    public void setAddDataPassword(String addDataPassword) {
        this.addDataPassword = addDataPassword;
    }

    public String getAddWebsite() {
        return addWebsite;
    }

    public void setAddWebsite(String addWebsite) {
        this.addWebsite = addWebsite;
    }
}
