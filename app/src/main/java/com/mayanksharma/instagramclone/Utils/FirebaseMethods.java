package com.mayanksharma.instagramclone.Utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mayanksharma.instagramclone.Home.HomeActivity;
import com.mayanksharma.instagramclone.Profile.AccountSettingsActivity;
import com.mayanksharma.instagramclone.R;
import com.mayanksharma.instagramclone.models.Photo;
import com.mayanksharma.instagramclone.models.User;
import com.mayanksharma.instagramclone.models.UserAccountSettings;
import com.mayanksharma.instagramclone.models.UserSettings;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static com.mayanksharma.instagramclone.Utils.BottomNavigationViewHelper.TAG;

public class FirebaseMethods {

    private static final String TAG = "FirebaseMethods";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseStorage mStorage;
    private StorageReference mStorageReference;
    private String userID;

    //variables
    private Context mContext;
    private double mPhotoUploadProgress = 0;

    public FirebaseMethods(Context context) {
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mStorage = FirebaseStorage.getInstance();
        mStorageReference = mStorage.getReference();
        mContext = context;

        if(mAuth.getCurrentUser() != null){
            userID = mAuth.getCurrentUser().getUid();
        }
    }

    public void uploadNewPhoto(String photoType, final String caption, final int count, final String imgUrl)
    {
        Log.d(TAG, "uploadNewPhoto: atempting to upload a new photo");

        FilePaths filePaths = new FilePaths();
        //case 1) new Photo
        if (photoType.equals(mContext.getString(R.string.new_photo)))
        {
            Log.d(TAG, "uploadNewPhoto: uploading a NEW PHOTO...");
            String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            StorageReference storageReference = mStorageReference
                    .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/photo" + (count + 1));


            //convert image url to bitmap
            Bitmap bm = ImageManager.getBitmap(imgUrl);
            byte[] bytes = ImageManager.getBytesFromBitmap(bm, 100);

            UploadTask uploadTask = null;
            uploadTask = storageReference.putBytes(bytes);


            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri firebaseUrl = taskSnapshot.getDownloadUrl();

                    Toast.makeText(mContext, "photo upload success", Toast.LENGTH_SHORT).show();

                    //add the new photo to 'photos' node and 'user_photos' node
                    addPhotoToDatabase(caption, firebaseUrl.toString());
                    Log.d(TAG, "onSuccess: uploaded successfully");

                    //navigate to the main feed so the user can see their photo
                    Intent intent = new Intent(mContext, HomeActivity.class);
                    mContext.startActivity(intent);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: Photo upload failed.");
                    Toast.makeText(mContext, "Photo upload failed ", Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                    if(progress - 15 > mPhotoUploadProgress){
                        Toast.makeText(mContext, "photo upload progress: " + String.format("%.0f", progress) + "%", Toast.LENGTH_SHORT).show();
                        mPhotoUploadProgress = progress;
                    }

                    Log.d(TAG, "onProgress: upload progress: " + progress + "% done");
                }
            });

        }//case 2) new Profile Photo
        else if (photoType.equals(mContext.getString(R.string.profile_photo)))
        {
            Log.d(TAG, "uploadNewPhoto: uploading a NEW PROFILE PHOTO...");

            ((AccountSettingsActivity)mContext).setViewPager(
                    ((AccountSettingsActivity)mContext).pagerAdapter
                            .getFragmentNumber(mContext.getString(R.string.edit_profile_fragment)));

            String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
            StorageReference storageReference = mStorageReference
                    .child(filePaths.FIREBASE_IMAGE_STORAGE + "/" + user_id + "/profile_photo");


            //convert image url to bitmap
            Bitmap bm = ImageManager.getBitmap(imgUrl);
            byte[] bytes = ImageManager.getBytesFromBitmap(bm, 100);

            UploadTask uploadTask = null;
            uploadTask = storageReference.putBytes(bytes);


            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri firebaseUrl = taskSnapshot.getDownloadUrl();

                    Toast.makeText(mContext, "photo upload success", Toast.LENGTH_SHORT).show();

                    //insert into "user_account_settings" node
                    setProfilePhoto(firebaseUrl.toString());

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: Photo upload failed.");
                    Toast.makeText(mContext, "Photo upload failed ", Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();

                    if(progress - 15 > mPhotoUploadProgress){
                        Toast.makeText(mContext, "photo upload progress: " + String.format("%.0f", progress) + "%", Toast.LENGTH_SHORT).show();
                        mPhotoUploadProgress = progress;
                    }

                    Log.d(TAG, "onProgress: upload progress: " + progress + "% done");
                }
            });
        }


    }

    private void setProfilePhoto(String url)
    {
        Log.d(TAG, "setProfilePhoto: setting new profile photo..." + url);

        myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(mContext.getString(R.string.profile_photo)).setValue(url);
    }

    private String getTimestamp()
    {
//        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy'T'HH:mm:ss'Z", Locale.US);
//        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
        String timestamp = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
        return timestamp; //.format(new Date());
    }

    public void addPhotoToDatabase(String caption, String url)
    {
        Log.d(TAG, "addPhotoToDatabase: uploading photo to database...");

        String tags = StringManipulation.getTags(caption);
        String newPhotoKey = myRef.child(mContext.getString(R.string.dbname_photos)).push().getKey();
        Photo photo = new Photo();
        photo.setCaption(caption);
        photo.setDate_created(getTimestamp());
        photo.setImage_path(url);
        photo.setTags(tags);
        photo.setUser_id(userID);
        photo.setPhoto_id(newPhotoKey);

        //insert into database
        myRef.child(mContext.getString(R.string.dbname_user_photos)).child(userID).child(newPhotoKey).setValue(photo);
        myRef.child(mContext.getString(R.string.dbname_photos)).child(newPhotoKey).setValue(photo);

    }

    public int getImageCount(DataSnapshot dataSnapshot)
    {
        int count = 0;
        for (DataSnapshot ds: dataSnapshot
                .child(mContext.getString(R.string.dbname_user_photos))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getChildren()) //userID
        {
            count++;
        }

        return count;
    }

    /**
     * update user_account_settings node for current user
     * @param displayName
     * @param status
     * @param description
     * @param phoneNumber
     */
    public void updateUserAccountSettings(String displayName, String status, String description, long phoneNumber) {
        Log.d(TAG, "updateUserAccountSettings: updating user account settings...");

        if (displayName != null) {
            myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                    .child(userID)
                    .child(mContext.getString(R.string.field_display_name))
                    .setValue(displayName);
        }

        if (status != null) {
            myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                    .child(userID)
                    .child(mContext.getString(R.string.field_status))
                    .setValue(status);
        }

        if (description != null) {
            myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                    .child(userID)
                    .child(mContext.getString(R.string.field_description))
                    .setValue(description);
        }

        if (phoneNumber != 0) {
            myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                    .child(userID)
                    .child(mContext.getString(R.string.field_phone_number))
                    .setValue(phoneNumber);
        }
    }

//    public boolean checkIfUsernameExists(String username, DataSnapshot datasnapshot){
//        Log.d(TAG, "checkIfUsernameExists: checking if " + username + " already exists.");
//
//        User user = new User();
//
//        for (DataSnapshot ds: datasnapshot.child(userID).getChildren()){
//            Log.d(TAG, "checkIfUsernameExists: datasnapshot: " + ds);
//
//            user.setUsername(ds.getValue(User.class).getUsername());
//            Log.d(TAG, "checkIfUsernameExists: username: " + user.getUsername());
//
//            if(StringManipulation.expandUsername(user.getUsername()).equals(username)){
//                Log.d(TAG, "checkIfUsernameExists: FOUND A MATCH: " + user.getUsername());
//                return true;
//            }
//        }
//        return false;
//    }

    /**
     * update username in the user's and user_account_settings node
     * @param username
     */
    public void updateUsername(String username)
    {
        Log.d(TAG, "updateUsername: updating the username to " + username);
        myRef.child(mContext.getString(R.string.dbname_users))
                .child(userID)
                .child(mContext.getString(R.string.field_username))
                .setValue(username);

        myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(userID)
                .child(mContext.getString(R.string.field_username))
                .setValue(username);
    }

    /**
     * update email in the user's node
     * @param email
     */
    public void updateEmail(String email)
    {
        Log.d(TAG, "updateUsername: updating the email to " + email);
        myRef.child(mContext.getString(R.string.dbname_users))
                .child(userID)
                .child(mContext.getString(R.string.field_email))
                .setValue(email);
    }

    /**
     * Register a new email and password to Firebase Authentication
     * @param email
     * @param password
     * @param username
     */
    public void registerNewEmail(final String email, String password, final String username){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(mContext, R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();

                        }
                        else if(task.isSuccessful()){
                            //send verification email
                            sendVerificationEmail();

                            userID = mAuth.getCurrentUser().getUid();
                            Log.d(TAG, "onComplete: Authstate changed: " + userID);
                        }

                    }
                });
    }


    public void sendVerificationEmail()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null)
        {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                    {

                    }else
                    {
                        Toast.makeText(mContext, "couldn't sent the verification email", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    /**
     * add information to user nodes
     * add information to user_account_settings node
     * @param email
     * @param username
     * @param description
     * @param status
     * @param profile_photo
     */
    public void addNewUser(String email, String username, String description, String status, String profile_photo){

        User user = new User( StringManipulation.condenseUsername(username),  1,  userID, email  );

        myRef.child(mContext.getString(R.string.dbname_users))
                .child(userID)
                .setValue(user);


        UserAccountSettings settings = new UserAccountSettings(
                description,
                username,
                0,
                0,
                0,
                profile_photo,
                status,
                StringManipulation.condenseUsername(username)
        );

        myRef.child(mContext.getString(R.string.dbname_user_account_settings))
                .child(userID)
                .setValue(settings);

    }

    /**
     * retrieve user information from currently logged in user
     * Database: user_account_settings node
     * @param dataSnapshot
     * @return
     */
    public UserSettings getUserSettings(DataSnapshot dataSnapshot)
    {
        Log.d(TAG, "getUserAccountSettings: retrieving user account settings from firebase...");

        UserAccountSettings settings = new UserAccountSettings();
        User user = new User();

        for (DataSnapshot ds: dataSnapshot.getChildren())
        {

            //user_account_settings node
            if (ds.getKey().equals(mContext.getString(R.string.dbname_user_account_settings)))
            {
                Log.d(TAG, "getUserAccountSettings: dataSnapshot " + ds); //try ds.child(userID)

                try
                {
                    settings.setDisplay_name(ds.child(userID).getValue(UserAccountSettings.class).getDisplay_name());
                    settings.setUsername(ds.child(userID).getValue(UserAccountSettings.class).getUsername());
                    settings.setStatus(ds.child(userID).getValue(UserAccountSettings.class).getStatus());
                    settings.setDescription(ds.child(userID).getValue(UserAccountSettings.class).getDescription());
                    settings.setProfile_photo(ds.child(userID).getValue(UserAccountSettings.class).getProfile_photo());
                    settings.setPosts(ds.child(userID).getValue(UserAccountSettings.class).getPosts());
                    settings.setFollowers(ds.child(userID).getValue(UserAccountSettings.class).getFollowers());
                    settings.setFollowing(ds.child(userID).getValue(UserAccountSettings.class).getFollowing());

                    Log.d(TAG, "getUserAccountSettings: retrieved user_account_settings information " + settings.toString());

                }catch (NullPointerException e)
                {
                    Log.d(TAG, "getUserAccountSettings: Null Pointer exception " + e.getMessage());
                }

            }

            if (ds.getKey().equals(mContext.getString(R.string.dbname_users))) {
                Log.d(TAG, "getUserAccountSettings: dataSnapshot " + ds); //try ds.child(userID)

                try
                {
                    user.setUsername(ds.child(userID).getValue(User.class).getUsername());
                    user.setEmail(ds.child(userID).getValue(User.class).getEmail());
                    user.setPhone_number(ds.child(userID).getValue(User.class).getPhone_number());
                    user.setUser_id(ds.child(userID).getValue(User.class).getUser_id());

                    Log.d(TAG, "getUserAccountSettings: retrieved user information " + user.toString());

                }catch (NullPointerException e)
                {
                    Log.d(TAG, "getUserAccountSettings: Null Pointer Exception " + e.getMessage());
                }
            }
        }

        return new UserSettings(user, settings);
    }

}
