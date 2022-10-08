package com.example.keys.aman.signin_login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
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
import com.example.keys.aman.home.PasswordGeneratorActivity;
import com.example.keys.aman.notes.BiometricActivity;
import com.example.keys.aman.notes.PinLockActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
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


public class LogInActivity extends AppCompatActivity {

    //objects
    Button btnLogin;
    private PrograceBar prograceBar;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    SharedPreferences sharedPreferences;
    public static DatabaseReference myRef;
    ActivityResultLauncher<Intent> getResult;


    //variables
    private final String AES_KEY = "aes_key";
    private final String AES_IV = "aes_iv";
    private final String SHARED_PREF_ALL_DATA = "All data";
    private final String IS_LOGIN = "islogin";
    private final String IS_FIRST_TIME = "0";
    private final String REQUEST_CODE_NAME = "request_code";
    private final String IS_AUTHENTICATED = "isauthenticated";
    private final String MASTER_PIN = "master_pin";
    private final String IS_PIN_SET = "ispin_set";
    private final String IS_USER_RESTRICTED = "is_user_restricted";
    public final String LOCK_APP = "lock_app";

    protected String UID;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;
    private boolean turn = false;

    public void setUID(String UID) {
        this.UID = UID;
    }
    public String getUID() {
        return UID;
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_log_in);
        sharedPreferences = getSharedPreferences(SHARED_PREF_ALL_DATA, MODE_PRIVATE);
        SplashActivity.isForeground = false;

        //Hooks
        btnLogin = findViewById(R.id.btn_login);

//        Check if User is already login then go direct to HomeScreen
        Boolean islogin = sharedPreferences.getBoolean(IS_LOGIN, false);
        System.out.println(islogin);
        if (islogin) {
            SplashActivity.isForeground = true;
            Intent intent = new Intent(LogInActivity.this, BiometricActivity.class);
            intent.putExtra(REQUEST_CODE_NAME, "LogInActivity");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }


        mAuth = FirebaseAuth.getInstance();
        createRequest();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SplashActivity.isForeground = true;
                setName("AMAN");
                progressBar();
                signIn();
            }
        });

        getResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        Intent data = result.getData();
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        if (task.isSuccessful()) {
                            prograceBar.dismissbar();
                        } else {
                            prograceBar.dismissbar();
                        }
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
    protected void onStart() {
        super.onStart();
        if (SplashActivity.isBackground) {
            Intent intent = new Intent(LogInActivity.this, BiometricActivity.class);
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
        Toast.makeText(this, "tabLayoutActivity.isForeground: " + SplashActivity.isForeground, Toast.LENGTH_SHORT).show();
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
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        getResult.launch(signInIntent);
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
                            setUID(user.getUid());
//                            UID = user.getUid();
                            //check user if already signed up in background
                            threadRunnable threadRunnable = new threadRunnable();
                            new Thread(threadRunnable).start();

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
//                                    Toast.makeText(LogInActivity.this, "before while Loop...", Toast.LENGTH_SHORT).show();
                                    while (!turn) {
                                        Log.d("LoginActivity","Check_progressBar");
                                        progressBar();
                                    }
                                    prograceBar.dismissbar();
                                    String temp = sharedPreferences.getString(IS_FIRST_TIME, "0");
                                    if (temp == "0") {
                                        SharedPreferences.Editor editor1 = sharedPreferences.edit();
                                        editor1.putString(AES_KEY, PasswordGeneratorActivity.generateRandomPassword(22, true, true, true, false) + "==");
                                        editor1.putString(AES_IV, PasswordGeneratorActivity.generateRandomPassword(16, true, true, true, false));
                                        editor1.putBoolean(IS_LOGIN, true);
                                        editor1.putString(IS_FIRST_TIME, "1");
                                        editor1.apply();

                                        createEntryOnDatabase(user);
                                        turn = false;
                                        Toast.makeText(LogInActivity.this, "Registration Completed!!", Toast.LENGTH_SHORT).show();
                                        SplashActivity.isForeground = true;
                                        Intent intent = new Intent(getApplicationContext(), PinLockActivity.class);
                                        intent.putExtra(REQUEST_CODE_NAME, "setpin");
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

                                                SharedPreferences.Editor editor1 = sharedPreferences.edit();
                                                editor1.putString(AES_KEY, aes_key);
                                                editor1.putString(AES_IV, aes_iv);
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
                                        intent.putExtra(REQUEST_CODE_NAME, "setpin");
                                        intent.putExtra("title", "Set Pin");
                                        startActivity(intent);
                                    }
                                }
                            }, 10000);
                        } else {
                            Toast.makeText(LogInActivity.this, "Sorry authentication failed.", Toast.LENGTH_SHORT).show();


                        }
                    }
                });
    }

    private class threadRunnable implements Runnable {
        Handler handler = new Handler();
        View view;

        public threadRunnable() {
        }

        @Override
        public void run() {
            handler.post(new Runnable() {
                @Override
                public void run() {

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("signupdata");
                    turn = checkUser(reference);
                }
            });
        }
    }

    public boolean checkUser(DatabaseReference reference) {
        LogInActivity logInActivity = new LogInActivity();

        reference.child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
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

    private void createEntryOnDatabase(FirebaseUser user) {
        String name, email, uid;
        name = user.getDisplayName();
        email = user.getEmail();
        uid = user.getUid();

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference("signupdata");

        AES aes = new AES();
        String encryptionKey = sharedPreferences.getString(AES_KEY, null);
        String encryptionIv = sharedPreferences.getString(AES_IV, null);
        aes.initFromStrings(encryptionKey, encryptionIv);
        String encryptedName, encryptedEmail;
        try {
            Toast.makeText(LogInActivity.this, "Creating Data  Entry on DB", Toast.LENGTH_SHORT).show();
            encryptedName = aes.encrypt(name);
            encryptedEmail = aes.encrypt(email);
            com.example.keys.aman.app.signin_login.UserHelperClass userHelperClass = new com.example.keys.aman.app.signin_login.UserHelperClass(encryptedName, encryptedEmail, encryptionKey, encryptionIv, uid);


            myRef.child(uid).setValue(userHelperClass);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void progressBar() {
        prograceBar = new PrograceBar(LogInActivity.this);
        prograceBar.showDialog();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        }, 500);
    }


    // getter and setter methods for Global Variable
    public String getAES_KEY() {
        return AES_KEY;
    }

    public String getAES_IV() {
        return AES_IV;
    }

    public String getSHARED_PREF_ALL_DATA() {
        return SHARED_PREF_ALL_DATA;
    }

    public String getIS_LOGIN() {
        return IS_LOGIN;
    }

    public String getIS_FIRST_TIME() {
        return IS_FIRST_TIME;
    }

    public String getREQUEST_CODE_NAME() {
        return REQUEST_CODE_NAME;
    }

    public String getIS_AUTHENTICATED() {
        return IS_AUTHENTICATED;
    }

    public String getMASTER_PIN() {
        return MASTER_PIN;
    }

    public String getIS_PIN_SET() {
        return IS_PIN_SET;
    }

    public String getIS_USER_RESTRICTED() {
        return IS_USER_RESTRICTED;
    }
}