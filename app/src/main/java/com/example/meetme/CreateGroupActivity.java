package com.example.meetme;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

public class CreateGroupActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView mImageView;

    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        mImageView = (ImageView) findViewById(R.id.group_imageView);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);

        findViewById(R.id.create_group_back_button).setOnClickListener(this);
        findViewById(R.id.create_group_next_button).setOnClickListener(this);
        findViewById(R.id.private_button).setOnClickListener(this);
        findViewById(R.id.upload_group_picture_button).setOnClickListener(this);

        setActionBarDrawerToggle();
        handleNavigationClickEvents();
    }

    private void handleNavigationClickEvents() {
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        int i = menuItem.getItemId();

                        if (i == R.id.home) {
                            Intent intent = new Intent(CreateGroupActivity.this, MainPageActivity.class);
                            startActivity(intent);
                        } else if (i == R.id.add_event) {
                            Intent intent = new Intent(CreateGroupActivity.this, ScheduleActivity.class);
                            startActivity(intent);
                        } else if (i == R.id.my_groups) {
                            //Go to MyGroups Activity.
                        } else if (i == R.id.settings) {
                            Intent intent = new Intent(CreateGroupActivity.this, SettingsActivity.class);
                            startActivity(intent);
                        }
                        return true;
                    }
                });

    }

    private void setActionBarDrawerToggle() {
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if (requestCode == 1 && resultCode == RESULT_OK) {
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

        if (i == R.id.create_group_back_button) {
            Intent intent = new Intent(CreateGroupActivity.this, MainPageActivity.class);
            startActivity(intent);
        } else if (i == R.id.create_group_next_button) {
            Intent intent = new Intent(CreateGroupActivity.this, AddMembersActivity.class);
            startActivity(intent);
        } else if (i == R.id.upload_group_picture_button) {
            Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickPhoto, 1);
        } else if (i == R.id.private_button) {
            //Do something to make group private.
        }
    }
}
