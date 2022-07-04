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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.keys.R;
import com.example.keys.aman.app.AES;
import com.example.keys.aman.app.PrograceBar;
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


public class LogInActivity extends AppCompatActivity {

    Button btn_login;

    private GoogleSignInClient mGoogleSignInClient;
    private final static int RC_SIGN_IN = 123;
    private FirebaseAuth mAuth;
    private String uid;
    SharedPreferences sharedPreferences;
    public static final String AES_KEY = "aes_key";
    public static final String AES_IV = "aes_iv";
    public static final String SHARED_PREF_ALL_DATA = "All data";
    public static final String KEY_USER_NAME = "name";
    public static final String KEY_USER_EMAIL = "email";
    public static String ISLOGIN = "islogin";
    public static String ISFIRST_TIME = "0";
    public static String REQUEST_CODE_NAME = "request_code";
    public static String ISAUTHENTICATED = "isauthenticated";
    public static String MASTER_PIN = "master_pin";
    public static String ISPIN_SET = "ispin_set";

    public static String DEVICE_NAME = "device_name";

    public static final String TAG = "main Activity";
    private PrograceBar prograce_bar;
    private String A1;
    private boolean turn = false;
    private String val = "";
    public static DatabaseReference myRef;
    private FirebaseDatabase firebaseDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_log_in);
        sharedPreferences = getSharedPreferences(SHARED_PREF_ALL_DATA, MODE_PRIVATE);

        //Hooks
        btn_login = findViewById(R.id.btn_login);

//         Check if User is already login then go direct to HomeScreen
        Boolean islogin = sharedPreferences.getBoolean(ISLOGIN, false);
        System.out.println(islogin);
        if (islogin) {

            Intent intent = new Intent(LogInActivity.this, BiometricActivity.class);
            intent.putExtra(REQUEST_CODE_NAME,"LogInActivity");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }


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

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                prograce_bar.dismissbar();
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                // ...
                System.out.println("Error: " +  e.getMessage());
                Toast.makeText(this, "Error: " +  e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            uid = user.getUid();
//                            System.out.println("Turn = " + turn);
                            checkUser();

                            //check user if already signed up

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    while (!turn){
                                        progressbar();
                                        System.out.println("checking User on firebase...");
//                                        Snackbar.make(getCurrentFocus(),"Your internet is slow.\n please wait or try again!!", BaseTransientBottomBar.LENGTH_LONG).show();
                                    }
                                    prograce_bar.dismissbar();
                                    A1 = sharedPreferences.getString(ISFIRST_TIME,"0");

                                    if (A1 == "0"){
                                        String device_name = Build.MANUFACTURER + " | " + Build.DEVICE;
                                        SharedPreferences.Editor editor1 = sharedPreferences.edit();
                                        editor1.putString(LogInActivity.AES_KEY, PassGenActivity.generateRandomPassword(22, true, true, true, false) + "==");
                                        editor1.putString(LogInActivity.AES_IV, PassGenActivity.generateRandomPassword(16, true, true, true, false));
                                        editor1.putBoolean(ISLOGIN, true);
                                        editor1.putString(ISFIRST_TIME,"1");
                                        editor1.putString(DEVICE_NAME, device_name);
                                        editor1.apply();
                                        Toast.makeText(LogInActivity.this, "Device Name: " + device_name, Toast.LENGTH_SHORT).show();

//                                        System.out.println("ISLOGIN: " + sharedPreferences.getString(ISLOGIN,null));
//                                        System.out.println("ISFIRST_TIME: " + sharedPreferences.getString(ISFIRST_TIME,null));
                                        writeData(user);
                                        turn = false;
                                        Intent intent = new Intent(getApplicationContext(), pinLockFragment.class);
                                        intent.putExtra(LogInActivity.REQUEST_CODE_NAME,"setpin");
                                        intent.putExtra("title","Set Pin");
                                        startActivity(intent);
                                    }else {


                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("signupdata").child(uid);
                                        // Read from the database
                                        reference.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                // This method is called once with the initial value and again
                                                // whenever data at this location is updated.
                                                String aes_iv = dataSnapshot.child("aes_iv").getValue(String.class);
                                                String aes_key= dataSnapshot.child("aes_key").getValue(String.class);

                                                SharedPreferences.Editor editor1 = sharedPreferences.edit();
                                                editor1.putString(LogInActivity.AES_KEY, aes_key);
                                                editor1.putString(LogInActivity.AES_IV, aes_iv);
                                                editor1.putBoolean(ISLOGIN, true);
                                                editor1.putString(ISFIRST_TIME,"1");
                                                editor1.apply();
                                                turn = false;


                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                // Failed to read value
                                                System.out.println("Not able get data from database");
                                            }
                                        });


                                        //readData(uid);
                                        prograce_bar.dismissbar();
                                        System.out.println("Registration Completed!!");



                                        Intent intent = new Intent(getApplicationContext(), pinLockFragment.class);
                                        intent.putExtra(LogInActivity.REQUEST_CODE_NAME,"setpin");
                                        intent.putExtra("title","Set Pin");
                                        startActivity(intent);
                                    }
                                }
                            }, 10000);
                        } else {
                            Toast.makeText(LogInActivity.this, "Sorry auth failed.", Toast.LENGTH_SHORT).show();


                        }
                    }
                });
    }

    private void checkUser() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("signupdata");
        reference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                System.out.println(dataSnapshot.getValue());
                try {
                    val = dataSnapshot.getValue().toString();
                }catch (Exception e){
                    System.out.println("Exception: " + e.getMessage());
                    val = "1";
                }


                if (val != "1"){

                    SharedPreferences.Editor editor1 = sharedPreferences.edit();
                    editor1.putString(ISFIRST_TIME,"1");
                    editor1.apply();
                    turn = true;

                }else {
//                    System.out.println("User Does not Exist!!");
//                    System.out.println("Creating new User!!");
                    turn = true;
//                    System.out.println("Turn = " + turn);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                System.out.println(error);

            }
        });
    }

//    private void readData(String uid) {
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("signupdata");
//
//        Query checkUser = reference.orderByChild("uid").equalTo(uid);
//        System.out.println("<>" + checkUser + uid);
//
//        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                System.out.println("check /");
//                if (dataSnapshot.exists()) {
//                    System.out.println("check //");
//                    System.out.println(dataSnapshot);
//                } else {
//                    System.out.println("check  ~//");
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//    }

    private void writeData(FirebaseUser user) {
        String name, email, mobile, uid;
        name = user.getDisplayName();
        email = user.getEmail();
        mobile = "1234567890";

        uid = user.getUid();

        createEntryonDatabse(name, email, mobile, uid);

    }

    public void createEntryonDatabse(String name, String email, String mobile, String uid) {
        firebaseDatabase = FirebaseDatabase.getInstance();
        myRef = firebaseDatabase.getReference("signupdata");

        //progressbar();
        AES aes = new AES();
        String key = sharedPreferences.getString(LogInActivity.AES_KEY, null);
        String iv = sharedPreferences.getString(LogInActivity.AES_IV, null);
        aes.initFromStrings(key, iv);
        String e_name, e_mobile, e_email;
        try {
            Toast.makeText(LogInActivity.this, "Creating Data  Entry on DB", Toast.LENGTH_SHORT).show();
            e_name = aes.encrypt(name);
            e_mobile = aes.encrypt(mobile);
            e_email = aes.encrypt(email);
            UserHelperClass userHelperClass = new UserHelperClass(e_name, e_email, key, iv, uid);


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