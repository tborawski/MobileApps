package com.example.meetme;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView mImageView;
    private FirebaseAuth mAuth;

    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();

        TextView textView = findViewById(R.id.username_textView);
        textView.setText(mAuth.getCurrentUser().getEmail());

        mImageView = findViewById(R.id.profile_imageView);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mToolbar = findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);

        findViewById(R.id.settings_back_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.version_button).setOnClickListener(this);
        findViewById(R.id.upload_button).setOnClickListener(this);

        setActionBarDrawerToggle();
        handleNavigationClickEvents();
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

    public void goHome() {
        Intent intent = new Intent(SettingsActivity.this, MainPageActivity.class);
        startActivity(intent);
    }

    public void addEvent() {
        Intent intent = new Intent(SettingsActivity.this, AddEventActivity.class);
        startActivity(intent);
    }

    public void myGroups() {
        Intent intent = new Intent(SettingsActivity.this, MyGroupsActivity.class);
        startActivity(intent);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if(requestCode == 1 && resultCode == RESULT_OK) {
            Uri selectedImage = imageReturnedIntent.getData();
            mImageView.setImageURI(selectedImage);
        }
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
            case R.id.settings_back_button:
                Intent intent = new Intent(SettingsActivity.this, MainPageActivity.class);
                startActivity(intent);
                break;
            case R.id.sign_out_button:
                mAuth.signOut();
                Intent signOut = new Intent(SettingsActivity.this, LoginActivity.class);
                startActivity(signOut);
                break;
            case R.id.version_button:
                Snackbar popUp = Snackbar.make(v, "Version 1.0.0", Snackbar.LENGTH_LONG).setAction("Action", null);
                View view = popUp.getView();
                TextView textView = view.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                popUp.show();
                break;
            case R.id.upload_button:
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, 1);
                break;
        }
    }
}
