package com.keys.aman;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.keys.aman.notes.addnote.NoteHelperClass;

public class MyViewModel extends AndroidViewModel {
    private final MutableLiveData<NoteHelperClass> noteData;

    public MyViewModel(@NonNull Application application) {
        super(application);
        noteData = new MutableLiveData<>();
    }

    public void setData(NoteHelperClass value) {
        noteData.setValue(value);
    }

    public LiveData<NoteHelperClass> getData() {
        return noteData;
    }
}

