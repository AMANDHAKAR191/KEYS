package com.keys.aman.data;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.keys.aman.AES;
import com.keys.aman.home.PasswordGeneratorActivity;
import com.keys.aman.home.addpassword.PasswordHelperClass;
import com.keys.aman.home.addpassword.WebsiteHelperClass;
import com.keys.aman.iAES;
import com.keys.aman.messages.ChatModelClass;
import com.keys.aman.messages.UserListModelClass;
import com.keys.aman.notes.addnote.NoteHelperClass;
import com.keys.aman.signin_login.UserHelperClass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class Firebase implements iFirebaseDAO{
    static DatabaseReference databaseReference;
    static FirebaseAuth firebaseAuth;
    static MyPreference myPreference;
    private static Firebase sInstance;
    Context context;
    ArrayList<PasswordHelperClass> dataHolder;
    private iAES iAES;
    private ArrayList<ChatModelClass> dataHolderChatMessages;


    public Firebase(Context context) {
        this.context = context;
    }

    public static Firebase getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new Firebase(context);
        }
        firebaseAuth = FirebaseAuth.getInstance();
        myPreference = MyPreference.getInstance(context);
        return sInstance;
    }

    public String getUID() {
        return Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
    }
    public String getDisplayName(){
        return Objects.requireNonNull(firebaseAuth.getCurrentUser()).getDisplayName();
    }

    public void loadPasswordsData(final iLoadPasswordDataCallback loadPasswordDataCallback) {
        dataHolder = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("PasswordsData").child(getUID());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        for (DataSnapshot ds1 : ds.getChildren()) {
                            PasswordHelperClass data = ds1.getValue(PasswordHelperClass.class);
                            dataHolder.add(data);
                        }
                    }
                    loadPasswordDataCallback.onPasswordDataReceivedCallback(dataHolder);
                } else {
                    loadPasswordDataCallback.onPasswordDataReceivedCallback(dataHolder);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("error: " + error);

            }
        });
    }

    public void saveSinglePassword(String comingDate, String userName, String password, String websiteName, String websiteLink, iPasswordSaveCallBack passwordSaveCallBack) {
        iAES = AES.getInstance(myPreference.getAesKey(), myPreference.getAesIv());
        String encryptedUserName = iAES.doubleEncryption(userName);
        String encryptedPassword = iAES.doubleEncryption(password);
        databaseReference = FirebaseDatabase.getInstance().getReference("PasswordsData").child(getUID()).child(websiteName);
        PasswordHelperClass passwordHelperClass = new PasswordHelperClass(comingDate, encryptedUserName, encryptedPassword, websiteName, websiteLink);
        databaseReference.child(comingDate).setValue(passwordHelperClass)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        passwordSaveCallBack.onPasswordSaved();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        passwordSaveCallBack.onFailed(e.getMessage());
                    }
                });
    }

    public void deleteSinglePassword(String passwordDate, String websiteName, final iPasswordDeleteCallback iPasswordDeleteCallback){
        databaseReference = FirebaseDatabase.getInstance().getReference("PasswordData").child(getUID());
        databaseReference.child(websiteName).child(passwordDate).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        iPasswordDeleteCallback.onPasswordDeleted();
                    }
                });
    }

    public void getWebsiteListData(final iWebsiteListCallback iWebsiteListCallback) {
        ArrayList<WebsiteHelperClass> dataholder = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("website_list");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        WebsiteHelperClass data = ds.getValue(WebsiteHelperClass.class);
                        dataholder.add(data);
                    }
                    Collections.sort(dataholder, WebsiteHelperClass.addDataHelperClassComparator);
                    iWebsiteListCallback.onWebsiteListLoaded(dataholder);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("check error:" + error.getMessage());
            }
        });
    }

    public void saveSingleNote(String date, String titleEncrypted, String noteEncrypted, boolean isHideNote, final iNoteSaveCallBack noteSaveCallBack) {
        databaseReference = FirebaseDatabase.getInstance().getReference("NotesData").child(getUID());
        NoteHelperClass addDNoteHelper;
        addDNoteHelper = new NoteHelperClass(date, titleEncrypted, noteEncrypted, isHideNote, false);
        databaseReference.child(date).setValue(addDNoteHelper)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        noteSaveCallBack.onNoteSaved();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        noteSaveCallBack.onFailed(e.getMessage());
                    }
                });
    }

    public void deleteSingleNote(String noteDate, final iNoteDeleteCallback iNoteDeleteCallback){
        databaseReference = FirebaseDatabase.getInstance().getReference("NotesData").child(getUID());
        databaseReference.child(noteDate).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        iNoteDeleteCallback.onNoteDeleted();
                    }
                });
    }
    public void checkUser(final iUserCheckCallback userCheckCallback) {
        databaseReference = FirebaseDatabase.getInstance().getReference("UserData");
        databaseReference.child(getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    myPreference.setNewUser(true);
                    userCheckCallback.onUserExist();

                } else {
                    myPreference.setNewUser(false);
                    userCheckCallback.onUserNotExist();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.err.println(error);
            }
        });
    }

    public void saveUserData(final iCreateAccountCallBack createAccountCallBack) {
        String publicUname, email, private_uid, publicUid;
        publicUname = firebaseAuth.getCurrentUser().getDisplayName();
        email = firebaseAuth.getCurrentUser().getEmail();
        private_uid = getUID();
        publicUid = createPublicUid(email);

        //save the data in myPreference
        myPreference.setAesKey(PasswordGeneratorActivity.generateRandomPassword(22, true, true, true, false) + "==");
        myPreference.setAesIv(PasswordGeneratorActivity.generateRandomPassword(16, true, true, true, false));
        myPreference.setPublicUid(publicUid);
        myPreference.setUserLoggedIn(true);
        myPreference.setNewUser(false);

        databaseReference = FirebaseDatabase.getInstance().getReference("UserData");

        String aesKey = myPreference.getAesKey();
        String aesIv = myPreference.getAesIv();
        iAES = AES.getInstance(aesKey, aesIv);

        String encryptedName, encryptedEmail;
        encryptedName = iAES.doubleEncryption(publicUname);
        encryptedEmail = iAES.doubleEncryption(email);

        UserHelperClass userHelperClass = new UserHelperClass(encryptedName, encryptedEmail, aesKey, aesIv, private_uid, publicUid);
        //save user data on database
        databaseReference.child(private_uid).setValue(userHelperClass)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //create a personal user chat list
                        UserListModelClass userListModel = new UserListModelClass(publicUid, publicUname, false);
                        DatabaseReference referenceSender = FirebaseDatabase.getInstance().getReference();
                        referenceSender.child("messageUserList").child(publicUid).setValue(userListModel)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        createAccountCallBack.onAccountCreatedSuccessfullyOnFirebase();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        createAccountCallBack.onAccountCreationFailed(e.getMessage());
                                    }
                                });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        createAccountCallBack.onAccountCreationFailed(e.getMessage());
                    }
                });
    }

    private String createPublicUid(String email) {
        String publicUid;
        String[] tempEmail = email.split("@", 2);

        tempEmail[0] = tempEmail[0].replace(".", "_");
        publicUid = tempEmail[0].replace("+", "_");
        System.out.println(publicUid);
        return publicUid;
    }

    public void loadUserData() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("UserData").child(getUID());
        // Read from the database
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String aes_iv = dataSnapshot.child("aesIv").getValue(String.class);
                String aes_key = dataSnapshot.child("aesKey").getValue(String.class);
                String public_uid = dataSnapshot.child("publicUid").getValue(String.class);

                myPreference.setAesKey(aes_key);
                myPreference.setAesIv(aes_iv);
                myPreference.setPublicUid(public_uid);
                myPreference.setUserLoggedIn(true);
                myPreference.setNewUser(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //TODO: update the code here
                // handle the error
                // Failed to read value
                System.out.println("Not able get data from database");
            }
        });
    }

    public void loadChatMessages(String senderRoom, final iLoadChatMessagesCallback loadChatMessagesCallback) {
        dataHolderChatMessages = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("messages").child(senderRoom)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        dataHolderChatMessages.clear();
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            System.out.println(ds);
                            ChatModelClass chatModel = ds.getValue(ChatModelClass.class);
                            if (chatModel.getType().equals("note")) {
                                System.out.println(chatModel.getNoteModelClass().getTitle());
                                System.out.println(chatModel.getNoteModelClass().getNote());
                            }
                            dataHolderChatMessages.add(chatModel);
                        }
                        loadChatMessagesCallback.onChatMessagesLoaded(dataHolderChatMessages);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    public interface iUserCheckCallback {
        void onUserExist();

        void onUserNotExist();
    }

    public interface iCreateAccountCallBack {
        void onAccountCreatedSuccessfullyOnFirebase();

        void onAccountCreationFailed(String message);

    }

    public interface iLoadPasswordDataCallback {
        void onPasswordDataReceivedCallback(ArrayList<PasswordHelperClass> dataHolderPassword);
    }

    public interface iLoadChatMessagesCallback {
        void onChatMessagesLoaded(ArrayList<ChatModelClass> dataHolderChatMessages);
    }

    public interface iPasswordSaveCallBack {
        void onPasswordSaved();

        void onFailed(String message);
    }

    public interface iNoteSaveCallBack {
        void onNoteSaved();

        void onFailed(String message);
    }

    public interface iPasswordDeleteCallback {
        void onPasswordDeleted();
    }
    public interface iNoteDeleteCallback{
        void onNoteDeleted();
    }

    public interface iWebsiteListCallback {
        void onWebsiteListLoaded(ArrayList<WebsiteHelperClass> dataHolderWebsiteList);
    }

}
