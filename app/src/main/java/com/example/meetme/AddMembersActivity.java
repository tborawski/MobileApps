package com.example.meetme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class AddMembersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_members);
    }

    /** Called when the user taps the Back button */
    public void goBackToCreateGroupActivity(View view) {
        Intent intent = new Intent(AddMembersActivity.this, CreateGroupActivity.class);
        startActivity(intent);
    }
}
