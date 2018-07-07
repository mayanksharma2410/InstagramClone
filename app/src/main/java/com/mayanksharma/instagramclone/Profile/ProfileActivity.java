package com.mayanksharma.instagramclone.Profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.mayanksharma.instagramclone.R;
import com.mayanksharma.instagramclone.Utils.BottomNavigationViewHelper;
import com.mayanksharma.instagramclone.Utils.GridImageAdapter;
import com.mayanksharma.instagramclone.Utils.UniversalImageLoader;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {
    public static final String TAG = "ProfileActivity";
    private Context mContext = ProfileActivity.this;
    public static final int ACTIVITY_NUM = 4;
    private ImageView profilePhoto;
    private ProgressBar mProgressBar;
    private static final int NUM_GRID_COL = 3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Log.d(TAG, "onCreate: starting profile activity");

        profilePhoto = (ImageView)findViewById(R.id.profile_image);

        init();

//        setupBottomNavigationView();
//        setupToolbar();
//        setupActivityWidgets();
//        setProfileImage();
//        tempGridSetup();
    }

    private void init()
    {
        Log.d(TAG, "init: inflating " + getString(R.string.profile_fragment));

        ProfileFragment fragment = new ProfileFragment();
        FragmentTransaction transaction = ProfileActivity.this.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(getString(R.string.profile_fragment));
        transaction.commit();
    }

//    private void tempGridSetup()
//    {
//        ArrayList<String> imgURLs = new ArrayList<>();
//        imgURLs.add("https://images.idgesg.net/images/article/2017/08/android_robot_logo_by_ornecolorada_cc0_via_pixabay1904852_wide-100732483-large.jpg");
//        imgURLs.add("https://i.ytimg.com/vi/wTblbYqQQag/maxresdefault.jpg");
//        imgURLs.add("https://images.idgesg.net/images/article/2017/08/android_robot_logo_by_ornecolorada_cc0_via_pixabay1904852_wide-100732483-large.jpg");
//        imgURLs.add("https://images.idgesg.net/images/article/2017/08/android_robot_logo_by_ornecolorada_cc0_via_pixabay1904852_wide-100732483-large.jpg");
//        imgURLs.add("https://images.idgesg.net/images/article/2017/08/android_robot_logo_by_ornecolorada_cc0_via_pixabay1904852_wide-100732483-large.jpg");
//        imgURLs.add("https://images.idgesg.net/images/article/2017/08/android_robot_logo_by_ornecolorada_cc0_via_pixabay1904852_wide-100732483-large.jpg");
//        imgURLs.add("https://images.idgesg.net/images/article/2017/08/android_robot_logo_by_ornecolorada_cc0_via_pixabay1904852_wide-100732483-large.jpg");
//        setupImageGrid(imgURLs);
//    }
//
//    private void setupImageGrid(ArrayList<String> imgURLs)
//    {
//        GridView gridView = (GridView)findViewById(R.id.gridView);
//
//        int gridWidth = getResources().getDisplayMetrics().widthPixels;
//        int imageWidth = gridWidth/NUM_GRID_COL;
//        gridView.setColumnWidth(imageWidth);
//        GridImageAdapter adapter = new GridImageAdapter(mContext, R.layout.layout_grid_imageview,"", imgURLs);
//        gridView.setAdapter(adapter);
//    }
//
//
//    private void setProfileImage()
//    {
//        Log.d(TAG, "setProfileImage: setting up profile image");
//        String imgURL = "images.idgesg.net/images/article/2017/08/android_robot_logo_by_ornecolorada_cc0_via_pixabay1904852_wide-100732483-large.jpg";
//        UniversalImageLoader.setImage(imgURL, profilePhoto, mProgressBar,"https://");
//    }
//
//    private void setupActivityWidgets()
//    {
//        mProgressBar = (ProgressBar)findViewById(R.id.profileProgressBar);
//        mProgressBar.setVisibility(View.GONE);
//    }
//
//    private void setupToolbar()
//    {
//        Toolbar toolbar = (Toolbar)findViewById(R.id.profile_toolbar);
//        setSupportActionBar(toolbar);
//
//        ImageView profileMenu = (ImageView)findViewById(R.id.profileMenu);
//        profileMenu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(TAG, "onClick: navigating to account settings");
//                Toast.makeText(mContext, "clicked", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(mContext, AccountSettingsActivity.class);
//                startActivity(intent);
//            }
//        });
//    }
//
//    //Bottom Navigation View
//    private void setupBottomNavigationView()
//    {
//        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigation View");
//        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx)findViewById(R.id.bottomNavViewBar);
//        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
//        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationViewEx);
//        Menu menu = bottomNavigationViewEx.getMenu();
//        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
//        menuItem.setChecked(true);
//    }

}
