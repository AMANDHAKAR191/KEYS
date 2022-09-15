package com.example.keys.aman;

import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.example.keys.aman.home.addpassword.AddPasswordDataHelperClass;
import com.example.keys.aman.notes.AddNoteDataHelperClass;
import com.example.keys.aman.signin_login.LogInActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class DatabaseProcess {
//    private DatabaseReference reference;
    SharedPreferences sharedPreferences;
    AddNoteDataHelperClass addNoteDataHelperClass;
    AddPasswordDataHelperClass addPasswordDataHelperClass;
    LogInActivity logInActivity = new LogInActivity();
    private boolean turn = false;

    public DatabaseProcess() {
    }

    public DatabaseProcess(AddNoteDataHelperClass addNoteDataHelperClass) {
        this.addNoteDataHelperClass = addNoteDataHelperClass;
    }

    public DatabaseProcess(AddPasswordDataHelperClass addPasswordDataHelperClass) {
        this.addPasswordDataHelperClass = addPasswordDataHelperClass;
    }

    public void storeData(DatabaseReference reference) {
        String comingDate = addNoteDataHelperClass.getDate();
        reference.child(comingDate).setValue(addNoteDataHelperClass);
    }

    public void retrieveData() {

    }

    public boolean checkUser(DatabaseReference reference) {
        LogInActivity logInActivity = new LogInActivity();
        reference.child(logInActivity.getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String temp;
                try {
                    temp = Objects.requireNonNull(dataSnapshot.getValue()).toString();
                } catch (Exception e) {
                    temp = "1";
                }


                if (temp != "1") {

                    SharedPreferences.Editor editor1 = sharedPreferences.edit();
                    editor1.putString(logInActivity.getIS_FIRST_TIME(), "1");
                    editor1.apply();
                    turn = true;

                } else {
                    turn = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println(error);

            }
        });
        return turn;
    }
}
