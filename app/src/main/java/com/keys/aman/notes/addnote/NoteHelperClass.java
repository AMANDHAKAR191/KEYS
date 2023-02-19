package com.keys.aman.notes.addnote;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Comparator;

public class NoteHelperClass implements Parcelable {
    String date, title, note;
    boolean isHideNote, isPinned;

    public NoteHelperClass() {
    }

    public NoteHelperClass(String date, String title, String note, boolean isHideNote, boolean isPinned) {
        this.date = date;
        this.title = title;
        this.note = note;
        this.isHideNote = isHideNote;
        this.isPinned = isPinned;
    }

    public static Comparator<NoteHelperClass> addDNoteHelperClassComparator = new Comparator<NoteHelperClass>() {
        @Override
        public int compare(NoteHelperClass a1, NoteHelperClass a2) {
            return a2.getDate().compareTo(a1.getDate());
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        // Write the values of the fields to the parcel
        parcel.writeString(date);
        parcel.writeString(title);
        parcel.writeString(note);
        parcel.writeByte((byte) (isHideNote ? 1 : 0));
        parcel.writeByte((byte) (isPinned ? 1 : 0));
    }

    public static final Parcelable.Creator<NoteHelperClass> CREATOR = new Parcelable.Creator<NoteHelperClass>() {
        @Override
        public NoteHelperClass createFromParcel(Parcel in) {
            // Read the values of the fields from the parcel
            String date = in.readString();
            String title = in.readString();
            String note = in.readString();
            boolean isHideNote = in.readByte() != 0;
            boolean isPinned = in.readByte() != 0;

            // Create a new AddNoteDataHelperClass object with the values from the parcel
            return new NoteHelperClass(date, title, note, isHideNote, isPinned);
        }

        @Override
        public NoteHelperClass[] newArray(int size) {
            return new NoteHelperClass[size];
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
