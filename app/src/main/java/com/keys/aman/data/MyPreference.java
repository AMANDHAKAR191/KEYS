package com.keys.aman.data;

import android.content.Context;
import android.content.SharedPreferences;

public class MyPreference {
    private static MyPreference sInstance;
    Context context;
    SharedPreferences sharedPreferences;
    public final String SHARED_PREF_KEYS = "keysSharedPreference";
    private final String AES_KEY = "aesKey";
    private final String AES_IV = "aesIv";
    public final String PUBLIC_UID = "publicUid";
    public final String MASTER_PIN = "masterPin";
    public final String LOCK_APP_OPTIONS = "lock_app";
    public final String RECEIVER_ROOM_ID = "receiverRoomId";
    public final String RECEIVER_PUBLIC_UID = "receiverPublicId";
    public final String SENDER_ROOM_ID = "senderRoomId";
    public final String LOGGED_IN = "loggedIn";
    public final String NEW_USER = "newUser";
    public final String PIN_SET = "pinSet";
    public final String USER_RESTRICTED = "userRestricted";
    private final String USER_AUTHENTICATED = "userAuthenticated";


    public MyPreference(Context context) {
        sharedPreferences = context.getApplicationContext().getSharedPreferences(SHARED_PREF_KEYS,Context.MODE_PRIVATE);
    }

    public static MyPreference getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new MyPreference(context);
        }
        return sInstance;
    }
    //String getter/setter method
    public void setAesKey(String aesKey){
        sharedPreferences.edit().putString(AES_KEY,aesKey).apply();
    }
    public String getAesKey(){
        return sharedPreferences.getString(AES_KEY,"");
    }
    public void setAesIv(String aesIv){
        sharedPreferences.edit().putString(AES_IV,aesIv).apply();
    }
    public String getAesIv(){
        return sharedPreferences.getString(AES_IV,"");
    }
    public void setPublicUid(String publicUid){
        sharedPreferences.edit().putString(PUBLIC_UID,publicUid).apply();
    }
    public String getPublicUid(){
        return sharedPreferences.getString(PUBLIC_UID,"");
    }

    public void setMasterPin(String masterPin){
        sharedPreferences.edit().putString(MASTER_PIN,masterPin).apply();
    }
    public String getMasterPin(){
        return sharedPreferences.getString(MASTER_PIN,"");
    }

    public void setReceiverRoomId(String receiverRoomId){
        sharedPreferences.edit().putString(RECEIVER_ROOM_ID,receiverRoomId).apply();
    }
    public String getReceiverRoomId(){
        return sharedPreferences.getString(RECEIVER_ROOM_ID,"");
    }

    public void setReceiverPublicId(String receiverRoomId){
        sharedPreferences.edit().putString(RECEIVER_PUBLIC_UID,receiverRoomId).apply();
    }
    public String getReceiverPublicId(){
        return sharedPreferences.getString(RECEIVER_PUBLIC_UID,"");
    }

    public void setSenderRoomId(String senderRoomId){
        sharedPreferences.edit().putString(SENDER_ROOM_ID,senderRoomId).apply();
    }
    public String getSenderRoomId(){
        return sharedPreferences.getString(SENDER_ROOM_ID,"");
    }

    //boolean getter/setter method
    public void setUserLoggedIn(boolean isUserLoggedIn){
        sharedPreferences.edit().putBoolean(LOGGED_IN,isUserLoggedIn).apply();
    }
    public boolean isUserLoggedIn(){
        return sharedPreferences.getBoolean(LOGGED_IN,false);
    }

    public void setNewUser(boolean isNewUser){
        sharedPreferences.edit().putBoolean(NEW_USER,isNewUser).apply();
    }
    public boolean isNewUser(){
        return sharedPreferences.getBoolean(NEW_USER,true);
    }

    public void setPinCompleted(boolean isPinCompleted){
        sharedPreferences.edit().putBoolean(PIN_SET,isPinCompleted).apply();
    }
    public boolean isPinCompleted(){
        return sharedPreferences.getBoolean(PIN_SET,false);
    }

    public void setUserRestricted(boolean isUserRestricted){
        sharedPreferences.edit().putBoolean(USER_RESTRICTED,isUserRestricted).apply();
    }
    public boolean isUserRestricted(){
        return sharedPreferences.getBoolean(USER_RESTRICTED,false);
    }

    public void setUserAuthenticated(boolean isUserRestricted){
        sharedPreferences.edit().putBoolean(USER_AUTHENTICATED,isUserRestricted).apply();
    }
    public boolean isUserAuthenticated(){
        return sharedPreferences.getBoolean(USER_AUTHENTICATED,false);
    }


    //integer getter/setter method
    public void setLockAppSelectedOption(int selectedOption){
        sharedPreferences.edit().putInt(LOCK_APP_OPTIONS,selectedOption).apply();
    }
    public int getLockAppSelectedOption(){
        return sharedPreferences.getInt(LOCK_APP_OPTIONS,0);
    }


}
