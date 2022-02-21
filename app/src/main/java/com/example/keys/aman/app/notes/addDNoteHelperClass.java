package com.example.keys.aman.app.notes;

import java.util.Comparator;

public class addDNoteHelperClass {
    String date, title, note;
    boolean hide_note;

    public addDNoteHelperClass() {
    }

    public addDNoteHelperClass(String date, String title, String note, Boolean hide_note) {
        this.date = date;
        this.title = title;
        this.note = note;
        this.hide_note = hide_note;

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

    public boolean isHide_note() {
        return hide_note;
    }

    public void setHide_note(boolean hide_note) {
        this.hide_note = hide_note;
    }
}
