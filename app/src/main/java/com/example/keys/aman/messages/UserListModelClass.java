package com.example.keys.aman.messages;

public class UserListModelClass {
    String publicUid, publicUname;

    public UserListModelClass() {
    }

    public UserListModelClass(String publicUid, String publicUname) {
        this.publicUid = publicUid;
        this.publicUname = publicUname;
    }

    public String getPublicUid() {
        return publicUid;
    }

    public void setPublicUid(String publicUid) {
        this.publicUid = publicUid;
    }

    public String getPublicUname() {
        return publicUname;
    }

    public void setPublicUname(String publicUname) {
        this.publicUname = publicUname;
    }
}
