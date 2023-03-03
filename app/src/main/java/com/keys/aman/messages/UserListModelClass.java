package com.keys.aman.messages;

public class UserListModelClass {
    String publicUid, publicUname;
    boolean profileAccess;

    public UserListModelClass() {
    }

    public UserListModelClass(String publicUid, String publicUname, boolean profileAccess) {
        this.publicUid = publicUid;
        this.publicUname = publicUname;
        this.profileAccess = profileAccess;
    }

    //Getter

    public String getPublicUid() {
        return publicUid;
    }

    public String getPublicUname() {
        return publicUname;
    }

    public boolean isProfileAccess() {
        return profileAccess;
    }


    //Setter


    public void setPublicUid(String publicUid) {
        this.publicUid = publicUid;
    }

    public void setPublicUname(String publicUname) {
        this.publicUname = publicUname;
    }

    public void setProfileAccess(boolean profileAccess) {
        this.profileAccess = profileAccess;
    }

}