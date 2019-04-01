package com.example.meetme;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class JoinActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView mListView;

    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private ArrayAdapter mAdapter;

    ArrayList<String> groupList = new ArrayList<>();
    ArrayList<String> docList = new ArrayList<>();
    ArrayList<String> userGroups = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        mListView = findViewById(R.id.user_event_listView);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mToolbar = findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);

        setList();

        db.collection("Groups").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(final QueryDocumentSnapshot document : task.getResult()){
                        document.getReference().collection("groupUsers").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    boolean found = false;
                                    for(QueryDocumentSnapshot doc : task.getResult()){
                                        if(doc.getId().equals(mAuth.getCurrentUser().getEmail())){
                                            found = true;
                                        }
                                        if(found){
                                            userGroups.add(document.getId());
                                        } else{
                                            if(document.get("isPrivate").toString().equals("OFF")){
                                                groupList.add(document.get("Name").toString());
                                                docList.add(document.getId());
                                                mAdapter.notifyDataSetChanged();
                                            }
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
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
                builder.setTitle("");

                builder.setMessage("Would you like to join this Group?");
                builder.setPositiveButton("Join", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Map<String, Object> userJoin = new HashMap<>();
                        userJoin.put("Level", "Base");
                        userJoin.put("User", mAuth.getCurrentUser().getEmail());
                        db.collection("Groups").document(docList.get(position)).collection("groupUsers").document(mAuth.getCurrentUser().getEmail()).set(userJoin);
                        groupList.remove(position);
                        docList.remove(position);
                        mAdapter.notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        });

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

    private void setList() {
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, groupList);
        mListView.setAdapter(mAdapter);
    }

    public void goHome() {
        Intent intent = new Intent(JoinActivity.this, MainPageActivity.class);
        startActivity(intent);
    }

    public void addEvent() {
        Intent intent = new Intent(JoinActivity.this, AddEventActivity.class);
        startActivity(intent);
    }

    public void myGroups() {
        Intent intent = new Intent(JoinActivity.this, MyGroupsActivity.class);
        startActivity(intent);
    }

    public void openSettings() {
        Intent intent = new Intent(JoinActivity.this, SettingsActivity.class);
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
            // Just in case we still need onClick.
        }
    }
}
