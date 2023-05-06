package com.keys.aman.data;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.keys.aman.notes.addnote.NoteHelperClass;

public interface IFirebaseDAO {
    String getUID();
    void checkUser(final Firebase.FirebaseUserCheckCallback userCheckCallback);
    void saveUserData(final Firebase.FirebaseCreateAccountCallBack createAccountCallBack);
    void loadUserData();

    void loadPasswordsData(final Firebase.FirebaseLoadPasswordDataCallback loadPasswordDataCallback);
    void loadChatMessages(String senderRoom, final Firebase.FirebaseLoadChatMessagesCallback loadChatMessagesCallback);
    void saveSinglePassword(String comingDate, String encryptedAddLogin, String encryptedAddPassword, String addWebsiteName, String comingLoginWebsiteLink, Firebase.onPasswordSaveCallBack passwordSaveCallBack);
    void saveSingleNote(String date, String titleEncrypted, String noteEncrypted, boolean isHideNote, final Firebase.onNoteSaveCallBack noteSaveCallBack);


}
