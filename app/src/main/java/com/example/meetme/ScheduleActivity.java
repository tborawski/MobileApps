package com.example.meetme;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class ScheduleActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{

    private Button mDateButton;
    private Button mTimeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        // Display user's username on the top right corner of the screen.
        String username = LoginActivity.email;
        TextView textView = findViewById(R.id.username_textView);
        textView.setText(username);

        // Display date dialog where user can pick the date.
        mDateButton = (Button) findViewById(R.id.date_picker_button);
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        // Display time dialog where user can pick the time.
        mTimeButton = (Button) findViewById(R.id.time_picker_button);
        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//        TextView textView = (TextView) findViewById(R.id.time_textView);
//        if(hourOfDay >= 12) {
//            if(minute == 0) {
//                textView.setText(hourOfDay + " : " + minute + "0 PM");
//            }
//            else if(minute < 10) {
//                textView.setText(hourOfDay + " : 0" + minute + " PM");
//            } else {
//                textView.setText(hourOfDay + " : " + minute + " PM");
//            }
//        } else {
//            if(minute == 0) {
//                textView.setText(hourOfDay + " : " + minute + "0 AM");
//            }
//            else if(minute < 10) {
//                textView.setText(hourOfDay + " : 0" + minute + " AM");
//            } else {
//                textView.setText(hourOfDay + " : " + minute + " AM");
//            }
//        }
    }

    /** Called when the user taps the Place Picker button */
    public void openBuildingsActivity(View view) {
        Intent intent = new Intent(ScheduleActivity.this, BuildingsActivity.class);
        startActivity(intent);
    }

    /** Called when the user taps the Back button */
    public void openMainPageActivity(View view) {
        Intent intent = new Intent(ScheduleActivity.this, MainPageActivity.class);
        startActivity(intent);
    }
}