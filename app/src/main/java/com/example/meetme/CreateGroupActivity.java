package com.example.meetme;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CreateGroupActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mGroupName;
    private EditText mGroupDes;
    private ToggleButton mPrivateButton;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        setUpViews();
        setSupportActionBar(mToolbar);
        setActionBarDrawerToggle();
        handleNavigationClickEvents();
        setUpUsernameDisplay();
    }

    private void setUpViews() {
        mGroupName = findViewById(R.id.group_name);
        mGroupDes = findViewById(R.id.group_description);
        mPrivateButton = findViewById(R.id.private_button);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mToolbar = findViewById(R.id.toolbar);

        findViewById(R.id.create_group_next_button).setOnClickListener(this);
        findViewById(R.id.private_button).setOnClickListener(this);
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
                            case R.id.settings:
                                openSettings();
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
        NavigationView navigationView = findViewById(R.id.navigation_view);
        View v = navigationView.getHeaderView(0);
        TextView userEmail = v.findViewById(R.id.navigation_bar_email);
        userEmail.setText(mAuth.getCurrentUser().getDisplayName());
    }

    private void toAddUsers() {
        Map<String, Object> docInfo = new HashMap<>();
        docInfo.put("Name", mGroupName.getText().toString());
        docInfo.put("Description", mGroupDes.getText().toString());
        docInfo.put("isPrivate", mPrivateButton.getText());

        DocumentReference newGroupRef = db.collection("Groups").document();
        newGroupRef.set(docInfo);

        Map<String, Object> users = new HashMap<>();
        users.put("Level", "Creator");
        users.put("User", mAuth.getCurrentUser().getEmail());
        newGroupRef.collection("groupUsers").document(mAuth.getCurrentUser().getEmail()).set(users);
        newGroupRef.collection("groupUsers").document().set(users);

        Intent intent = new Intent(CreateGroupActivity.this, AddMembersActivity.class);
        intent.putExtra("GROUP_NAME", newGroupRef.getId());
        startActivity(intent);
    }

    private void goHome() {
        Intent intent = new Intent(CreateGroupActivity.this, MainPageActivity.class);
        startActivity(intent);
    }

    private void addEvent() {
        Intent intent = new Intent(CreateGroupActivity.this, AddEventActivity.class);
        startActivity(intent);
    }

    private void myGroups() {
        Intent intent = new Intent(CreateGroupActivity.this, MyGroupsActivity.class);
        startActivity(intent);
    }

    private void openSettings() {
        Intent intent = new Intent(CreateGroupActivity.this, SettingsActivity.class);
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
            case R.id.create_group_next_button:
                if (TextUtils.isEmpty(mGroupName.getText().toString())) {
                    mGroupName.setError("Name must not be blank.");
                } else {
                    toAddUsers();
                }
                break;
            case R.id.private_button:
                //Do something to make group private.
                break;
        }
    }
}
