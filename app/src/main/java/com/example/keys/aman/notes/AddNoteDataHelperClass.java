package com.example.keys.aman.notes;

import java.util.Comparator;

public class AddNoteDataHelperClass {
    String date, title, note;
    boolean isHideNote, isPinned;

    public AddNoteDataHelperClass() {
    }

    public AddNoteDataHelperClass(String date, String title, String note, boolean isHideNote, boolean isPinned) {
        this.date = date;
        this.title = title;
        this.note = note;
        this.isHideNote = isHideNote;
        this.isPinned = isPinned;
    }

    public static Comparator<AddNoteDataHelperClass> addDNoteHelperClassComparator = new Comparator<AddNoteDataHelperClass>() {
        @Override
        public int compare(AddNoteDataHelperClass a1, AddNoteDataHelperClass a2) {
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

    public void setHideNote(boolean hideNote) {
        isHideNote = hideNote;
    }

    public boolean isPinned() {
        return isPinned;
    }

    public void setPinned(boolean pinned) {
        isPinned = pinned;
    }
}
