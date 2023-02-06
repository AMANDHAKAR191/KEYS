package com.example.keys.aman.signin_login;

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

import com.example.keys.R;
import com.example.keys.aman.AES;
import com.example.keys.aman.PrograceBar;
import com.example.keys.aman.SplashActivity;
import com.example.keys.aman.authentication.BiometricAuthActivity;
import com.example.keys.aman.authentication.PinLockActivity;
import com.example.keys.aman.home.PasswordGeneratorActivity;
import com.example.keys.aman.messages.UserListModelClass;
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

import java.util.Objects;

/* TODO Checked on 12/10/2022
    All unimportant variable methods is removed
    Not Working
*/
public class LogInActivity extends AppCompatActivity {

    private static final String TAG = "LogInActivity";
    //objects
    Button btnLogin, btnCreateAccount;
    TextView tvErrorMessage;
    private PrograceBar prograceBar;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    SharedPreferences sharedPreferences;
    public static DatabaseReference myRef;
    ActivityResultLauncher<Intent> getResultLogin;
    ActivityResultLauncher<Intent> getResultSignIn;
    Handler progressBarHandler = new Handler();


    //Global variables
    private final String AES_KEY = "aes_key";
    private final String AES_IV = "aes_iv";
    public final String PUBLIC_UID = "public_uid";
    public final String SHARED_PREF_ALL_DATA = "All data";
    public final String IS_LOGIN = "islogin";
    public final String IS_FIRST_TIME = "0";
    public final String REQUEST_CODE_NAME = "request_code";
    public static final String REQUEST_ID = "LogInActivity";


    String UID;
    private boolean turn = false;
    public GoogleSignInOptions gso;


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
        sharedPreferences = getSharedPreferences(SHARED_PREF_ALL_DATA, MODE_PRIVATE);
        SplashActivity.isForeground = false;

        //Hooks
        btnLogin = findViewById(R.id.btn_login);
        btnCreateAccount = findViewById(R.id.btn_create_account);
        tvErrorMessage = findViewById(R.id.tv_error_message);

//        Check if User is already login then go direct to HomeScreen
        Boolean isLogin = sharedPreferences.getBoolean(IS_LOGIN, false);
        System.out.println(isLogin);
        if (isLogin) {
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
        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createRequest();
                SplashActivity.isForeground = true;
                progressBar();
                Intent loginIntent = mGoogleSignInClient.getSignInIntent();
                getResultSignIn.launch(loginIntent);
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
                            firebaseAuthWithGoogleLogin(account);
                        } catch (ApiException e) {
                            // Google Sign In failed, update UI appropriately
                            // ...
                            Toast.makeText(LogInActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                }
        );
        getResultSignIn = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                        prograceBar.dismissbar();
                        try {
                            // Google Sign In was successful, authenticate with Firebase
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            firebaseAuthWithGoogleCreateAccount(account);
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
    protected void onStart() {
        super.onStart();
        if (SplashActivity.isBackground) {
            Intent intent = new Intent(LogInActivity.this, BiometricAuthActivity.class);
            intent.putExtra(REQUEST_CODE_NAME, "LockBackGroundApp");
            startActivity(intent);
        }
        if (SplashActivity.isForeground) {
            SplashActivity.isForeground = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!SplashActivity.isForeground) {
            SplashActivity.isBackground = true;
        }
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


    private void firebaseAuthWithGoogleCreateAccount(GoogleSignInAccount acct) {

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
//                            MyThreadRunnable MyThreadRunnable = new MyThreadRunnable();
//                            new Thread(MyThreadRunnable).start();

                            Handler handler = new Handler();
                            Thread loginThread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    handler.post(new Runnable() {
                                        private int count = 0;
                                        @Override
                                        public void run() {
                                            String temp = sharedPreferences.getString(IS_FIRST_TIME, "0");
                                                createEntryOnDatabase(user);
                                                turn = false;
                                                Toast.makeText(LogInActivity.this, "Registration Completed!!", Toast.LENGTH_SHORT).show();
                                                SplashActivity.isForeground = true;
                                                Intent intent = new Intent(getApplicationContext(), PinLockActivity.class);
                                                intent.putExtra(REQUEST_CODE_NAME, "setpin");
                                                intent.putExtra("title", "Set 6 digit Pin");
                                                startActivity(intent);
                                        }
                                    });
                                }
                            });
                            loginThread.start();

                        } else {
                            Toast.makeText(LogInActivity.this, "Sorry authentication failed.", Toast.LENGTH_SHORT).show();


                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        tvErrorMessage.setText(e.getMessage());
                    }
                });
    }

    private void firebaseAuthWithGoogleLogin(GoogleSignInAccount acct) {

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
//                            MyThreadRunnable MyThreadRunnable = new MyThreadRunnable();
//                            new Thread(MyThreadRunnable).start();

                            Handler handler = new Handler();
                            Thread loginThread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    handler.post(new Runnable() {
                                        private int count = 0;

                                        @Override
                                        public void run() {
                                            prograceBar.dismissbar();
                                            String temp = sharedPreferences.getString(IS_FIRST_TIME, "0");
//                                            if (temp == "0") {
//
//                                                createEntryOnDatabase(user);
//                                                turn = false;
//                                                Toast.makeText(LogInActivity.this, "Registration Completed!!", Toast.LENGTH_SHORT).show();
//                                                SplashActivity.isForeground = true;
//                                                Intent intent = new Intent(getApplicationContext(), PinLockActivity.class);
//                                                intent.putExtra(REQUEST_CODE_NAME, "setpin");
//                                                intent.putExtra("title", "Set Pin");
//                                                startActivity(intent);
//                                            } else
//                                            {
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

                                                    SharedPreferences.Editor editor1 = sharedPreferences.edit();
                                                    editor1.putString(AES_KEY, aes_key);
                                                    editor1.putString(AES_IV, aes_iv);
                                                    editor1.putString(PUBLIC_UID, public_uid);
                                                    editor1.putBoolean(IS_LOGIN, true);
                                                    editor1.putString(IS_FIRST_TIME, "1");
                                                    editor1.apply();
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
                                            intent.putExtra("title", "Set 6 digit Pin");
                                            startActivity(intent);
//                                            }
                                        }
                                    });
                                }
                            });
                            loginThread.start();

                        } else {
                            Toast.makeText(LogInActivity.this, "Sorry authentication failed.", Toast.LENGTH_SHORT).show();


                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        tvErrorMessage.setText(e.getMessage());
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
                    SharedPreferences.Editor editor1 = sharedPreferences.edit();
                    editor1.putString(IS_FIRST_TIME, "1");
                    editor1.apply();

                }
                turn = true;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                tvErrorMessage.setText(error.getMessage());
            }
        });
        return turn;
    }

    private void createEntryOnDatabase(FirebaseUser user) {
        String publicUname, email, private_uid, publicUid;
        publicUname = user.getDisplayName();
        email = user.getEmail();
        private_uid = user.getUid();
//        publicUid = PasswordGeneratorActivity.generateRandomPassword(22, true, true, true, false) + "==";
        String[] tempEmail = email.split("@",2);

        tempEmail[0] = tempEmail[0].replace(".", "_");
        publicUid = tempEmail[0].replace("+", "_");
        System.out.println(publicUid);

        SharedPreferences.Editor editor1 = sharedPreferences.edit();
        editor1.putString(AES_KEY, PasswordGeneratorActivity.generateRandomPassword(22, true, true, true, false) + "==");
        editor1.putString(AES_IV, PasswordGeneratorActivity.generateRandomPassword(16, true, true, true, false));
        editor1.putBoolean(IS_LOGIN, true);
        editor1.putString(PUBLIC_UID, publicUid);
        editor1.putString(IS_FIRST_TIME, "1");
        editor1.apply();

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference("signupdata");

        AES aes = new AES();
        String encryptionKey = sharedPreferences.getString(AES_KEY, null);
        String encryptionIv = sharedPreferences.getString(AES_IV, null);
        aes.initFromStrings(encryptionKey, encryptionIv);
        String encryptedName, encryptedEmail;
        try {
            Toast.makeText(LogInActivity.this, "Creating Data  Entry on DB", Toast.LENGTH_SHORT).show();
            encryptedName = aes.encrypt(publicUname);
            encryptedEmail = aes.encrypt(email);
            UserHelperClass userHelperClass = new UserHelperClass(encryptedName, encryptedEmail, encryptionKey, encryptionIv, private_uid, publicUid);


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

    public void progressBar() {
        prograceBar = new PrograceBar(LogInActivity.this);
        prograceBar.showDialog();
    }


    // getter and setter methods for Global Variable
    public String getAES_KEY() {
        return AES_KEY;
    }

    public String getAES_IV() {
        return AES_IV;
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