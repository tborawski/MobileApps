package com.example.meetme;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ShowMembersActivity extends AppCompatActivity {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String mGroupID;
    private ListView mListView;
    private ArrayAdapter mAdapter;
    private ArrayList<String> members = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_members);
        mGroupID = getIntent().getStringExtra("GROUP_ID");
        mListView = findViewById(R.id.group_member_list);
        getMembers();
    }

    private void getMembers(){
        db.collection("Groups").document(mGroupID).collection("groupUsers").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot user : task.getResult()){
                                String name = user.getId();
                                if(name.contains("@")) {
                                    members.add(user.getId());
                                }
                            }
                        }
                        setList();
                    }
                });
    }
    private void setList(){
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, members);
        mListView.setAdapter(mAdapter);
    }
}
