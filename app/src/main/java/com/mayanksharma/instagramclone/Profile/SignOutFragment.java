package com.mayanksharma.instagramclone.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mayanksharma.instagramclone.Login.LoginActivity;
import com.mayanksharma.instagramclone.R;

public class SignOutFragment extends Fragment {
    private static final String TAG = "SignOutFragment";
    private ProgressBar progressBar;
    private TextView tvSignOut, tvSigningOut;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signout, container, false);

        tvSignOut = (TextView)view.findViewById(R.id.tvConfirmSignOut);
        progressBar = (ProgressBar)view.findViewById(R.id.progressBar);
        tvSigningOut = (TextView)view.findViewById(R.id.tvSigningOut);
        Button btnSignOut = (Button)view.findViewById(R.id.btnConfirmSignOut);

        progressBar.setVisibility(View.GONE);
        tvSigningOut.setVisibility(View.GONE);

        setupFirebaseAuth();

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: attemptong to sign out...");

                progressBar.setVisibility(View.GONE);
                tvSigningOut.setVisibility(View.GONE);

                mAuth.signOut();
                getActivity().finish();
            }
        });

        return view;
    }

    /*
     ******************************************* Firebase ***************************************************************
     */


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
                    //User us not signed in
                    Log.d(TAG, "onAuthStateChanged: signed out");

                    Log.d(TAG, "onAuthStateChanged: navigating back to login activity...");
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null)
        {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
