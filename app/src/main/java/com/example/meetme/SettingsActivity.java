package com.example.meetme;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView mImageView;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private Uri mImageUri;
    private Bitmap mBitmap;
    private StorageTask mUploadTask;

    private FirebaseStorage mFirebaseStorage = FirebaseStorage.getInstance();
    private StorageReference mStorageReference = mFirebaseStorage.getReference();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setUpViews();
        setSupportActionBar(mToolbar);
        setActionBarDrawerToggle();
        handleNavigationClickEvents();
        setUpUsernameDisplay();
    }

    private void setUpViews() {
        mImageView = findViewById(R.id.profile_imageView);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mToolbar = findViewById(R.id.toolbar);

        findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.version_button).setOnClickListener(this);
        findViewById(R.id.choose_picture_button).setOnClickListener(this);
        findViewById(R.id.upload_picture_button).setOnClickListener(this);
    }

    private void handleNavigationClickEvents() {
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();

                        int i = menuItem.getItemId();

                        switch (i) {
                            case R.id.home:
                                goHome();
                                break;
                            case R.id.add_event:
                                addEvent();
                                break;
                            case R.id.my_groups:
                                myGroups();
                                break;
                        }
                        return true;
                    }
                });

    }

    private void setActionBarDrawerToggle() {
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
    }

    private void setUpUsernameDisplay() {
        TextView textView = findViewById(R.id.username_textView);
        textView.setText(mAuth.getCurrentUser().getDisplayName());

        NavigationView navigationView = findViewById(R.id.navigation_view);
        View v = navigationView.getHeaderView(0);
        TextView userEmail = v.findViewById(R.id.navigation_bar_email);
        userEmail.setText(mAuth.getCurrentUser().getDisplayName());
    }

    private void setUpProfilePicture() {
        NavigationView navigationView = findViewById(R.id.navigation_view);
        View v = navigationView.getHeaderView(0);
        ImageView profilePicture = v.findViewById(R.id.profile_picture);
        profilePicture.setImageBitmap(mBitmap);
    }

    @SuppressLint("IntentReset")
    public void choosePicture() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickPhoto.setType("image/*");
        pickPhoto.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(pickPhoto, 1);
    }

    private void uploadImage() {
        if (mImageUri != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.show();

            StorageReference fileReference = mStorageReference.child("images/" + UUID.randomUUID().toString());
            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast toast = Toast.makeText(SettingsActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                            toast.show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            mImageUri = imageReturnedIntent.getData();
            try {
                mBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageUri);
                mImageView.setImageBitmap(mBitmap);
                setUpProfilePicture();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void showVersion(View v) {
        Snackbar popUp = Snackbar.make(v, "Version 1.0.0", Snackbar.LENGTH_LONG).setAction("Action", null);
        View view = popUp.getView();
        TextView textView = view.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        popUp.show();
    }

    private void uploadInProgress(View v) {
        Snackbar popUp = Snackbar.make(v, "Upload in progress", Snackbar.LENGTH_LONG).setAction("Action", null);
        View view = popUp.getView();
        TextView textView = view.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        popUp.show();
    }

    private void signOutOfApp() {
        mAuth.signOut();
        Intent signOut = new Intent(SettingsActivity.this, LoginActivity.class);
        startActivity(signOut);
    }

    private void goHome() {
        Intent intent = new Intent(SettingsActivity.this, MainPageActivity.class);
        startActivity(intent);
    }

    private void addEvent() {
        Intent intent = new Intent(SettingsActivity.this, AddEventActivity.class);
        startActivity(intent);
    }

    private void myGroups() {
        Intent intent = new Intent(SettingsActivity.this, MyGroupsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mActionBarDrawerToggle.syncState();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();

        switch (i) {
            case R.id.sign_out_button:
                signOutOfApp();
                break;
            case R.id.version_button:
                showVersion(v);
                break;
            case R.id.choose_picture_button:
                choosePicture();
                break;
            case R.id.upload_picture_button:
                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    uploadInProgress(v);
                } else {
                    uploadImage();
                }
                break;
        }
    }
}
