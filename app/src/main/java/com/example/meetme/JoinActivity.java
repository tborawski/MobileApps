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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class JoinActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Join";

    private ListView mListView;
    private ArrayAdapter mAdapter;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    ArrayList<String> groupList = new ArrayList();
    ArrayList<String> docList = new ArrayList();
    ArrayList<String> userGroups = new ArrayList();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        mListView = findViewById(R.id.user_event_listView);

        // Display user's username on the top right corner of the screen.
        TextView textView = (TextView) findViewById(R.id.username_textView);
        textView.setText(mAuth.getCurrentUser().getEmail());

        setList();

        db.collection("Groups").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(final QueryDocumentSnapshot document : task.getResult()){
                        document.getReference().collection("groupUsers").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    boolean found = false;
                                    for(QueryDocumentSnapshot doc : task.getResult()){
                                        if(doc.getId().equals(mAuth.getCurrentUser().getEmail())){
                                            found = true;
                                        }
                                        if(found){
                                            userGroups.add(document.getId());
                                        } else{
                                            if(document.get("isPrivate").toString().equals("OFF")){
                                                groupList.add(document.get("Name").toString());
                                                docList.add(document.getId());
                                                mAdapter.notifyDataSetChanged();
                                            }
                                        }
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(JoinActivity.this);
                builder.setTitle("");

                builder.setMessage("Would you like to join this Group?");
                builder.setPositiveButton("Join", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Map<String, Object> userJoin = new HashMap<>();
                        userJoin.put("Level", "Base");
                        userJoin.put("User", mAuth.getCurrentUser().getEmail());
                        db.collection("Groups").document(docList.get(position)).collection("groupUsers").document(mAuth.getCurrentUser().getEmail()).set(userJoin);
                        groupList.remove(position);
                        docList.remove(position);
                        mAdapter.notifyDataSetChanged();
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


    private void setList() {
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, groupList);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();

        if (i == R.id.join_back_button) {
            Intent intent = new Intent(JoinActivity.this, MainPageActivity.class);
            startActivity(intent);
        }
    }
}
