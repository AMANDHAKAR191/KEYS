package com.example.keys.aman.messages;

import com.example.keys.aman.home.addpassword.AddPasswordDataHelperClass;
import com.example.keys.aman.notes.addnote.AddNoteDataHelperClass;

public class ChatModelClass {
    String message,dateAndTime, publicUid, type;
    AddPasswordDataHelperClass PasswordModelClass;
    AddNoteDataHelperClass noteModelClass;


    public ChatModelClass() {
    }

    public ChatModelClass(String message, String dateAndTime, String publicUid, String type, AddPasswordDataHelperClass passwordModelClass, AddNoteDataHelperClass noteModelClass) {
        this.message = message;
        this.dateAndTime = dateAndTime;
        this.publicUid = publicUid;
        this.type = type;
        PasswordModelClass = passwordModelClass;
        this.noteModelClass = noteModelClass;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDateAndTime() {
        return dateAndTime;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public AddPasswordDataHelperClass getPasswordModelClass() {
        return PasswordModelClass;
    }

    public void setPasswordModelClass(AddPasswordDataHelperClass passwordModelClass) {
        PasswordModelClass = passwordModelClass;
    }

    public AddNoteDataHelperClass getNoteModelClass() {
        return noteModelClass;
    }

    public void setNoteModelClass(AddNoteDataHelperClass noteModelClass) {
        this.noteModelClass = noteModelClass;
    }
}
