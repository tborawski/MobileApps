package com.example.meetme;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Document";
    private TextView mUser;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    ArrayList<Event> userEvents = new ArrayList();
    //String[] userEvents = new String[2];
    //int i = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        mAuth = FirebaseAuth.getInstance();

        mUser = findViewById(R.id.username);
        mUser.setText(mAuth.getCurrentUser().getEmail());

        findViewById(R.id.user_sign_out).setOnClickListener(this);
        findViewById(R.id.create_event_button).setOnClickListener(this);

        db.collection("Events").document(mAuth.getCurrentUser().getEmail())
                .collection("uEvents").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                Log.d(TAG, document.getId());
                                Event e = new Event(document);
                                userEvents.add(e);

                            }
                        }
                        setList();
                    }
                });


    }

    private void setList(){
        EventAdapter adapter = new EventAdapter(this, userEvents);
        ListView listView = findViewById(R.id.user_event_list);
        listView.setAdapter(adapter);
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
        }else if(i == R.id.create_event_button) {
            addEvent();
        }
    }
}
