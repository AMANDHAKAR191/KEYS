package com.example.keys.aman.app.home.addpassword;

public class websiteHelper {
    String website_image, website_login_url, website_name;

    public websiteHelper() {
    }

    public websiteHelper(String website_image, String website_name, String website_login_url) {
        this.website_image = website_image;
        this.website_login_url = website_login_url;
        this.website_name = website_name;
    }

    public String getWebsite_image() {
        return website_image;
    }

    public String getWebsite_login_url() {
        return website_login_url;
    }

    public String getWebsite_name() {
        return website_name;
    }
}
