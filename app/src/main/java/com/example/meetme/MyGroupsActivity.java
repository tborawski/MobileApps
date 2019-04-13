package com.example.meetme;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MyGroupsActivity extends AppCompatActivity {

    private ArrayAdapter mAdapter;
    private ListView mListView;
    private EditText mFilter;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String uEmail = mAuth.getCurrentUser().getEmail();

    ArrayList<String> groupIds = new ArrayList<>();
    ArrayList<String> groupNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_groups);

        setUpViews();
        setSupportActionBar(mToolbar);
        setActionBarDrawerToggle();
        handleNavigationClickEvents();
        setUpUsernameDisplay();
        setList();
        setUpGroup();
        goToGroupChat();
        searchGroup();
    }

    private void setUpViews() {
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mToolbar = findViewById(R.id.toolbar);
        mListView = findViewById(R.id.my_group_list);
        mFilter = findViewById(R.id.my_groups_search);
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

    private void setUpGroup() {
        db.collection("Groups").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (final QueryDocumentSnapshot group : task.getResult()) {
                        group.getReference().collection("groupUsers").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot unames : task.getResult()) {
                                        if (unames.getId().equals(uEmail)) {
                                            groupNames.add(group.get("Name").toString());
                                            groupIds.add(group.getId());
                                            mAdapter.notifyDataSetChanged();
                                        }
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    private void goToGroupChat() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MyGroupsActivity.this, GroupMainActivity.class);
                intent.putExtra("GROUP_ID", groupIds.get(position));
                startActivity(intent);
            }
        });
    }

    private void searchGroup() {
        mFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Do nothing.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mListView.setAdapter(mAdapter);
                (MyGroupsActivity.this).mAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Do nothing.
            }
        });
    }

    private void setList() {
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, groupNames);
        mListView.setAdapter(mAdapter);
    }

    private void goHome() {
        Intent intent = new Intent(MyGroupsActivity.this, MainPageActivity.class);
        startActivity(intent);
    }

    private void addEvent() {
        Intent intent = new Intent(MyGroupsActivity.this, AddEventActivity.class);
        startActivity(intent);
    }

    private void openSettings() {
        Intent intent = new Intent(MyGroupsActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mActionBarDrawerToggle.syncState();
    }
}
