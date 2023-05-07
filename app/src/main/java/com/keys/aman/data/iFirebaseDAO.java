package com.keys.aman.data;

import android.content.Context;

import java.util.Objects;

public interface iFirebaseDAO {
    String getUID();
    String getDisplayName();
    void checkUser(final Firebase.iUserCheckCallback userCheckCallback);
    void saveUserData(final Firebase.iCreateAccountCallBack createAccountCallBack);
    void loadUserData();

    void loadPasswordsData(final Firebase.iLoadPasswordDataCallback loadPasswordDataCallback);
    void loadChatMessages(String senderRoom, final Firebase.iLoadChatMessagesCallback loadChatMessagesCallback);
    void saveSinglePassword(String comingDate, String encryptedAddLogin, String encryptedAddPassword, String addWebsiteName, String comingLoginWebsiteLink, Firebase.iPasswordSaveCallBack passwordSaveCallBack);
    void getWebsiteListData(final Firebase.iWebsiteListCallback iWebsiteListCallback);
    void saveSingleNote(String date, String titleEncrypted, String noteEncrypted, boolean isHideNote, final Firebase.iNoteSaveCallBack noteSaveCallBack);
    void deleteSinglePassword(String passwordDate, String websiteName, final Firebase.iPasswordDeleteCallback iPasswordDeleteCallback);
    void deleteSingleNote(String noteDate, final Firebase.iNoteDeleteCallback iNoteDeleteCallback);

}
