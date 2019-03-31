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
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MyGroupsActivity extends AppCompatActivity implements View.OnClickListener {

    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private String uEmail = mAuth.getCurrentUser().getEmail();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    ArrayAdapter mAdapter;

    ArrayList<String> groupIds = new ArrayList();
    ArrayList<String> groupNames = new ArrayList();
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_groups);

        findViewById(R.id.my_groups_back_button).setOnClickListener(this);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mToolbar = findViewById(R.id.toolbar);
        mListView = findViewById(R.id.my_group_list);

        setSupportActionBar(mToolbar);

        setActionBarDrawerToggle();
        handleNavigationClickEvents();

        setList();

        db.collection("Groups").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(final QueryDocumentSnapshot group : task.getResult()){
                        group.getReference().collection("groupUsers").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    for(QueryDocumentSnapshot unames : task.getResult()){
                                        if(unames.getId().equals(uEmail)){
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

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MyGroupsActivity.this, GroupMainActivity.class);
                intent.putExtra("GROUP_ID", groupIds.get(position));
                startActivity(intent);
            }
        });
    }

    private void setList(){
        mAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, groupNames);
        mListView.setAdapter(mAdapter);

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

    public void goHome() {
        Intent intent = new Intent(MyGroupsActivity.this, MainPageActivity.class);
        startActivity(intent);
    }

    public void addEvent() {
        Intent intent = new Intent(MyGroupsActivity.this, AddEventActivity.class);
        startActivity(intent);
    }

    public void openSettings() {
        Intent intent = new Intent(MyGroupsActivity.this, SettingsActivity.class);
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
            case R.id.my_groups_back_button:
                Intent intent = new Intent(MyGroupsActivity.this, MainPageActivity.class);
                startActivity(intent);
                break;
        }
    }
}
