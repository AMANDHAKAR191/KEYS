package com.example.keys.aman.app.home.addpassword;

public class websiteHelper {
    String website_image_name, website_login_url;

    public websiteHelper() {
    }

    public websiteHelper(String website_image_url, String website_login_url) {
        this.website_image_name = website_image_url;
        this.website_login_url = website_login_url;
    }

    public String getWebsite_image_name() {
        return website_image_name;
    }

    public void setWebsite_image_name(String website_image_name) {
        this.website_image_name = website_image_name;
    }

    public String getWebsite_login_url() {
        return website_login_url;
    }

    public void setWebsite_login_url(String website_login_url) {
        this.website_login_url = website_login_url;
    }
}
