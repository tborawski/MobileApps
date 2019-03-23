package com.example.meetme;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MainPageActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Document";

    private ListView mListView;
    private EditText mFilter;
    private EventAdapter mAdapter;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    ArrayList<Event> userEvents = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        mListView = (ListView) findViewById(R.id.user_event_listView);
        mFilter = (EditText) findViewById(R.id.search_event);

        mAuth = FirebaseAuth.getInstance();

        // Display user's username on the top right corner of the screen.
        TextView textView = (TextView) findViewById(R.id.username_textView);
        textView.setText(mAuth.getCurrentUser().getEmail());

        findViewById(R.id.settings_button).setOnClickListener(this);
        findViewById(R.id.add_schedule_button).setOnClickListener(this);
        findViewById(R.id.join_button).setOnClickListener(this);
        findViewById(R.id.create_group_button).setOnClickListener(this);

        addEvent();
        checkEvent();
        searchEvent();
    }

    private void addEvent() {
        db.collection("Events").document(mAuth.getCurrentUser().getEmail()).collection("uEvents").get()
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
                        Log.d(TAG, userEvents.toString());
                        setList();
                    }
                });
    }

    private void checkEvent() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainPageActivity.this);
                builder.setTitle(userEvents.get(position).name);

                String date = userEvents.get(position).date;
                String startTime = userEvents.get(position).startTime;
                String endTime = userEvents.get(position).endTime;
                String loc = userEvents.get(position).loc;

                builder.setMessage("Date: " + date + "\n\nStart Time: " + startTime + "\n\nEnd Time: " + endTime + "\n\nPlace: " + loc);

                builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.collection("Events").document(mAuth.getCurrentUser().getEmail())
                                .collection("uEvents").document(userEvents.get(position).name).delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                        userEvents.remove(position);
                                        mAdapter.notifyDataSetChanged();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error deleting document", e);
                                    }
                                });
                    }
                });
                builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.create().show();
            }
        });
    }

    private void searchEvent() {
       //Search for a particular Event.
    }

    private void setList() {
        mAdapter = new EventAdapter(this, userEvents);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v){
        int i = v.getId();

        if(i == R.id.settings_button){
            Intent intent = new Intent(MainPageActivity.this, SettingsActivity.class);
            startActivity(intent);
        } else if(i == R.id.add_schedule_button) {
            Intent intent = new Intent(MainPageActivity.this, ScheduleActivity.class);
            startActivity(intent);
        } else if(i == R.id.join_button) {
            Intent intent = new Intent(MainPageActivity.this, JoinActivity.class);
            startActivity(intent);
        } else if(i == R.id.create_group_button) {
            Intent intent = new Intent(MainPageActivity.this, CreateGroupActivity.class);
            startActivity(intent);
        }
    }
}
