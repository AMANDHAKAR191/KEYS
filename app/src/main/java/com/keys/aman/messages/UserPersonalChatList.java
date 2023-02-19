package com.keys.aman.messages;

public class UserPersonalChatList {
    String otherUserPublicUid, otherUserPublicUname;
    boolean knowUser;
    String LastMessage;

    public UserPersonalChatList() {
    }

    public UserPersonalChatList(String otherUserPublicUid, String otherUserPublicUname, boolean knowUser, String lastMessage) {
        this.otherUserPublicUid = otherUserPublicUid;
        this.otherUserPublicUname = otherUserPublicUname;
        this.knowUser = knowUser;
        LastMessage = lastMessage;
    }
    //Getter

    public String getOtherUserPublicUid() {
        return otherUserPublicUid;
    }

    public String getOtherUserPublicUname() {
        return otherUserPublicUname;
    }

    public boolean isKnowUser() {
        return knowUser;
    }

    public String getLastMessage() {
        return LastMessage;
    }
    //Setter

    public void setOtherUserPublicUid(String otherUserPublicUid) {
        this.otherUserPublicUid = otherUserPublicUid;
    }

    public void setOtherUserPublicUname(String otherUserPublicUname) {
        this.otherUserPublicUname = otherUserPublicUname;
    }

    public void setKnowUser(boolean knowUser) {
        this.knowUser = knowUser;
    }

    public void setLastMessage(String lastMessage) {
        LastMessage = lastMessage;
    }
}
