package com.example.keys.aman.app.home.addpassword;

import java.util.Comparator;

public class addDataHelperClass {
    String date, addDataLogin, addDataPassword, addWebsite_name, addWebsite_link;

    public addDataHelperClass() {
    }

    public addDataHelperClass(String date, String addDataLogin, String addDataPassword, String addWebsite_name, String addWebsite_link) {
        this.date = date;
        this.addDataLogin = addDataLogin;
        this.addDataPassword = addDataPassword;
        this.addWebsite_name = addWebsite_name;
        this.addWebsite_link = addWebsite_link;
    }

    public static Comparator<addDataHelperClass> addDataHelperClassComparator = new Comparator<addDataHelperClass>() {
        @Override
        public int compare(addDataHelperClass a1, addDataHelperClass a2) {
            return a1.getAddWebsite_name().compareTo(a2.getAddWebsite_name());
        }
    };

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public String getAddWebsite_name() {
        return addWebsite_name;
    }

    public void setAddWebsite_name(String addWebsite_name) {
        this.addWebsite_name = addWebsite_name;
    }

    public String getAddWebsite_link() {
        return addWebsite_link;
    }

    public void setAddWebsite_link(String addWebsite_link) {
        this.addWebsite_link = addWebsite_link;
    }
}
