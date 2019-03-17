package com.example.meetme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        // Display user's username on the top right corner of the screen.
        String username = LoginActivity.email;
        TextView textView = (TextView) findViewById(R.id.username_textView);
        textView.setText(username);
    }

    /** Called when the user taps Join button */
    public void openSettingsActivity(View view) {
        Intent intent = new Intent(MainPageActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    /** Called when the user taps the Schedule button */
    public void openScheduleActivity(View view) {
        Intent intent = new Intent(MainPageActivity.this, ScheduleActivity.class);
        startActivity(intent);
    }

    /** Called when the user taps Join button */
    public void openJoinActivity(View view) {
        Intent intent = new Intent(MainPageActivity.this, JoinActivity.class);
        startActivity(intent);
    }

    /** Called when the user taps the Create Group button */
    public void openCreateGroupActivity(View view) {
        Intent intent = new Intent(MainPageActivity.this, CreateGroupActivity.class);
        startActivity(intent);
    }
}
