package com.example.keys.aman.messages;

public class ChatModelClass {
    String message,dateAndTime, publicUid;;

    public String getDateAndTime() {
        return dateAndTime;
    }

    public ChatModelClass() {
    }

    public ChatModelClass(String message, String dateAndTime, String publicUid) {
        this.message = message;
        this.dateAndTime = dateAndTime;
        this.publicUid = publicUid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setDateAndTime(String dateAndTime) {
        this.dateAndTime = dateAndTime;
    }

    public String getPublicUid() {
        return publicUid;
    }

    public void setPublicUid(String publicUid) {
        this.publicUid = publicUid;
    }
}
