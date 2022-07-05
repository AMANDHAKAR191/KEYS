package com.example.keys.aman.app.home.addpassword;

import java.util.Comparator;

public class websiteHelper {
    String website_login_url, website_name;


    public websiteHelper() {
    }

    public websiteHelper(String website_name, String website_login_url) {
        this.website_login_url = website_login_url;
        this.website_name = website_name;
    }

    public static Comparator<websiteHelper> addDataHelperClassComparator = new Comparator<websiteHelper>() {
        @Override
        public int compare(websiteHelper a1, websiteHelper a2) {
            return a1.getWebsite_name().compareTo(a2.getWebsite_name());
        }
    };

    public String getWebsite_login_url() {
        return website_login_url;
    }

    public void setWebsite_login_url(String website_login_url) {
        this.website_login_url = website_login_url;
    }

    public String getWebsite_name() {
        return website_name;
    }

    public void setWebsite_name(String website_name) {
        this.website_name = website_name;
    }
}
