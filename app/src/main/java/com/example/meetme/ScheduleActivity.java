package com.example.meetme;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.example.meetme.BuildingsActivity.KEY;

public class ScheduleActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, View.OnClickListener {

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

    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        mAuth = FirebaseAuth.getInstance();

        mName = findViewById(R.id.activity_name);
        mStartTime = findViewById(R.id.start_time_textView);
        mEndTime = findViewById(R.id.end_time_textView);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);

        findViewById(R.id.schedule_back_button).setOnClickListener(this);
        findViewById(R.id.date_picker_button).setOnClickListener(this);
        findViewById(R.id.start_time_picker_button).setOnClickListener(this);
        findViewById(R.id.end_time_picker_button).setOnClickListener(this);
        findViewById(R.id.place_picker_button).setOnClickListener(this);
        findViewById(R.id.add_event_button).setOnClickListener(this);

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
                            Intent intent = new Intent(ScheduleActivity.this, MainPageActivity.class);
                            startActivity(intent);
                        } else if (i == R.id.add_event) {
                            Intent intent = new Intent(ScheduleActivity.this, ScheduleActivity.class);
                            startActivity(intent);
                        } else if (i == R.id.my_groups) {
                            //Go to MyGroups Activity.
                        } else if (i == R.id.settings) {
                            Intent intent = new Intent(ScheduleActivity.this, SettingsActivity.class);
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

    private void submitEvent(){
        Map<String, Object> newEvent = new HashMap<>();
        newEvent.put("Name", mName.getText().toString());
        newEvent.put("Start Time", mStartTime.getText());
        newEvent.put("End Time", mEndTime.getText());
        newEvent.put("Date", mDate.getText());
        newEvent.put("Place", mPlace.getText());

        DocumentReference userEvents = db.collection("Events").document(mAuth.getCurrentUser().getEmail());
        userEvents.collection("uEvents").document(mName.getText().toString()).set(newEvent);

        Intent intent = new Intent(ScheduleActivity.this, MainPageActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());

        mDate = (TextView) findViewById(R.id.date_textView);
        mDate.setText(currentDate);

        canSubmit = mDate.getText() != "";
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if(startOrEnd == 0) {
            mTime = (TextView) findViewById(R.id.start_time_textView);
        } else if(startOrEnd == 1) {
            mTime = (TextView) findViewById(R.id.end_time_textView);
        }

        if(hourOfDay >= 12) {
            if(minute == 0) {
                mTime.setText(new StringBuilder().append(hourOfDay).append(" : ").append(minute).append("0 PM").toString());
            }
            else if(minute < 10) {
                mTime.setText(new StringBuilder().append(hourOfDay).append(" : 0").append(minute).append(" PM").toString());
            } else {
                mTime.setText(new StringBuilder().append(hourOfDay).append(" : ").append(minute).append(" PM").toString());
            }
        } else {
            if(hourOfDay < 10) {
                mTime.setText(new StringBuilder().append("0").append(hourOfDay).append(" : ").append(minute).append("0 AM").toString());
            }
            else if(minute == 0) {
                mTime.setText(new StringBuilder().append(hourOfDay).append(" : ").append(minute).append("0 AM").toString());
            }
            else if(minute < 10) {
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
            if(resultCode == Activity.RESULT_OK){
                result = data.getStringExtra(KEY);
                mPlace = (TextView) findViewById(R.id.place_textView);
                mPlace.setText(result);
            }
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();

        if(i == R.id.schedule_back_button) {
            Intent intent = new Intent(ScheduleActivity.this, MainPageActivity.class);
            startActivity(intent);
        } else if(i == R.id.date_picker_button) {
            DialogFragment datePicker = new DatePickerFragment();
            datePicker.show(getSupportFragmentManager(), "date picker");
        } else if(i == R.id.start_time_picker_button) {
            startOrEnd = 0;
            DialogFragment timePicker = new TimePickerFragment();
            timePicker.show(getSupportFragmentManager(), "time picker");
        } else if(i == R.id.end_time_picker_button) {
            startOrEnd = 1;
            DialogFragment timePicker = new TimePickerFragment();
            timePicker.show(getSupportFragmentManager(), "time picker");
        } else if(i == R.id.place_picker_button) {
            Intent intent = new Intent(ScheduleActivity.this, BuildingsActivity.class);
            startActivityForResult(intent, 1);
        } else if(i == R.id.add_event_button) {
            if(canSubmit) {
                submitEvent();
            }
        }
    }
}