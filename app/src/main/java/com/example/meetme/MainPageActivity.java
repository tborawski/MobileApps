package com.example.meetme;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

public class MainPageActivity extends AppCompatActivity {

    private static final String TAG = "Document";

    private ListView mListView;
    private ArrayAdapter adapter;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    ArrayList userEvents = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        EditText editText = (EditText) findViewById(R.id.search_text);

        mListView = (ListView) findViewById(R.id.user_event_listView);

        // Display user's username on the top right corner of the screen.
        String username = LoginActivity.email;
        TextView textView = (TextView) findViewById(R.id.username_textView);
        textView.setText(username);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Do nothing.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //Do nothing.
            }
        });

        db.collection("Events").document(mAuth.getCurrentUser().getEmail()).collection("uEvents").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                Log.d(TAG, document.getId());
                                userEvents.add(document.getId());

                            }
                        }
                        Log.d(TAG, userEvents.toString());
                        setList();
                    }
                });
    }

    private void setList() {
        adapter = new ArrayAdapter<String>(this, R.layout.user_event_listview, userEvents);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainPageActivity.this);
                builder.setTitle(userEvents.get(position).toString());

                String mStart = "";
                String mEnd = "";
                String mDate = "";
                String mPlace = "";

                builder.setMessage("Start Time: " + mStart +"\nEnd Time: " + mEnd + "\nDate: " + mDate + "\nPlace: " + mPlace);
                builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.collection("Events").document(mAuth.getCurrentUser().getEmail())
                                .collection("uEvents").document(userEvents.get(position).toString()).delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                        userEvents.remove(position);
                                        adapter.notifyDataSetChanged();
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
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        });
    }

    /** Called when the user taps Settings button */
    public void goToSettingsActivity(View view) {
        Intent intent = new Intent(MainPageActivity.this, SettingsActivity.class);
        startActivity(intent);
    }

    /** Called when the user taps the "+" button */
    public void goToScheduleActivity(View view) {
        Intent intent = new Intent(MainPageActivity.this, ScheduleActivity.class);
        startActivity(intent);
    }

    /** Called when the user taps Join button */
    public void goToJoinActivity(View view) {
        Intent intent = new Intent(MainPageActivity.this, JoinActivity.class);
        startActivity(intent);
    }

    /** Called when the user taps the Create Group button */
    public void goToCreateGroupActivity(View view) {
        Intent intent = new Intent(MainPageActivity.this, CreateGroupActivity.class);
        startActivity(intent);
    }
}
