package com.example.meetme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.auth.User;

public class UserActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView mUser;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        mAuth = FirebaseAuth.getInstance();

        mUser = findViewById(R.id.username);
        mUser.setText(mAuth.getCurrentUser().getEmail());

        findViewById(R.id.user_sign_out).setOnClickListener(this);
        findViewById(R.id.create_event_button).setOnClickListener(this);
    }

    private void signOut(){
        mAuth.signOut();
        Intent intent = new Intent(UserActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    private void addEvent(){
        Intent intent = new Intent(UserActivity.this, AddEventActivity.class);
        startActivity(intent);
    }


    @Override
    public void onClick(View v){
        int i = v.getId();

        if(i == R.id.user_sign_out){
            signOut();
        }else if(i == R.id.create_event_button){
            addEvent();
        }
    }
}
