package com.example.meetme;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class AddEventActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener, View.OnClickListener{

    private boolean canSubmit = false;
    private EditText mName;
    private TextView mDate;
    private TextView mTime;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        mAuth = FirebaseAuth.getInstance();
        mName = findViewById(R.id.add_event_name);
        mDate = findViewById(R.id.add_date_text);
        mTime = findViewById(R.id.add_time_text);
        findViewById(R.id.date_picker_button).setOnClickListener(this);
        findViewById(R.id.time_picker_button).setOnClickListener(this);
        findViewById(R.id.add_event_back).setOnClickListener(this);
        findViewById(R.id.add_event_submit_button).setOnClickListener(this);


    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth){
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentDateString = DateFormat.getDateInstance().format(c.getTime());
        mDate.setText(currentDateString);
        if(mTime.getText() != ""){
            canSubmit = true;
        } else{
            canSubmit = false;
        }

    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute){
        if(hourOfDay >= 12){
            if(minute == 0){
                mTime.setText(hourOfDay + " : " + minute + "0 PM");
            } else if(minute < 10){
                mTime.setText(hourOfDay + " : " + minute + " PM");
            } else{
                mTime.setText(hourOfDay + " : " + minute + " PM");
            }
        } else {
            if(minute == 0){
                mTime.setText(hourOfDay + " : " + minute + "0 AM");
            } else if(minute < 10) {
                mTime.setText(hourOfDay + " : " + minute + " AM");
            } else{
                mTime.setText(hourOfDay + " : " + minute + " AM");
            }
        }
        if(mDate.getText() != ""){
            canSubmit = true;
        } else {
            canSubmit = false;
        }
    }

    private void goBack(){
        Intent intent = new Intent(AddEventActivity.this, UserActivity.class);
        startActivity(intent);
    }

    private void submitEvent(){
        Map<String, Object> newEvent = new HashMap<>();
        newEvent.put("Name", mName.getText().toString());
        newEvent.put("Date", mDate.getText());
        newEvent.put("Time", mTime.getText());
        DocumentReference userEvents = db.collection("Events").document(mAuth.getCurrentUser().getEmail());
        userEvents.collection("uEvents").document(mName.getText().toString()).set(newEvent);
        Intent intent = new Intent(AddEventActivity.this, UserActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClick(View v){
        int i = v.getId();

        if(i == R.id.date_picker_button){
            DialogFragment datePicker = new DatePickerFragment();
            datePicker.show(getSupportFragmentManager(), "date picker");
        } else if(i == R.id.time_picker_button){
            DialogFragment timePicker = new TimePickerFragment();
            timePicker.show(getSupportFragmentManager(), "time picker");
        } else if(i == R.id.add_event_back){
            goBack();
        } else if(i == R.id.add_event_submit_button){
            if(canSubmit){
                submitEvent();
            }
        }
    }
}
