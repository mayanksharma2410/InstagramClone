package com.mayanksharma.instagramclone.Share;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mayanksharma.instagramclone.R;
import com.mayanksharma.instagramclone.Utils.FirebaseMethods;
import com.mayanksharma.instagramclone.Utils.UniversalImageLoader;

public class NextActivity extends AppCompatActivity {
    private final static String TAG = "NextActivity";

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;

    //variables
    private String mAppend = "file:/";
    private int imageCount = 0;
    private String imgURL;

    //widgets
    private EditText mCaption;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);
        mFirebaseMethods = new FirebaseMethods(NextActivity.this);

        Log.d(TAG, "onCreate: got the chosen image " + getIntent().getStringExtra(getString(R.string.selected_image)));

        mCaption = (EditText)findViewById(R.id.caption);

        setupFirebaseAuth();

        ImageView backArrow = (ImageView)findViewById(R.id.ivBackArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing the share activity...");
                finish();
            }
        });

        TextView post = (TextView)findViewById(R.id.tvPost);
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to the final screen for uploading...");
                //upload the image to firebase
                Toast.makeText(NextActivity.this, "uploading the photo...", Toast.LENGTH_SHORT).show();
                String caption = mCaption.getText().toString();
                mFirebaseMethods.uploadNewPhoto(getString(R.string.new_photo), caption, imageCount, imgURL);
            }
        });

        setImage();

    }

    public void someMethod()
    {
        /*
        step 1)
        Create a data model for photos

        step 2)
        Add properties to the photo objects(caption, data, imgURL, photo_id, tags, user_id)

        step 3)
        count the no. of photos the user already has

        step 4)
        a) Upload the photo to firebase storage and insert the new nodes to the firebase database
        b) insert into "photos" node
        c) insert into "user_photos" node

         */
    }

    /**
     * gets the image url from the incoming intent and displays the
     */
    private void setImage()
    {
        Intent intent = getIntent();
        ImageView image = (ImageView)findViewById(R.id.imageShare);
        imgURL = intent.getStringExtra(getString(R.string.selected_image));
        UniversalImageLoader.setImage(imgURL, image, null, mAppend);
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
        Log.d(TAG, "onDataChange: image count " + imageCount);

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

                imageCount = mFirebaseMethods.getImageCount(dataSnapshot);
                Log.d(TAG, "onDataChange: image count " + imageCount);
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
