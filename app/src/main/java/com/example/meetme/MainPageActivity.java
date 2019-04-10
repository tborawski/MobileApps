package com.example.meetme;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainPageActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Document";

    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private ListView mListView;
    private EventAdapter mEventAdapter;
    private ArrayAdapter mAdapter;
    private EditText mFilter;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    ArrayList<Event> userEvents = new ArrayList<>();
    ArrayList<String> eventNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        mListView = findViewById(R.id.user_event_listView);
        mFilter = findViewById(R.id.search_event);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mToolbar = findViewById(R.id.toolbar);

        findViewById(R.id.join_button).setOnClickListener(this);
        findViewById(R.id.create_group_button).setOnClickListener(this);

        setSupportActionBar(mToolbar);
        setActionBarDrawerToggle();
        handleNavigationClickEvents();
        setUpUsernameDisplay();
        addEventNames();
        addUserEvent();
        checkEvent();
        searchEvent();
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
        userEmail.setText(mAuth.getCurrentUser().getEmail());
    }

    private void addEventNames() {
        db.collection("Events").document(mAuth.getCurrentUser().getEmail()).collection("uEvents").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                if (document.exists()) {
                                    Log.d(TAG, document.getId());
                                    Object e = document.get("Name");
                                    eventNames.add(e.toString());
                                }
                            }
                        }
                        setEventNameList();
                    }
                });
    }

    private void addUserEvent() {
        db.collection("Events").document(mAuth.getCurrentUser().getEmail()).collection("uEvents").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId());
                                Event e = new Event(document);
                                userEvents.add(e);
                            }
                        }
                        Log.d(TAG, userEvents.toString());
                        setList();
                    }
                });
    }

    private void checkEvent() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainPageActivity.this);

                String name = userEvents.get(position).name;
                String date = userEvents.get(position).date;
                String startTime = userEvents.get(position).startTime;
                String endTime = userEvents.get(position).endTime;
                String loc = userEvents.get(position).loc;

                builder.setMessage("Name: " + name + "\n\nDate: " + date + "\n\nStart Time: " + startTime + "\n\nEnd Time: " + endTime + "\n\nPlace: " + loc);

                builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteEvent(position);
                    }
                });
                builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateEvent();
                    }
                });
                builder.create().show();
            }
        });
    }

    private void deleteEvent(final int pos) {
        db.collection("Events").document(mAuth.getCurrentUser().getEmail())
                .collection("uEvents").document(userEvents.get(pos).id).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                        userEvents.remove(pos);
                        mEventAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
    }

    private void updateEvent() {
        // DO something to allow user to update event.
    }

    private void searchEvent() {
        mFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Do nothing.
            }

            @Override
            public void onTextChanged(final CharSequence s, final int start, int before, int count) {
                mListView.setAdapter(mAdapter);
                (MainPageActivity.this).mAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Do nothing.
            }
        });
    }

    private void compareEvents() {
        Collections.sort(userEvents, new Comparator<Event>() {
            @SuppressLint("SimpleDateFormat")
            DateFormat f = new SimpleDateFormat("EEEE, MMMM dd, YYYY");

            public int compare(Event e1, Event e2) {
                try {
                    return f.parse(e2.date).compareTo(f.parse(e1.date));
                } catch (ParseException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        });
    }

    private void setList() {
        mEventAdapter = new EventAdapter(this, userEvents);
        compareEvents();
        mListView.setAdapter(mEventAdapter);
    }

    private void setEventNameList() {
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, eventNames);
        mListView.setAdapter(mAdapter);
    }

    public void addEvent() {
        Intent intent = new Intent(MainPageActivity.this, AddEventActivity.class);
        startActivity(intent);
    }

    public void myGroups() {
        Intent intent = new Intent(MainPageActivity.this, MyGroupsActivity.class);
        startActivity(intent);
    }

    public void openSettings() {
        Intent intent = new Intent(MainPageActivity.this, SettingsActivity.class);
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
            case R.id.join_button:
                Intent join = new Intent(MainPageActivity.this, JoinActivity.class);
                startActivity(join);
                break;
            case R.id.create_group_button:
                Intent createGroup = new Intent(MainPageActivity.this, CreateGroupActivity.class);
                startActivity(createGroup);
                break;
        }
    }
}
