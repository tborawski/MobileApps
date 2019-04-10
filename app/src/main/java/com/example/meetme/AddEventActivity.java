package com.example.meetme;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.example.meetme.BuildingsActivity.KEY;

public class AddEventActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, View.OnClickListener {

    public final static String TAG = "Schedule";
    public static String result;

    private boolean canSubmit = false;

    private int startOrEnd = -1;

    private EditText mName;
    private TextView mDate;
    private TextView mTime;
    private TextView mPlace;
    private TextView mStartTime;
    private TextView mEndTime;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String mGroupId;
    private String mGroupName;
    private boolean groupEvent = false;
    ArrayList<String> members = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        mName = findViewById(R.id.activity_name);
        mStartTime = findViewById(R.id.start_time_textView);
        mEndTime = findViewById(R.id.end_time_textView);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mToolbar = findViewById(R.id.toolbar);

        findViewById(R.id.start_time_picker_button).setOnClickListener(this);
        findViewById(R.id.end_time_picker_button).setOnClickListener(this);
        findViewById(R.id.date_picker_button).setOnClickListener(this);
        findViewById(R.id.place_picker_button).setOnClickListener(this);
        findViewById(R.id.add_event_button).setOnClickListener(this);

        setSupportActionBar(mToolbar);
        setActionBarDrawerToggle();
        handleNavigationClickEvents();
        setUpUsernameDisplay();
        scheduleGroupEvents();
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

    private void scheduleGroupEvents() {
        if (getIntent().hasExtra("GROUP_ID")) {
            mGroupId = getIntent().getStringExtra("GROUP_ID");
            mGroupName = getIntent().getStringExtra("GROUP_NAME");
            groupEvent = true;
            db.collection("Groups").document(mGroupId).collection("groupUsers").get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot user : task.getResult()) {
                                    members.add(user.getId());
                                }
                            }
                        }
                    });
            Log.d(TAG, mGroupId);
        }
    }

    private void submitEvent() {
        Map<String, Object> newEvent = new HashMap<>();
        newEvent.put("Name", mName.getText().toString());
        newEvent.put("Start Time", mStartTime.getText());
        newEvent.put("End Time", mEndTime.getText());
        newEvent.put("Date", mDate.getText());
        newEvent.put("Place", mPlace.getText());

        if (groupEvent) {
            newEvent.put("Name", mGroupName + ": " + mName.getText());
            for (String mem : members) {
                db.collection("Events").document(mem).collection("uEvents").document().set(newEvent);
            }
        } else {
            DocumentReference userEvents = db.collection("Events").document(mAuth.getCurrentUser().getEmail());
            userEvents.collection("uEvents").document().set(newEvent);
        }

        Intent intent = new Intent(AddEventActivity.this, MainPageActivity.class);
        startActivity(intent);
    }

    public void goHome() {
        Intent intent = new Intent(AddEventActivity.this, MainPageActivity.class);
        startActivity(intent);
    }

    public void myGroups() {
        Intent intent = new Intent(AddEventActivity.this, MyGroupsActivity.class);
        startActivity(intent);
    }

    public void openSettings() {
        Intent intent = new Intent(AddEventActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());

        mDate = findViewById(R.id.date_textView);
        mDate.setText(currentDate);

        canSubmit = mDate.getText() != "";
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (startOrEnd == 0) {
            mTime = findViewById(R.id.start_time_textView);
        } else if (startOrEnd == 1) {
            mTime = findViewById(R.id.end_time_textView);
        }

        if (hourOfDay >= 12) {
            if (minute == 0) {
                mTime.setText(new StringBuilder().append(hourOfDay).append(" : ").append(minute).append("0 PM").toString());
            } else if (minute < 10) {
                mTime.setText(new StringBuilder().append(hourOfDay).append(" : 0").append(minute).append(" PM").toString());
            } else {
                mTime.setText(new StringBuilder().append(hourOfDay).append(" : ").append(minute).append(" PM").toString());
            }
        } else {
            if (hourOfDay < 10) {
                mTime.setText(new StringBuilder().append("0").append(hourOfDay).append(" : ").append(minute).append("0 AM").toString());
            } else if (minute == 0) {
                mTime.setText(new StringBuilder().append(hourOfDay).append(" : ").append(minute).append("0 AM").toString());
            } else if (minute < 10) {
                mTime.setText(new StringBuilder().append(hourOfDay).append(" : 0").append(minute).append(" AM").toString());
            } else {
                mTime.setText(new StringBuilder().append(hourOfDay).append(" : ").append(minute).append(" AM").toString());
            }
        }

        canSubmit = mTime.getText() != "";
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mActionBarDrawerToggle.syncState();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                result = data.getStringExtra(KEY);
                mPlace = findViewById(R.id.place_textView);
                mPlace.setText(result);
            }
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();

        switch (i) {
            case R.id.date_picker_button:
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
                break;
            case R.id.start_time_picker_button:
                startOrEnd = 0;
                DialogFragment sTimePicker = new TimePickerFragment();
                sTimePicker.show(getSupportFragmentManager(), "time picker");
                break;
            case R.id.end_time_picker_button:
                startOrEnd = 1;
                DialogFragment eTimePicker = new TimePickerFragment();
                eTimePicker.show(getSupportFragmentManager(), "time picker");
                break;
            case R.id.place_picker_button:
                Intent pickPlace = new Intent(AddEventActivity.this, BuildingsActivity.class);
                startActivityForResult(pickPlace, 1);
                break;
            case R.id.add_event_button:
                if (canSubmit) {
                    if (TextUtils.isEmpty(mName.getText().toString())) {
                        mName.setError("Name must not be blank.");
                    } else {
                        submitEvent();
                    }
                }
                break;
        }
    }
}