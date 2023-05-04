package com.keys.aman.signin_login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.security.ProviderInstaller;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.keys.aman.AES;
import com.keys.aman.MyPreference;
import com.keys.aman.PrograceBar;
import com.keys.aman.R;
import com.keys.aman.SplashActivity;
import com.keys.aman.authentication.BiometricAuthActivity;
import com.keys.aman.authentication.PinLockActivity;
import com.keys.aman.home.PasswordGeneratorActivity;
import com.keys.aman.messages.UserListModelClass;

import java.util.Objects;

/* TODO Checked on 12/10/2022
    All unimportant variable methods is removed
    Not Working
*/
public class LogInActivity extends AppCompatActivity {

    private static final String TAG = "LogInActivity";
    //objects
    Button btnLogin;
    TextView tvErrorMessage;
    private PrograceBar prograceBar;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    MyPreference myPreference;
    public static DatabaseReference myRef;
    ActivityResultLauncher<Intent> getResultLogin;
    ActivityResultLauncher<Intent> getResultSignIn;
    Handler progressBarHandler = new Handler();

    public final String REQUEST_CODE_NAME = "request_code";
    public static final String REQUEST_ID = "LogInActivity";


    String UID;
    private boolean turn = false;
    public GoogleSignInOptions gso;
    private AES aes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_log_in);
        try {
            ProviderInstaller.installIfNeeded(getApplicationContext());
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
        //initialize local database
        myPreference = MyPreference.getInstance(this);

        SplashActivity.isForeground = false;

        //Hooks
        btnLogin = findViewById(R.id.btn_login);
        tvErrorMessage = findViewById(R.id.tv_error_message);

//        Check if User is already login then go direct to HomeScreen
        if (myPreference.isUserLoggedIn()) {
            SplashActivity.isForeground = true;
            Intent intent = new Intent(LogInActivity.this, BiometricAuthActivity.class);
            intent.putExtra(REQUEST_CODE_NAME, REQUEST_ID);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent, savedInstanceState);
            finish();
        }


        mAuth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createRequest();
                SplashActivity.isForeground = true;
                progressBar();
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                getResultLogin.launch(signInIntent);
            }
        });

        getResultLogin = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                        prograceBar.dismissbar();
                        try {
                            // Google Sign In was successful, authenticate with Firebase
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            firebaseAuthWithGoogle(account);
                        } catch (ApiException e) {
                            // Google Sign In failed, update UI appropriately
                            // ...
                            Toast.makeText(LogInActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                }
        );
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SplashActivity.isForeground = true;
    }

    private void createRequest() {
        // Configure Google Sign In
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressBar();
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            UID = user.getUid();
                            //check user if already signed up in background
                            MyThreadRunnable MyThreadRunnable = new MyThreadRunnable();
                            new Thread(MyThreadRunnable).start();

                            Handler handler = new Handler();
                            Thread loginThread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    handler.postDelayed(new Runnable() {
                                        private int count = 0;

                                        @Override
                                        public void run() {
                                            while (!turn) {
                                                count++;
                                                progressBar();
                                            }
                                            prograceBar.dismissbar();
                                            boolean temp = myPreference.isNewUser();
                                            if (temp) { //new User
                                                createEntryOnDatabase(user);
                                                turn = false;
                                                Toast.makeText(LogInActivity.this, "Registration Completed!!", Toast.LENGTH_SHORT).show();
                                                SplashActivity.isForeground = true;
                                                Intent intent = new Intent(getApplicationContext(), PinLockActivity.class);
                                                intent.putExtra(REQUEST_CODE_NAME, REQUEST_ID);
                                                intent.putExtra("title", "Set Pin");
                                                startActivity(intent);
                                            } else {
                                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("signupdata").child(UID);
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

                                                        turn = false;
                                                        Toast.makeText(LogInActivity.this, "SignIn Completed!!", Toast.LENGTH_SHORT).show();
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                        // Failed to read value
                                                        System.out.println("Not able get data from database");
                                                    }
                                                });
                                                SplashActivity.isForeground = true;
                                                Intent intent = new Intent(getApplicationContext(), PinLockActivity.class);
                                                intent.putExtra(REQUEST_CODE_NAME, REQUEST_ID);
                                                intent.putExtra("title", "Set Pin");
                                                startActivity(intent);
                                                finish();
                                            }
                                        }
                                    }, 10000);
                                }
                            });
                            loginThread.start();

                        } else {
                            Toast.makeText(LogInActivity.this, "Sorry authentication failed.", Toast.LENGTH_SHORT).show();


                        }
                    }
                });
    }


    public boolean checkUser(DatabaseReference reference) {
        Log.d(TAG, "UID: " + UID);
        reference.child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(TAG, "Checking User... 1");
                String temp;
                try {
                    Log.d(TAG, "Checking User... 2");
                    temp = Objects.requireNonNull(dataSnapshot.getValue()).toString();
                } catch (Exception e) {
                    Log.d(TAG, "Checking User.. 3");
                    temp = "1";
                }


                if (temp != "1") {
                    myPreference.setNewUser(false);
                }
                turn = true;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.err.println(error);

            }
        });
        return turn;
    }

    private void createEntryOnDatabase(FirebaseUser user) {
        String publicUname, email, private_uid, publicUid;
        publicUname = user.getDisplayName();
        email = user.getEmail();
        private_uid = user.getUid();
        publicUid = createPublicUid(email);

        myPreference.setAesKey(PasswordGeneratorActivity.generateRandomPassword(22, true, true, true, false) + "==");
        myPreference.setAesIv(PasswordGeneratorActivity.generateRandomPassword(16, true, true, true, false));
        myPreference.setPublicUid(publicUid);
        myPreference.setUserLoggedIn(true);
        myPreference.setNewUser(false);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference("signupdata");

        String aesKey = myPreference.getAesKey();
        String aesIv = myPreference.getAesIv();

        aes = AES.getInstance(aesKey,aesIv);
        String encryptedName, encryptedEmail;
        try {
            Toast.makeText(LogInActivity.this, "Creating Data  Entry on DB", Toast.LENGTH_SHORT).show();
            encryptedName = aes.singleEncryption(publicUname);
            encryptedEmail = aes.singleEncryption(email);
            UserHelperClass userHelperClass = new UserHelperClass(encryptedName, encryptedEmail, aesKey, aesIv, private_uid, publicUid);


            myRef.child(private_uid).setValue(userHelperClass)
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
                                            Toast.makeText(LogInActivity.this, "Chat User Added", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            tvErrorMessage.setText(e.getMessage());
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @NonNull
    private String createPublicUid(String email) {
        String publicUid;
        String[] tempEmail = email.split("@",2);

        tempEmail[0] = tempEmail[0].replace(".", "_");
        publicUid = tempEmail[0].replace("+", "_");
        System.out.println(publicUid);
        return publicUid;
    }


    public void progressBar() {
        prograceBar = new PrograceBar(LogInActivity.this);
        prograceBar.showDialog();
    }


    private class MyThreadRunnable implements Runnable {

        public MyThreadRunnable() {
        }

        @Override
        public void run() {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("signupdata");
            turn = checkUser(reference);
        }
    }
}