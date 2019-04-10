package com.example.meetme;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class GroupMainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Group";

    private MessageAdapter mAdapter;
    private TextView mGroupNameView;
    private TextView mGroupDes;
    private EditText mMessage;
    private String mGroupId;
    private String mCurrentTime;
    private ListView mListView;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    ArrayList<Message> messages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_main);

        mGroupId = getIntent().getStringExtra("GROUP_ID");
        mGroupNameView = findViewById(R.id.view_group_name);
        mGroupDes = findViewById(R.id.view_group_description);
        mListView = findViewById(R.id.chat_view);
        mMessage = findViewById(R.id.chat_message);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mToolbar = findViewById(R.id.toolbar);

        findViewById(R.id.add_member).setOnClickListener(this);
        findViewById(R.id.create_group_activity).setOnClickListener(this);
        findViewById(R.id.leave_group).setOnClickListener(this);
        findViewById(R.id.send_chat).setOnClickListener(this);

        handleNavigationClickEvents();
        setActionBarDrawerToggle();
        setUpUsernameDisplay();
        getGroupInfo();
        getMessages();
        getUpdates();
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

    private void setList() {
        mAdapter = new MessageAdapter(this, messages);
        compareMessages();
        mListView.setAdapter(mAdapter);
    }

    private void compareMessages() {
        Collections.sort(messages, new Comparator<Message>() {
            @SuppressLint("SimpleDateFormat")
            DateFormat f = new SimpleDateFormat("dd/MM/yyyy     HH:mm");

            public int compare(Message msg1, Message msg2) {
                try {
                    return f.parse(msg1.time).compareTo(f.parse(msg2.time));
                } catch (ParseException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        });
    }

    private void getGroupInfo() {
        db.collection("Groups").document(mGroupId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    mGroupNameView.setText(task.getResult().get("Name").toString());
                    mGroupDes.setText(task.getResult().get("Description").toString());
                    Log.d(TAG, "Info");
                } else {
                    Log.d(TAG, "Info failed");
                    Intent intent = new Intent(GroupMainActivity.this, MainPageActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void getMessages() {
        db.collection("Groups").document(mGroupId).collection("Chat").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot message : task.getResult()) {
                                Message m = new Message(message);
                                messages.add(m);
                            }
                        }
                        setList();
                    }
                });
    }

    private void postChat() {
        getCurrentDateTime();
        if (!mMessage.getText().equals("")) {
            Map<String, Object> newMessage = new HashMap<>();
            newMessage.put("Sender", mAuth.getCurrentUser().getEmail() + ":");
            newMessage.put("Message", mMessage.getText().toString());
            newMessage.put("Time", mCurrentTime);
            db.collection("Groups").document(mGroupId).collection("Chat").document().set(newMessage);
            mMessage.setText("");
        }
    }

    private void leaveGroup() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(GroupMainActivity.this);
        builder.setTitle("");

        builder.setMessage("Are you sure you wanna leave this group?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                db.collection("Groups").document(mGroupId).collection("groupUsers").document(mAuth.getCurrentUser().getEmail())
                        .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Intent intent = new Intent(GroupMainActivity.this, MyGroupsActivity.class);
                        startActivity(intent);
                    }
                });
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

    private void getUpdates() {
        db.collection("Groups").document(mGroupId).collection("Chat")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }

                        if (snapshots != null) {
                            for (DocumentChange dc : snapshots.getDocumentChanges()) {
                                switch (dc.getType()) {
                                    case ADDED:
                                        Message m = new Message(dc.getDocument());
                                        messages.add(m);
                                        mAdapter.notifyDataSetChanged();
                                        break;
                                    case REMOVED:
                                        messages.remove(dc.getDocument());
                                        mAdapter.notifyDataSetChanged();
                                        break;
                                }
                            }
                        }
                    }
                });
    }

    private void getCurrentDateTime() {
        Date date = new Date();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy     HH:mm");
        mCurrentTime = formatter.format(date);
    }

    private void createGroupEvent() {
        Intent intent = new Intent(GroupMainActivity.this, AddEventActivity.class);
        intent.putExtra("GROUP_ID", mGroupId);
        intent.putExtra("GROUP_NAME", mGroupNameView.getText());
        startActivity(intent);
    }

    private void addMember() {
        Intent intent = new Intent(GroupMainActivity.this, AddMembersActivity.class);
        intent.putExtra("GROUP_NAME", mGroupId);
        startActivity(intent);
    }

    private void goHome() {
        Intent intent = new Intent(GroupMainActivity.this, MainPageActivity.class);
        startActivity(intent);
    }

    private void addEvent() {
        Intent intent = new Intent(GroupMainActivity.this, AddEventActivity.class);
        startActivity(intent);
    }

    private void myGroups() {
        Intent intent = new Intent(GroupMainActivity.this, MyGroupsActivity.class);
        startActivity(intent);
    }

    private void openSettings() {
        Intent intent = new Intent(GroupMainActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPostCreate(@android.support.annotation.Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mActionBarDrawerToggle.syncState();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();

        switch (i) {
            case R.id.add_member:
                addMember();
                break;
            case R.id.send_chat:
                postChat();
                break;
            case R.id.leave_group:
                leaveGroup();
                break;
            case R.id.create_group_activity:
                createGroupEvent();
                break;
        }
    }
}