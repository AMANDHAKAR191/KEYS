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
import com.keys.aman.MyPreference;
import com.keys.aman.home.PasswordGeneratorActivity;
import com.keys.aman.home.addpassword.PasswordHelperClass;
import com.keys.aman.messages.ChatModelClass;
import com.keys.aman.messages.UserListModelClass;
import com.keys.aman.notes.addnote.NoteHelperClass;
import com.keys.aman.signin_login.UserHelperClass;

import java.util.ArrayList;
import java.util.Objects;

public class Firebase {
    static DatabaseReference databaseReference;
    static FirebaseAuth firebaseAuth;
    static MyPreference myPreference;
    private static Firebase sInstance;
    Context context;
    ArrayList<PasswordHelperClass> dataHolder;
    private AES aes;
    private ArrayList<ChatModelClass> dataHolderChatMessages;


    public Firebase(Context context) {
        this.context = context;
    }

    public static Firebase getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new Firebase(context);
            firebaseAuth = FirebaseAuth.getInstance();
            myPreference = MyPreference.getInstance(context);
        }
        return sInstance;
    }

    public String getUID() {
        return Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
    }

    public void loadPasswordsData(final FirebaseLoadPasswordDataCallback loadPasswordDataCallback) {
        System.out.println("UID: " + getUID());
        dataHolder = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("PasswordsData").child(getUID());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                System.out.println("dataSnapshot: " + dataSnapshot);
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        for (DataSnapshot ds1 : ds.getChildren()) {
                            PasswordHelperClass data = ds1.getValue(PasswordHelperClass.class);
                            dataHolder.add(data);
                        }
                    }
                    System.out.println("dataHolderPassword: " + dataHolder);
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

    public void saveSinglePassword(String comingDate, String encryptedAddLogin, String encryptedAddPassword, String addWebsiteName, String comingLoginWebsiteLink, onPasswordSaveCallBack passwordSaveCallBack) {
        databaseReference = FirebaseDatabase.getInstance().getReference("PasswordsData").child(getUID()).child(addWebsiteName);
        PasswordHelperClass passwordHelperClass = new PasswordHelperClass(comingDate, encryptedAddLogin, encryptedAddPassword, addWebsiteName, comingLoginWebsiteLink);
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

    public void saveSingleNote(String date, String titleEncrypted, String noteEncrypted, boolean isHideNote, final onNoteSaveCallBack noteSaveCallBack) {
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

    public void checkUser(final FirebaseUserCheckCallback userCheckCallback) {
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

    public void saveUserData(final FirebaseCreateAccountCallBack createAccountCallBack) {
        String publicUname, email, private_uid, publicUid;
        publicUname = firebaseAuth.getCurrentUser().getDisplayName();
        email = firebaseAuth.getCurrentUser().getEmail();
        private_uid = getUID();
        publicUid = createPublicUid(email);

        myPreference.setAesKey(PasswordGeneratorActivity.generateRandomPassword(22, true, true, true, false) + "==");
        myPreference.setAesIv(PasswordGeneratorActivity.generateRandomPassword(16, true, true, true, false));
        myPreference.setPublicUid(publicUid);
        myPreference.setUserLoggedIn(true);
        myPreference.setNewUser(false);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("UserData");

        String aesKey = myPreference.getAesKey();
        String aesIv = myPreference.getAesIv();

        aes = AES.getInstance(aesKey, aesIv);
        String encryptedName, encryptedEmail;
        try {
            encryptedName = aes.singleEncryption(publicUname);
            encryptedEmail = aes.singleEncryption(email);
            UserHelperClass userHelperClass = new UserHelperClass(encryptedName, encryptedEmail, aesKey, aesIv, private_uid, publicUid);


            databaseReference.child(private_uid).setValue(userHelperClass)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            createAccountCallBack.onAccountCreatedSuccessfullyOnFirebase();
                            UserListModelClass userListModel = new UserListModelClass(publicUid, publicUname, false);
                            DatabaseReference referenceSender = FirebaseDatabase.getInstance().getReference();
                            referenceSender.child("messageUserList").child(publicUid)
                                    .setValue(userListModel)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                        }
                                    });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            createAccountCallBack.onAccountCreationFailed(e.getMessage());
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                // Failed to read value
                System.out.println("Not able get data from database");
            }
        });
    }

    public void loadChatMessages(String senderRoom, final FirebaseLoadChatMessagesCallback loadChatMessagesCallback){
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

    public interface FirebaseUserCheckCallback {
        void onUserExist();

        void onUserNotExist();
    }

    public interface FirebaseCreateAccountCallBack {
        void onAccountCreatedSuccessfullyOnFirebase();

        void onAccountCreationFailed(String message);

    }

    public interface FirebaseLoadPasswordDataCallback {
        void onPasswordDataReceivedCallback(ArrayList<PasswordHelperClass> dataHolderPassword);
    }

    public interface FirebaseLoadChatMessagesCallback{
        void onChatMessagesLoaded(ArrayList<ChatModelClass> dataHolderChatMessages);
    }

    public interface onPasswordSaveCallBack {
        void onPasswordSaved();

        void onFailed(String message);
    }

    public interface onNoteSaveCallBack {
        void onNoteSaved();

        void onFailed(String message);
    }

}
