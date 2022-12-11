package com.example.keys.aman.messages;

public class ChatModelClass {
    String message,dateAndTime, publicUid;;
    String addDataLogin, addDataPassword, addWebsite_name;
    String title, note;

    public ChatModelClass() {
    }

    public ChatModelClass(String message, String dateAndTime, String publicUid, String addDataLogin, String addDataPassword, String addWebsite_name, String title, String note) {
        this.message = message;
        this.dateAndTime = dateAndTime;
        this.publicUid = publicUid;
        this.addDataLogin = addDataLogin;
        this.addDataPassword = addDataPassword;
        this.addWebsite_name = addWebsite_name;
        this.title = title;
        this.note = note;
    }

    public String getMessage() {
        return message;
    }

    public String getDateAndTime() {
        return dateAndTime;
    }

    public String getPublicUid() {
        return publicUid;
    }

    public String getAddDataLogin() {
        return addDataLogin;
    }

    public String getAddDataPassword() {
        return addDataPassword;
    }

    public String getAddWebsite_name() {
        return addWebsite_name;
    }

    public String getTitle() {
        return title;
    }

    public String getNote() {
        return note;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setDateAndTime(String dateAndTime) {
        this.dateAndTime = dateAndTime;
    }

    public void setPublicUid(String publicUid) {
        this.publicUid = publicUid;
    }

    public void setAddDataLogin(String addDataLogin) {
        this.addDataLogin = addDataLogin;
    }

    public void setAddDataPassword(String addDataPassword) {
        this.addDataPassword = addDataPassword;
    }

    public void setAddWebsite_name(String addWebsite_name) {
        this.addWebsite_name = addWebsite_name;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
