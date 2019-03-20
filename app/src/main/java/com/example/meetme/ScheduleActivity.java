package com.example.meetme;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.example.meetme.BuildingsActivity.KEY;

public class ScheduleActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    public static String result;

    private boolean canSubmit = false;

    private int startOrEnd = -1;

    private EditText mName;

    private TextView mDate;
    private TextView mTime;
    private TextView mStartTime;
    private TextView mEndTime;

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

        // Display user's username on the top right corner of the screen.
        String username = LoginActivity.email;
        final TextView textView = findViewById(R.id.username_textView);
        textView.setText(username);

        Button dateButton = (Button) findViewById(R.id.date_picker_button);
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(), "date picker");
            }
        });

        final Button startTimeButton = (Button) findViewById(R.id.start_time_picker_button);
        startTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startOrEnd = 0;
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });

        Button endTimeButton = (Button) findViewById(R.id.end_time_picker_button);
        endTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startOrEnd = 1;
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });

        Button placeButton = (Button) findViewById(R.id.place_picker_button);
        placeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ScheduleActivity.this, BuildingsActivity.class);
                startActivityForResult(intent, 1);

                TextView addressView = (TextView) findViewById(R.id.place_textView);
                addressView.setText(result);
            }
        });

        Button addButton = (Button) findViewById(R.id.add_event_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(canSubmit) {
                    Map<String, Object> newEvent = new HashMap<>();
                    newEvent.put("Name", mName.getText().toString());
                    newEvent.put("Date", mDate.getText());
                    newEvent.put("Start Time", mStartTime.getText());
                    newEvent.put("End Time", mEndTime.getText());

                    DocumentReference userEvents = db.collection("Events").document(mAuth.getCurrentUser().getEmail());
                    userEvents.collection("uEvents").document(mName.getText().toString()).set(newEvent);

                    Intent intent = new Intent(ScheduleActivity.this, MainPageActivity.class);
                    startActivity(intent);
                }
            }
        });
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
            if(minute == 0) {
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                result = data.getStringExtra(KEY);
            }
            //Write your code if there's no result
        }
    }

    /** Called when the user taps the Back button */
    public void goBackToMainPageActivity(View view) {
        Intent intent = new Intent(ScheduleActivity.this, MainPageActivity.class);
        startActivity(intent);
    }
}