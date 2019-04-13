package com.example.meetme;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ShowMembersActivity extends AppCompatActivity {

    private String mGroupID;
    private ListView mListView;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    ArrayList<String> members = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_members);

        setUpViews();
        setSupportActionBar(mToolbar);
        handleNavigationClickEvents();
        setActionBarDrawerToggle();
        setUpUsernameDisplay();
        getMembers();
    }

    private void setUpViews() {
        mGroupID = getIntent().getStringExtra("GROUP_ID");

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mToolbar = findViewById(R.id.toolbar);
        mListView = findViewById(R.id.group_member_list);
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

    private void getMembers() {
        db.collection("Groups").document(mGroupID).collection("groupUsers").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot user : task.getResult()) {
                                String name = user.getId();
                                if (name.contains("@")) {
                                    members.add(user.getId());
                                }
                            }
                        }
                        setList();
                    }
                });
    }

    private void setList() {
        ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, members);
        mListView.setAdapter(adapter);
    }

    private void goHome() {
        Intent intent = new Intent(ShowMembersActivity.this, MainPageActivity.class);
        startActivity(intent);
    }

    private void addEvent() {
        Intent intent = new Intent(ShowMembersActivity.this, AddEventActivity.class);
        startActivity(intent);
    }

    private void myGroups() {
        Intent intent = new Intent(ShowMembersActivity.this, MyGroupsActivity.class);
        startActivity(intent);
    }

    private void openSettings() {
        Intent intent = new Intent(ShowMembersActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPostCreate(@android.support.annotation.Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mActionBarDrawerToggle.syncState();
    }
}
