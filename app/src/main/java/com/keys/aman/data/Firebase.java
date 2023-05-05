package com.keys.aman.data;

import android.content.Context;
import android.widget.Toast;

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
import com.keys.aman.messages.UserListModelClass;
import com.keys.aman.signin_login.LogInActivity;
import com.keys.aman.signin_login.UserHelperClass;

import java.util.ArrayList;
import java.util.Objects;

public class Firebase {
    private static Firebase sInstance;
    static DatabaseReference databaseReference;
    static FirebaseAuth firebaseAuth;
    Context context;
    ArrayList<PasswordHelperClass> dataHolderPassword;
    static MyPreference myPreference;
    private AES aes;


    public Firebase(Context context) {
        this.context = context;
    }

    public static Firebase getInstance(Context context){
        if (sInstance == null){
            sInstance = new Firebase(context);
            firebaseAuth = FirebaseAuth.getInstance();
            myPreference = MyPreference.getInstance(context);
        }
        return sInstance;
    }

    public String getUID(){
        return Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
    }

    public void loadPasswordsData(final FirebaseCallBack callBack){
        System.out.println("UID: " + getUID());
        dataHolderPassword = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("addpassworddata").child(getUID());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                System.out.println("dataSnapshot: " + dataSnapshot);
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        for (DataSnapshot ds1 : ds.getChildren()) {
                            PasswordHelperClass data = ds1.getValue(PasswordHelperClass.class);
                            dataHolderPassword.add(data);
                        }
                    }
                    System.out.println("dataHolderPassword: " + dataHolderPassword);
                    callBack.onPasswordDataReceivedCallback(dataHolderPassword);
                } else {
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("error: " + error);

            }
        });
    }

    public void checkUser(final FirebaseCallBack callBack) {
        databaseReference = FirebaseDatabase.getInstance().getReference("signupdata");
        databaseReference.child(getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    myPreference.setNewUser(true);
                    callBack.onUserExist();

                }else {
                    myPreference.setNewUser(false);
                    callBack.onUserNotExist();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.err.println(error);

            }
        });
    }

    public void saveUserData(){
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
        databaseReference = firebaseDatabase.getReference("signupdata");

        String aesKey = myPreference.getAesKey();
        String aesIv = myPreference.getAesIv();

        aes = AES.getInstance(aesKey,aesIv);
        String encryptedName, encryptedEmail;
        try {
            encryptedName = aes.singleEncryption(publicUname);
            encryptedEmail = aes.singleEncryption(email);
            UserHelperClass userHelperClass = new UserHelperClass(encryptedName, encryptedEmail, aesKey, aesIv, private_uid, publicUid);


            databaseReference.child(private_uid).setValue(userHelperClass)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
//                            UserPersonalChatList personalChatList = new UserPersonalChatList(publicUid, publicUname, false);
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
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private String createPublicUid(String email) {
        String publicUid;
        String[] tempEmail = email.split("@",2);

        tempEmail[0] = tempEmail[0].replace(".", "_");
        publicUid = tempEmail[0].replace("+", "_");
        System.out.println(publicUid);
        return publicUid;
    }
    public void loadUserData(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("signupdata").child(getUID());
        // Read from the database
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String aes_iv = dataSnapshot.child("aes_iv").getValue(String.class);
                String aes_key = dataSnapshot.child("aes_key").getValue(String.class);
                String public_uid = dataSnapshot.child("public_uid").getValue(String.class);

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
    public interface FirebaseCallBack {
        void onPasswordDataReceivedCallback(ArrayList<PasswordHelperClass> dataHolderPassword);
        void onUserExist();
        void onUserNotExist();
    }

}
