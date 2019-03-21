package com.example.meetme;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class JoinActivity extends AppCompatActivity {

    private static final String TAG = "Document";

    private ListView mListView;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    List<String> userEvents = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        mListView = findViewById(R.id.user_event_listView);

        // Display user's username on the top right corner of the screen.
        String username = LoginActivity.email;
        TextView textView = (TextView) findViewById(R.id.username_textView);
        textView.setText(username);

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
        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.user_event_listview, userEvents);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
                builder.setTitle(userEvents.get(position).toString());

                builder.setMessage("Would you like to join this Group?");
                builder.setPositiveButton("Join", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Do something to join group and add event to main page.
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        });
    }

    /** Called when the user taps the Back button */
    public void goBackToMainPageActivity(View view) {
        Intent intent = new Intent(JoinActivity.this, MainPageActivity.class);
        startActivity(intent);
    }
}
