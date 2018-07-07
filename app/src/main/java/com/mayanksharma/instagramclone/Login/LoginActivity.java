package com.mayanksharma.instagramclone.Login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mayanksharma.instagramclone.Home.HomeActivity;
import com.mayanksharma.instagramclone.R;

import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private Context mContext;
    private ProgressBar mProgressBar;
    private EditText mEmail, mPassword;
    private TextView mPleaseWait;
    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mPleaseWait = (TextView)findViewById(R.id.pleaseWaitText);
        mProgressBar = (ProgressBar)findViewById(R.id.progressbar);
        mProgressBar.setVisibility(View.GONE);
        mEmail = (EditText)findViewById(R.id.input_email);
        mPassword = (EditText)findViewById(R.id.input_password);
        mContext = LoginActivity.this;

        Log.d(TAG, "onCreate: Login Activity started...");
        mProgressBar.setVisibility(View.GONE);
        mPleaseWait.setVisibility(View.GONE);

        setupFirebaseAuth();
        init();
    }

    private boolean isStringNull(String string)
    {
        Log.d(TAG, "isStringNull: checking if string is null");
        if(string.equals(""))
        {
            return true;
        }else
            return false;
    }

    /*
     ******************************************* Firebase ***************************************************************
     */

    private void init()
    {
        //initializing the button for logging in
        Button mButton = (Button)findViewById(R.id.btn_login);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: logging in...");

                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();

                if (isStringNull(email) && isStringNull(password)) {
                    Toast.makeText(mContext, "Please fill in the details...", Toast.LENGTH_SHORT).show();
                }else
                {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mPleaseWait.setVisibility(View.VISIBLE);

                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Log.d(TAG, "onComplete: signing with email" + task.isSuccessful());

                                    FirebaseUser user = mAuth.getCurrentUser();

                                    if (!task.isSuccessful()) {
                                        // Sign in fails
                                        Log.d(TAG, "signInWithEmail:failed login", task.getException());
                                        Toast.makeText(LoginActivity.this, R.string.auth_failed,
                                                Toast.LENGTH_SHORT).show();
                                        mProgressBar.setVisibility(View.GONE);
                                        mPleaseWait.setVisibility(View.GONE);
                                    } else {
                                        // If sign in success
                                        try
                                        {
                                            if(user.isEmailVerified())
                                            {
                                                Log.d(TAG, "onComplete: success. email is verified");
                                                Intent intent = new Intent(mContext, HomeActivity.class);
                                                startActivity(intent);
                                            }else{
                                                Toast.makeText(mContext, "Email is not verified \n check your email inbox", Toast.LENGTH_SHORT).show();
                                                mProgressBar.setVisibility(View.GONE);
                                                mPleaseWait.setVisibility(View.GONE);
                                                mAuth.signOut();
                                            }

                                        }catch(NullPointerException e)
                                        {
                                            Log.d(TAG, "onComplete: NullPointerException: " + e.getMessage());
                                        }
                                    }

                                    // ...
                                }
                            });

                }
            }
        });

        TextView linkSignUp = (TextView)findViewById(R.id.link_signup);
        linkSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to register activity...");
                Intent intent = new Intent(mContext, RegisterActivity.class);
                startActivity(intent);
            }
        });

        /**
         * if the User is logged in it is navigated to home activity
         */
        if(mAuth.getCurrentUser() != null)
        {
            Intent intent = new Intent(mContext, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void setupFirebaseAuth()
    {
        Log.d(TAG, "setupFirebaseAuth: setting up authentication");
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();


                if (user != null)
                {
                    //User is signed in
                    Log.d(TAG, "onAuthStateChanged: signed in " + user.getUid());
                }else
                {
                    //User us not signeed in
                    Log.d(TAG, "onAuthStateChanged: signed out");
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null)
        {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
