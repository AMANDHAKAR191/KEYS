package com.keys.aman.home.addpassword;

import java.util.Comparator;

public class WebsiteHelperClass {
    String website_login_url, website_name;


    public WebsiteHelperClass() {
    }

    public WebsiteHelperClass(String website_name, String website_login_url) {
        this.website_login_url = website_login_url;
        this.website_name = website_name;
    }

    public static Comparator<WebsiteHelperClass> addDataHelperClassComparator = new Comparator<WebsiteHelperClass>() {
        @Override
        public int compare(WebsiteHelperClass a1, WebsiteHelperClass a2) {
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
