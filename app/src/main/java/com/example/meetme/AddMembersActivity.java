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
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddMembersActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Memebers";
    private ListView mListView;
    private String groupName;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    ArrayList<String> members = new ArrayList();
    ArrayList<String> currentMembers = new ArrayList();
    private ArrayAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_members);

        mListView = (ListView) findViewById(R.id.add_members_listView);
        EditText filter = (EditText) findViewById(R.id.search_filter);
        groupName = getIntent().getStringExtra("GROUP_NAME");

        findViewById(R.id.add_members_back_button).setOnClickListener(this);
        findViewById(R.id.skip_button).setOnClickListener(this);

        db.collection("Groups").document(groupName).collection("groupUsers").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot cUser : task.getResult()){
                                currentMembers.add(cUser.getId());
                            }
                            getNonMembers();
                        }
                    }
                });


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddMembersActivity.this);
                builder.setTitle("");
                final String name = members.get(position);
                builder.setMessage("Would you like to add this member to your group?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Map<String, Object> newMember = new HashMap<>();
                        newMember.put("Level", "Base");
                        newMember.put("User", name);
                        db.collection("Groups").document(groupName).collection("groupUsers").document(name).set(newMember);
                        members.remove(position);
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
        mAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, members);
        mListView.setAdapter(mAdapter);
    }

    private void getNonMembers(){
        //Add users to arrayList
        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for(QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId());
                        if(!currentMembers.contains(document.getId())){
                            members.add(document.getId());
                        }
                    }
                }
                setList();
            }
        });
    }

    public void onClick(View v) {
        int i = v.getId();

        if(i == R.id.add_members_back_button) {
            Intent intent = new Intent(AddMembersActivity.this, CreateGroupActivity.class);
            startActivity(intent);
        } else if(i == R.id.skip_button) {
            //Do something to skip adding members
        }
    }
}
