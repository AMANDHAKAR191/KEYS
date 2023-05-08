/*created by AMAN DHAKAR
Last update 07/05/2025*/

package com.keys.aman.signin_login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.keys.aman.data.MyPreference;
import com.keys.aman.ProgressBar;
import com.keys.aman.R;
import com.keys.aman.SplashActivity;
import com.keys.aman.authentication.BiometricAuthActivity;
import com.keys.aman.authentication.PinLockActivity;
import com.keys.aman.data.Firebase;
import com.keys.aman.data.iFirebaseDAO;
import com.keys.aman.databinding.ActivityLogInBinding;
import com.keys.aman.iProgressBar;

public class LogInActivity extends AppCompatActivity{

    public static final String REQUEST_ID = "LogInActivity";
    private static final String TAG = "LogInActivity";
    public final String REQUEST_CODE_NAME = "request_code";
    public GoogleSignInOptions gso;
    //objects
    MyPreference myPreference;
    ActivityResultLauncher<Intent> getResultLogin;
    iProgressBar iProgressBar;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    ActivityLogInBinding binding;
    iFirebaseDAO iFirebaseDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLogInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initVariables();
        checkGooglePlayServiceSecurityProvider();

//        Check if User is already login then go direct to HomeScreen
        if (myPreference.isUserLoggedIn()) {
            SplashActivity.isForeground = true;
            startBioAuthActivity();
        }

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createRequest();
                SplashActivity.isForeground = true;
                //Show ProgressBar and Update Status
                iProgressBar.showDialog();
                iProgressBar.updateProgressBar("Fetching google account list...");
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
                        try {
                            // Google Sign In was successful, authenticate with Firebase
                            //Update ProgressBar status
                            iProgressBar.updateProgressBar("Authenticating with firebase...");
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            firebaseAuthWithGoogle(account);
                        } catch (ApiException e) {
                            // Google Sign In failed, update UI appropriately
                            Toast.makeText(LogInActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }
                }
        );
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (iProgressBar.isDialogShowing()){
            iProgressBar.dismissDialog();
        }
    }

    private void checkGooglePlayServiceSecurityProvider() {
        //This code initializes the Google Play services security provider to ensure that the app uses
        // the latest security protocols available on the device. It uses the ProviderInstaller class
        // from the Google Play services library to install the security provider if it is not already
        // installed on the device.
        try {
            ProviderInstaller.installIfNeeded(getApplicationContext());
        } catch (GooglePlayServicesRepairableException |
                 GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    public void initVariables(){
        mAuth = FirebaseAuth.getInstance();
        //initialize local database
        myPreference = MyPreference.getInstance(this);
        iProgressBar = new ProgressBar(LogInActivity.this);
        SplashActivity.isForeground = false;
        iFirebaseDAO = Firebase.getInstance(LogInActivity.this);
    }

    private void startBioAuthActivity() {
        Intent intent = new Intent(LogInActivity.this, BiometricAuthActivity.class);
        intent.putExtra(REQUEST_CODE_NAME, REQUEST_ID);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
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
                            //Update ProgressBar status
                            iProgressBar.updateProgressBar("Checking User...");
                            //check user if already signed up in background
                            iFirebaseDAO.checkUser(new Firebase.iUserCheckCallback() {
                                @Override
                                public void onUserExist() {
                                    iProgressBar.dismissDialog();
                                    Toast.makeText(LogInActivity.this, "User Exist, loading data...", Toast.LENGTH_SHORT).show();
                                    iFirebaseDAO.loadUserData();
                                    SplashActivity.isForeground = true;
                                    startPinLockActivity();
                                }

                                @Override
                                public void onUserNotExist() {
                                    iFirebaseDAO.saveUserData(new Firebase.iCreateAccountCallBack() {
                                        @Override
                                        public void onAccountCreatedSuccessfullyOnFirebase() {
                                            //Update ProgressBar status
                                            iProgressBar.updateProgressBar("Account Created");
                                            SplashActivity.isForeground = true;
                                            startPinLockActivity();
                                        }

                                        @Override
                                        public void onAccountCreationFailed(String message) {
                                            Toast.makeText(LogInActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                            iProgressBar.dismissDialog();
                                        }
                                    });

                                }
                            });
                        } else {
                            try {
                                task.getResult();
                            } catch (Exception firebaseNetworkException) {
                                Toast.makeText(LogInActivity.this, "Check Internet Connection", Toast.LENGTH_SHORT).show();
                                iProgressBar.dismissDialog();
                            }

                        }
                    }
                });
    }

    private void startPinLockActivity() {
        Intent intent = new Intent(getApplicationContext(), PinLockActivity.class);
        intent.putExtra(REQUEST_CODE_NAME, REQUEST_ID);
        intent.putExtra("title", "Set Pin");
        startActivity(intent);
        finish();
    }
}