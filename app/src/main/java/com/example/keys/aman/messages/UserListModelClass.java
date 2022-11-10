package com.example.keys.aman.messages;

public class UserListModelClass {
    String publicUid, publicUname;
    boolean knowUser;

    public UserListModelClass() {
    }

    public UserListModelClass(String publicUid, String publicUname, boolean knowUser) {
        this.publicUid = publicUid;
        this.publicUname = publicUname;
        this.knowUser = knowUser;
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

    public boolean isKnowUser() {
        return knowUser;
    }

    public void setKnowUser(boolean knowUser) {
        this.knowUser = knowUser;
    }
}
