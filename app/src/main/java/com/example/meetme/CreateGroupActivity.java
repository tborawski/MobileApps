package com.example.meetme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class CreateGroupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        // Display user's username on the top right corner of the screen.
        String username = LoginActivity.email;
        TextView textView = (TextView) findViewById(R.id.username_textView);
        textView.setText(username);
    }

    /** Called when the user taps the Back button */
    public void openMainPageActivity(View view) {
        Intent intent = new Intent(CreateGroupActivity.this, MainPageActivity.class);
        startActivity(intent);
    }

    /** Called when the user taps the Next button */
    public void openAddMembersActivity(View view) {
        Intent intent = new Intent(CreateGroupActivity.this, AddMembersActivity.class);
        startActivity(intent);
    }
}
