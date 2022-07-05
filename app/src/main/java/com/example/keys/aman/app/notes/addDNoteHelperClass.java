package com.example.keys.aman.app.notes;

import java.util.Comparator;

public class addDNoteHelperClass {
    String date, title, note;
    boolean isHideNote;

    public addDNoteHelperClass() {
    }

    public addDNoteHelperClass(String date, String title, String note, Boolean isHideNote) {
        this.date = date;
        this.title = title;
        this.note = note;
        this.isHideNote = isHideNote;

    }
    public static Comparator<addDNoteHelperClass> addDNoteHelperClassComparator = new Comparator<addDNoteHelperClass>() {
        @Override
        public int compare(addDNoteHelperClass a1, addDNoteHelperClass a2) {
            return a2.getDate().compareTo(a1.getDate());
        }
    };

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public boolean isHideNote() {
        return isHideNote;
    }

    public void setHideNote(boolean isHideNote) {
        this.isHideNote = isHideNote;
    }
}
