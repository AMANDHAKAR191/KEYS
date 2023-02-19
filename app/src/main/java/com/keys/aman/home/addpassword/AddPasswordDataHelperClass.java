package com.keys.aman.home.addpassword;

import java.util.Comparator;

public class AddPasswordDataHelperClass {
    String date, addDataLogin, addDataPassword, addWebsite_name, addWebsite_link;

    public AddPasswordDataHelperClass() {
    }

    public AddPasswordDataHelperClass(String date, String addDataLogin, String addDataPassword, String addWebsite_name, String addWebsite_link) {
        this.date = date;
        this.addDataLogin = addDataLogin;
        this.addDataPassword = addDataPassword;
        this.addWebsite_name = addWebsite_name;
        this.addWebsite_link = addWebsite_link;
    }

    public static Comparator<AddPasswordDataHelperClass> addDataHelperClassComparator = new Comparator<AddPasswordDataHelperClass>() {
        @Override
        public int compare(AddPasswordDataHelperClass a1, AddPasswordDataHelperClass a2) {
            return a1.getAddWebsite_name().compareTo(a2.getAddWebsite_name());
        }
    };

    public String getDate() {
        return date;
    }

    public String getAddDataLogin() {
        return addDataLogin;
    }

    public String getAddDataPassword() {
        return addDataPassword;
    }

    public String getAddWebsite_name() {
        return addWebsite_name;
    }

    public String getAddWebsite_link() {
        return addWebsite_link;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setAddDataLogin(String addDataLogin) {
        this.addDataLogin = addDataLogin;
    }

    public void setAddDataPassword(String addDataPassword) {
        this.addDataPassword = addDataPassword;
    }

    public void setAddWebsite_name(String addWebsite_name) {
        this.addWebsite_name = addWebsite_name;
    }

    public void setAddWebsite_link(String addWebsite_link) {
        this.addWebsite_link = addWebsite_link;
    }
}
