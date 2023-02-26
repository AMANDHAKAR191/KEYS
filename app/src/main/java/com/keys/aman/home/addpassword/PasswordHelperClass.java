package com.keys.aman.home.addpassword;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Comparator;

public class PasswordHelperClass implements Parcelable {
    String date, addDataLogin, addDataPassword, addWebsite_name, addWebsite_link;

    public PasswordHelperClass() {
    }

    public PasswordHelperClass(String date, String addDataLogin, String addDataPassword, String addWebsite_name, String addWebsite_link) {
        this.date = date;
        this.addDataLogin = addDataLogin;
        this.addDataPassword = addDataPassword;
        this.addWebsite_name = addWebsite_name;
        this.addWebsite_link = addWebsite_link;
    }

    public static Comparator<PasswordHelperClass> addDataHelperClassComparator = new Comparator<PasswordHelperClass>() {
        @Override
        public int compare(PasswordHelperClass a1, PasswordHelperClass a2) {
            return a1.getAddWebsite_name().compareTo(a2.getAddWebsite_name());
        }
    };

    protected PasswordHelperClass(Parcel in) {
        date = in.readString();
        addDataLogin = in.readString();
        addDataPassword = in.readString();
        addWebsite_name = in.readString();
        addWebsite_link = in.readString();
    }

    public static final Creator<PasswordHelperClass> CREATOR = new Creator<PasswordHelperClass>() {
        @Override
        public PasswordHelperClass createFromParcel(Parcel in) {
            return new PasswordHelperClass(in);
        }

        @Override
        public PasswordHelperClass[] newArray(int size) {
            return new PasswordHelperClass[size];
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(date);
        parcel.writeString(addDataLogin);
        parcel.writeString(addDataPassword);
        parcel.writeString(addWebsite_name);
        parcel.writeString(addWebsite_link);
    }
}
