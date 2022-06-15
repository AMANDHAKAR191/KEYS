package com.example.keys.aman.app.home.addpassword;

import java.util.Comparator;

public class addDataHelperClass {
    String date, addDataLogin, addDataPassword, addWebsite;

    public addDataHelperClass() {
    }

    public addDataHelperClass(String date, String addDataLogin, String addDataPassword, String addWebsite) {
        this.date = date;
        this.addDataLogin = (addDataLogin);
        this.addDataPassword = (addDataPassword);
        this.addWebsite = (addWebsite);

    }

    public static Comparator<addDataHelperClass> addDataHelperClassComparator = new Comparator<addDataHelperClass>() {
        @Override
        public int compare(addDataHelperClass a1, addDataHelperClass a2) {
            return a1.getAddWebsite().compareTo(a2.getAddWebsite());
        }
    };

    public String getDate() {
        return date;
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
