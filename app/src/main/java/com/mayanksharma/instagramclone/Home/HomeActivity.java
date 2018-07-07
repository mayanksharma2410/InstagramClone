package com.mayanksharma.instagramclone.Home;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.mayanksharma.instagramclone.Login.LoginActivity;
import com.mayanksharma.instagramclone.R;
import com.mayanksharma.instagramclone.Utils.BottomNavigationViewHelper;
import com.mayanksharma.instagramclone.Utils.SectionsPagerAdapter;
import com.mayanksharma.instagramclone.Utils.UniversalImageLoader;
import com.nostra13.universalimageloader.core.ImageLoader;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";
    private Context mContext = HomeActivity.this;
    public static final int ACTIVITY_NUM = 0;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.d(TAG, "onCreate: starting...");

        setupFirebaseAuth();
        initImageLoader();
        setupBottomNavigationView();
        setupViewPager();
    }


    private void initImageLoader()
    {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(mContext);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    //responsible for having 3 tabs: camera, home and messages
    public void setupViewPager()
    {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new CameraFragment()); //index 0
        adapter.addFragment(new HomeFragment()); //index 1
        adapter.addFragment(new MessagesFragment()); //index 2
        ViewPager viewPager = (ViewPager)findViewById(R.id.container);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout)findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_camera);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_appname);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_arrow);
    }

    //Bottom Navigation View
    private void setupBottomNavigationView()
    {
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigation View");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx)findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }


    /*
      ******************************************* Firebase ***************************************************************
      */

    /**
     * checking if the @param User is logged in
     * @param user
     */
    private void checkCurrentUser(FirebaseUser user)
    {
        if (user == null)
        {
            Intent intent  = new Intent(mContext, LoginActivity.class);
            startActivity(intent);
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

                //Check if the User is logged in
                checkCurrentUser(user);

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
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        FirebaseUser user = mAuth.getCurrentUser();
        checkCurrentUser(user);
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