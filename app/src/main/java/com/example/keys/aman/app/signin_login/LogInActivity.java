package com.example.keys.aman.app.signin_login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import com.example.keys.aman.app.AES;
import com.example.keys.aman.app.PrograceBar;
import com.example.keys.aman.app.SplashActivity;
import com.example.keys.aman.app.home.PassGenActivity;
import com.example.keys.aman.app.notes.BiometricActivity;
import com.example.keys.aman.app.notes.pinLockFragment;
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

    Button btnLogin;
    private PrograceBar prograceBar;

    private GoogleSignInClient mGoogleSignInClient;
    private final static int RC_SIGN_IN = 123;
    private FirebaseAuth mAuth;
    private String uid;

    SharedPreferences sharedPreferences;
    public static final String AES_KEY = "aes_key";
    public static final String AES_IV = "aes_iv";
    public static final String SHARED_PREF_ALL_DATA = "All data";
    public static String IS_LOGIN = "islogin";
    public static String IS_FIRST_TIME = "0";
    public static String REQUEST_CODE_NAME = "request_code";
    public static String IS_AUTHENTICATED = "isauthenticated";
    public static String MASTER_PIN = "master_pin";
    public static String IS_PIN_SET = "ispin_set";
    public static String IS_USER_RESTRICTED = "is_user_restricted";


    private boolean turn = false;
    public static DatabaseReference myRef;
    ActivityResultLauncher<Intent> getResult;


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
                            uid = Objects.requireNonNull(user).getUid();
                            //check user if already signed up in background
                            threadRunnable threadRunnable = new threadRunnable();
                            new Thread(threadRunnable).start();

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(LogInActivity.this, "before while Loop...", Toast.LENGTH_SHORT).show();
                                    while (!turn) {
                                        progressBar();
                                    }
                                    prograceBar.dismissbar();
                                    String temp = sharedPreferences.getString(IS_FIRST_TIME, "0");

                                    if (temp == "0") {
                                        String device_name = Build.MANUFACTURER + " | " + Build.DEVICE;
                                        SharedPreferences.Editor editor1 = sharedPreferences.edit();
                                        editor1.putString(LogInActivity.AES_KEY, PassGenActivity.generateRandomPassword(22, true, true, true, false) + "==");
                                        editor1.putString(LogInActivity.AES_IV, PassGenActivity.generateRandomPassword(16, true, true, true, false));
                                        editor1.putBoolean(IS_LOGIN, true);
                                        editor1.putString(IS_FIRST_TIME, "1");
                                        editor1.apply();

                                        writeData(user);
                                        turn = false;
                                        Toast.makeText(LogInActivity.this, "Registration Completed!!", Toast.LENGTH_SHORT).show();
                                        SplashActivity.isForeground = true;
                                        Intent intent = new Intent(getApplicationContext(), pinLockFragment.class);
                                        intent.putExtra(LogInActivity.REQUEST_CODE_NAME, "setpin");
                                        intent.putExtra("title", "Set Pin");
                                        startActivity(intent);
                                    } else {
                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("signupdata").child(uid);
                                        // Read from the database
                                        reference.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                // This method is called once with the initial value and again
                                                // whenever data at this location is updated.
                                                String aes_iv = dataSnapshot.child("aes_iv").getValue(String.class);
                                                String aes_key = dataSnapshot.child("aes_key").getValue(String.class);

                                                SharedPreferences.Editor editor1 = sharedPreferences.edit();
                                                editor1.putString(LogInActivity.AES_KEY, aes_key);
                                                editor1.putString(LogInActivity.AES_IV, aes_iv);
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
                                        Intent intent = new Intent(getApplicationContext(), pinLockFragment.class);
                                        intent.putExtra(LogInActivity.REQUEST_CODE_NAME, "setpin");
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

    private void checkUser() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("signupdata");
        reference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
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
                    editor1.putString(IS_FIRST_TIME, "1");
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
    }

    public class threadRunnable implements Runnable {
        Handler handler = new Handler();
        View view;

        public threadRunnable() {
        }

        @Override
        public void run() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    checkUser();
                }
            });
        }
    }
    
    private void writeData(FirebaseUser user) {
        String name, email, uid;
        name = user.getDisplayName();
        email = user.getEmail();
        uid = user.getUid();

        createEntryOnDatabse(name, email, uid);

    }

    public void createEntryOnDatabse(String name, String email, String uid) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference("signupdata");

        AES aes = new AES();
        String encryptionKey = sharedPreferences.getString(LogInActivity.AES_KEY, null);
        String encryptionIv = sharedPreferences.getString(LogInActivity.AES_IV, null);
        aes.initFromStrings(encryptionKey, encryptionIv);
        String encryptedName, encryptedEmail;
        try {
            Toast.makeText(LogInActivity.this, "Creating Data  Entry on DB", Toast.LENGTH_SHORT).show();
            encryptedName = aes.encrypt(name);
            encryptedEmail = aes.encrypt(email);
            UserHelperClass userHelperClass = new UserHelperClass(encryptedName, encryptedEmail, encryptionKey, encryptionIv, uid);


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

    @Override
    protected void onStart() {
        super.onStart();
        if (SplashActivity.isBackground){
            Intent intent = new Intent(LogInActivity.this, BiometricActivity.class);
            intent.putExtra(REQUEST_CODE_NAME, "LockBackGroundApp");
            startActivity(intent);
        }
        if (SplashActivity.isForeground){
            SplashActivity.isForeground = false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Toast.makeText(this, "tabLayoutActivity.isForeground: " + SplashActivity.isForeground, Toast.LENGTH_SHORT).show();
        if (!SplashActivity.isForeground){
            SplashActivity.isBackground = true;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SplashActivity.isForeground = true;
    }
}