package com.mayanksharma.instagramclone.Profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.mayanksharma.instagramclone.Login.LoginActivity;
import com.mayanksharma.instagramclone.R;
import com.mayanksharma.instagramclone.Utils.BottomNavigationViewHelper;
import com.mayanksharma.instagramclone.Utils.FirebaseMethods;
import com.mayanksharma.instagramclone.Utils.UniversalImageLoader;
import com.mayanksharma.instagramclone.models.User;
import com.mayanksharma.instagramclone.models.UserAccountSettings;
import com.mayanksharma.instagramclone.models.UserSettings;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    public static final int ACTIVITY_NUM = 4;

    private TextView mPosts, mFollowers, mFollowing, mDisplayName, mUsername, mStatus, mDescription;
    private CircleImageView mProfilePhoto;
    private ProgressBar mProgressbar;
    private GridView gridView;
    private Toolbar toolbar;
    private ImageView profileMenu;
    private BottomNavigationViewEx bottomNavigationView;
    private Context mContext;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        mDisplayName = (TextView)view.findViewById(R.id.display_name);
        mUsername = (TextView)view.findViewById(R.id.username);
        mStatus = (TextView)view.findViewById(R.id.status);
        mDescription = (TextView)view.findViewById(R.id.description);
        mPosts = (TextView)view.findViewById(R.id.tvPost);
        mFollowers = (TextView)view.findViewById(R.id.tvfollowers);
        mFollowing = (TextView)view.findViewById(R.id.tvFollowing);
        mProfilePhoto = (CircleImageView)view.findViewById(R.id.profile_image);
        mProgressbar = (ProgressBar)view.findViewById(R.id.profileProgressBar);
        gridView = (GridView)view.findViewById(R.id.gridView);
        toolbar = (Toolbar)view.findViewById(R.id.profile_toolbar);
        profileMenu = (ImageView)view.findViewById(R.id.profileMenu);
        TextView editProfile = (TextView)view.findViewById(R.id.textEditProfile);
        bottomNavigationView = (BottomNavigationViewEx)view.findViewById(R.id.bottomNavViewBar);
        mContext = getActivity();
        mFirebaseMethods = new FirebaseMethods(getActivity());

        Log.d(TAG, "onCreateView: starting profile fragment...");

        setupBottomNavigationView();
        setupToolbar();
        setupFirebaseAuth();

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to..." + mContext.getString(R.string.edit_profile_fragment));
                Intent intent = new Intent(mContext, AccountSettingsActivity.class);
                intent.putExtra(getString(R.string.calling_activity), getString(R.string.profile_activity));
                startActivity(intent);
            }
        });

        return view;
    }

    private void setupProfileWidgets(UserSettings userSettings)
    {
        Log.d(TAG, "setupProfileWidgets: setting up widgets from data retrieved from firebase database..." + userSettings.toString());
        Log.d(TAG, "setupProfileWidgets: setting up widgets from data retrieved from firebase database..." + userSettings.getSettings().toString());

        //User user = userSettings.getUser();
        UserAccountSettings settings = userSettings.getSettings();

        UniversalImageLoader.setImage(settings.getProfile_photo(), mProfilePhoto, null, "");
        mDisplayName.setText(settings.getDisplay_name());
        mUsername.setText(settings.getUsername());
        mStatus.setText(settings.getStatus());
        mDescription.setText(settings.getDescription());
        mPosts.setText(String.valueOf(settings.getPosts()));
        mFollowers.setText(String.valueOf(settings.getFollowers()));
        mFollowing.setText(String.valueOf(settings.getFollowing()));
        mProgressbar.setVisibility(View.GONE);
    }

    private void setupToolbar()
    {
        ((ProfileActivity)getActivity()).setSupportActionBar(toolbar);

        profileMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to account settings");
                Toast.makeText(mContext, "clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(mContext, AccountSettingsActivity.class);
                startActivity(intent);
            }
        });
    }

    //Bottom Navigation View
    private void setupBottomNavigationView()
    {
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigation View");
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationView);
        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

    /*
     ******************************************* Firebase ***************************************************************
     */

    private void setupFirebaseAuth()
    {
        Log.d(TAG, "setupFirebaseAuth: setting up authentication");
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

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

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //retrieve user information from the database
                setupProfileWidgets(mFirebaseMethods.getUserSettings(dataSnapshot));

                //retrieve images for the user
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        FirebaseUser user = mAuth.getCurrentUser();
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
