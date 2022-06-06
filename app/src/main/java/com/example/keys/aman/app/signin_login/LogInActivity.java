package com.example.keys.aman.app.signin_login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.keys.R;
import com.example.keys.aman.app.AES;
import com.example.keys.aman.app.PrograceBar;
import com.example.keys.aman.app.home.HomeActivity;
import com.example.keys.aman.app.home.PassGenActivity;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class LogInActivity extends AppCompatActivity {

    Button btn_login;


    private GoogleSignInClient mGoogleSignInClient;
    private final static int RC_SIGN_IN = 123;
    private FirebaseAuth mAuth;
    private String uid;
    SharedPreferences sharedPreferences;
    public static String AES_KEY = "aes_key";
    public static String AES_IV = "aes_iv";
    public static final String SHARED_PREF_ALL_DATA = "All data";
    public static final String KEY_USER_MOBILE = "mobile";
    public static final String KEY_USER_PASSSWORD = "password";
    public static final String KEY_USER_NAME = "name";
    public static final String KEY_USER_EMAIL = "email";
    public static String KEY_REMEMBER_ME;
    public static String KEY_USE_FINGERPRINT;
    public static String KEY_USE_PIN;
    public static String KEY_CREATE_ADDP_SHORTCUT;
    public static String KEY_CREATE_ADDN_SHORTCUT;
    public static String ISLOGIN;
    public static String ISFIRST_TIME = "0";

    public static final String TAG = "main Activity";
    public static String aes_key = "aes_key";
    public static String aes_iv = "aes_iv";
    private PrograceBar prograce_bar;


    @Override
    protected void onStart() {
        super.onStart();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_log_in);
        sharedPreferences = getSharedPreferences(SHARED_PREF_ALL_DATA, MODE_PRIVATE);

        //Hooks
        btn_login = findViewById(R.id.btn_login);

        String islogin = sharedPreferences.getString(ISLOGIN, null);
        System.out.println(islogin);
        if (islogin.equals("true")) {
            Intent intent = new Intent(LogInActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        if (user != null) {
//            //readData(uid);
//            Toast.makeText(LogInActivity.this, "check 1", Toast.LENGTH_SHORT).show();
//            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
//            System.out.println("check 0");
//            startActivity(intent);
//
//        }
        mAuth = FirebaseAuth.getInstance();
        createRequest();

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressbar();
                signIn();
            }
        });
    }


    private void createRequest() {
        System.out.println("Creating Request...");
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
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        progressbar();

        System.out.println("getting result from intent");
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                System.out.println("Authenticating with firebase...");
                GoogleSignInAccount account = task.getResult(ApiException.class);
                prograce_bar.dismissbar();
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                // ...
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        System.out.println("Authenticating with firebase...");
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            System.out.println("Authentication successful with firebase...");
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            uid = user.getUid();
                            //check user if already signed up
                            System.out.println("checking User on firebase...");
                            checkUser();

                            System.out.println("uid: " + uid);
//                            prograce_bar.dismissbar();
                            writeData(user);
                            //readData(uid);
                            prograce_bar.dismissbar();

                            if (sharedPreferences.getString(ISFIRST_TIME,null) == "0"){
                                Toast.makeText(LogInActivity.this, "Generating KEY and IV...", Toast.LENGTH_SHORT).show();
                                SharedPreferences.Editor editor1 = sharedPreferences.edit();
                                editor1.putString(LogInActivity.AES_KEY, PassGenActivity.generateRandomPassword(22, true, true, true, false) + "==");
                                editor1.putString(LogInActivity.AES_IV, PassGenActivity.generateRandomPassword(16, true, true, true, false));
                                editor1.putString(ISLOGIN, "true");
                                editor1.putString(ISFIRST_TIME,"1");
                                editor1.apply();
                            }

                            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                            startActivity(intent);


                        } else {
                            Toast.makeText(LogInActivity.this, "Sorry auth failed.", Toast.LENGTH_SHORT).show();


                        }
                    }
                });
    }

    private void checkUser() {
        System.out.println("checking User on firebase...");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("signupdata");
        System.out.println("UID: " + uid);
        Query checkUser = reference.orderByChild("uid").equalTo(uid);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    System.out.println("**Data exist!!");
//                    String nameFromDB = dataSnapshot.child(userEnteredUsername).child("name").getValue(String.class);
//                    String usernameFromDB = dataSnapshot.child(userEnteredUsername).child("username").getValue(String.class);
//                    String phoneNoFromDB = dataSnapshot.child(userEnteredUsername).child("phoneNo").getValue(String.class);
//                    String emailFromDB = dataSnapshot.child(userEnteredUsername).child("email").getValue(String.class);

                } else {
                    System.out.println("**Data does not exist!!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println("Error!! " + error);

            }
        });
    }

    private void readData(String uid) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("signupdata");

        Query checkUser = reference.orderByChild("uid").equalTo(uid);
        System.out.println("<>" + checkUser + uid);

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                System.out.println("check /");
                if (dataSnapshot.exists()) {
                    System.out.println("check //");
                    System.out.println(dataSnapshot);
                } else {
                    System.out.println("check  ~//");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void writeData(FirebaseUser user) {
        String name, email, mobile, uid;
        name = user.getDisplayName();
        email = user.getEmail();
        mobile = "1234567890";

        uid = user.getUid();
        System.out.println(name + email + mobile + uid);
        createEntryonDatabse(name, email, mobile, uid);

    }

    public void createEntryonDatabse(String name, String email, String mobile, String uid) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference("signupdata");

        //progressbar();
        AES aes = new AES();
        String key = sharedPreferences.getString(LogInActivity.AES_KEY, null);
        String iv = sharedPreferences.getString(LogInActivity.AES_IV, null);
        aes.initFromStrings(key, iv);
        String e_name, e_mobile, e_email;
        try {
            Toast.makeText(LogInActivity.this, "Saving Data on DB", Toast.LENGTH_SHORT).show();
            e_name = aes.encrypt(name);
            e_mobile = aes.encrypt(mobile);
            e_email = aes.encrypt(email);
            UserHelperClass userHelperClass = new UserHelperClass(e_name, e_mobile, e_email, key, iv, uid);


            myRef.child(uid).setValue(userHelperClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void progressbar() {
        prograce_bar = new PrograceBar(LogInActivity.this);
        prograce_bar.showDialog();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        }, 500);
    }
}