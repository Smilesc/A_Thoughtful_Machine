package com.cita.a_thoughtful_machine;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class LoadingScreen extends AppCompatActivity {
    ImageView imageView;
    ProgressBar progressBar;
    GoogleSignInClient mGoogleSignInClient;
    static final int RC_SIGN_IN = 6;
    Button sign_in;
    Process_Image process_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);
        imageView = findViewById(R.id.imageView);
        imageView.setImageURI(getIntent().getData());

        sign_in = findViewById(R.id.sign_in);

        progressBar = findViewById(R.id.progressBar);

        System.out.println("CHECK HERE");
        AccountManager am = AccountManager.get(this); // "this" references the current Context
        System.out.println("AND HERE");
        Account[] accounts = am.getAccountsByType("com.google");
        System.out.println("ACCOUNTS: " + accounts[0]);
        Account myAccount_ = accounts[0];
        Bundle options = new Bundle();

        am.getAuthToken(
                myAccount_,                     // Account retrieved using getAccountsByType()
                "oauth2:https://www.googleapis.com/auth/cloud-platform",            // Auth scope
                options,                        // Authenticator-specific options
                true,                           // Your activity
                new OnTokenAcquired(),          // Callback called when a token is successfully acquired
                new Handler(new OnError()));    // Callback called if an error occurs

        System.out.println("OK WE GOT PAST IT");

//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestEmail()
//                .build();
//
//        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
//
//        sign_in.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                signIn();
//            }
//        });
//        process_image.execute();
    }

    private class OnTokenAcquired implements AccountManagerCallback<Bundle> {
        @Override
        public void run(AccountManagerFuture<Bundle> result) {
            try {
                // Get the result of the operation from the AccountManagerFuture.
                Bundle bundle = result.getResult();
                String token = bundle.getString(AccountManager.KEY_AUTHTOKEN);
                System.out.println("TOKEN: " + token);
                process_image = new Process_Image(progressBar, token);
                process_image.execute();
            }
            catch(Exception e){
                System.out.println("SOMETHING WENT WRONG: " + e.getMessage());
            }

            // The token is a named value in the bundle. The name of the value
            // is stored in the constant AccountManager.KEY_AUTHTOKEN.

        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
//        if (requestCode == RC_SIGN_IN) {
//            // The Task returned from this call is always completed, no need to attach
//            // a listener.
//            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//            handleSignInResult(task);
//        }
//    }

//    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
//
//        try {
//            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
//
//            Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_SHORT).show();
//            Process_Image process_image = new Process_Image(progressBar);
//            process_image.execute();
//
//        } catch (ApiException e) {
//            Toast.makeText(getApplicationContext(),"Failer",Toast.LENGTH_SHORT).show();
//        }
//    }

}
