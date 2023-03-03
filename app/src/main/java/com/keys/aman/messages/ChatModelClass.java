package com.keys.aman.messages;

import com.keys.aman.home.addpassword.PasswordHelperClass;
import com.keys.aman.notes.addnote.NoteHelperClass;

public class ChatModelClass {
    String message,dateAndTime, publicUid, type, status;
    PasswordHelperClass PasswordModelClass;
    NoteHelperClass noteModelClass;


    public ChatModelClass() {
    }

    public ChatModelClass(String message, String dateAndTime, String publicUid, String type, String status, PasswordHelperClass passwordModelClass, NoteHelperClass noteModelClass) {
        this.message = message;
        this.dateAndTime = dateAndTime;
        this.publicUid = publicUid;
        this.type = type;
        this.status = status;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public PasswordHelperClass getPasswordModelClass() {
        return PasswordModelClass;
    }

    public void setPasswordModelClass(PasswordHelperClass passwordModelClass) {
        PasswordModelClass = passwordModelClass;
    }

    public NoteHelperClass getNoteModelClass() {
        return noteModelClass;
    }

    public void setNoteModelClass(NoteHelperClass noteModelClass) {
        this.noteModelClass = noteModelClass;
    }
}