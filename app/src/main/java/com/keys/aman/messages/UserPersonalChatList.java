package com.keys.aman.messages;

public class UserPersonalChatList {
    String otherUserPublicUid, otherUserPublicUname, commonEncryptionKey, commonEncryptionIv;
    boolean knowUser;
    String LastMessage;

    public UserPersonalChatList() {
    }

    public UserPersonalChatList(String otherUserPublicUid, String otherUserPublicUname, String commonEncryptionKey, String commonEncryptionIv, boolean knowUser, String lastMessage) {
        this.otherUserPublicUid = otherUserPublicUid;
        this.otherUserPublicUname = otherUserPublicUname;
        this.commonEncryptionKey = commonEncryptionKey;
        this.commonEncryptionIv = commonEncryptionIv;
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

    public String getCommonEncryptionKey() {
        return commonEncryptionKey;
    }

    public String getCommonEncryptionIv() {
        return commonEncryptionIv;
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

    public void setCommonEncryptionKey(String commonEncryptionKey) {
        this.commonEncryptionKey = commonEncryptionKey;
    }

    public void setCommonEncryptionIv(String commonEncryptionIv) {
        this.commonEncryptionIv = commonEncryptionIv;
    }

    public void setKnowUser(boolean knowUser) {
        this.knowUser = knowUser;
    }

    public void setLastMessage(String lastMessage) {
        LastMessage = lastMessage;
    }
}
