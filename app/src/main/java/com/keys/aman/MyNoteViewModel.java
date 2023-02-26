package com.keys.aman;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.keys.aman.notes.addnote.NoteHelperClass;

public class MyNoteViewModel extends AndroidViewModel {
    private final MutableLiveData<NoteHelperClass> noteData;

    public MyNoteViewModel(@NonNull Application application) {
        super(application);
        noteData = new MutableLiveData<>();
    }

    public void setNoteData(NoteHelperClass value) {
        noteData.setValue(value);
    }

    public LiveData<NoteHelperClass> getNoteData() {
        return noteData;
    }
}

