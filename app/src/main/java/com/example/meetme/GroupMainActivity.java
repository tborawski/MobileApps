package com.example.meetme;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class GroupMainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Group";

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    TextView mGroupNameView;
    TextView mGroupDes;
    EditText mMessage;
    String mGroupId;

    ListView mListView;
    private MessageAdapter mAdapter;

    ArrayList<Message> messages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_main);

        mGroupId = getIntent().getStringExtra("GROUP_ID");
        mGroupNameView = findViewById(R.id.view_group_name);
        mGroupDes = findViewById(R.id.view_group_description);
        mListView = findViewById(R.id.chat_view);

        mMessage = findViewById(R.id.chat_message);
        findViewById(R.id.send_chat).setOnClickListener(this);
        findViewById(R.id.leave_group).setOnClickListener(this);
        findViewById(R.id.create_group_activity).setOnClickListener(this);


        getGroupInfo();
        getMessages();
        //getUpdates();


    }

    private void getGroupInfo(){
        db.collection("Groups").document(mGroupId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    mGroupNameView.setText(task.getResult().get("Name").toString());
                    mGroupDes.setText(task.getResult().get("Description").toString());
                    Log.d(TAG, "Info");
                } else {
                    Log.d(TAG, "Info failed");
                    Intent intent = new Intent(GroupMainActivity.this, MainPageActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void getMessages(){
        db.collection("Groups").document(mGroupId).collection("Chat").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot message : task.getResult()){
                                Message m = new Message(message);
                                messages.add(m);
                            }
                        }
                        setList();
                    }
                });
    }
    private void getUpdates(){
        db.collection("Groups").document(mGroupId).collection("Chat")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {

                        if(e != null){
                            return;
                        }

                        for(DocumentChange dc : snapshots.getDocumentChanges()){
                            switch (dc.getType()){
                                case ADDED:
                                    Message m = new Message(dc.getDocument());
                                    messages.add(m);
                                    mAdapter.notifyDataSetChanged();
                                    break;
                                case REMOVED:
                                    messages.remove(dc.getDocument());
                                    mAdapter.notifyDataSetChanged();
                                    break;
                            }
                        }
                    }
                });
    }

    private void setList(){
        mAdapter = new MessageAdapter(this, messages);
        mListView.setAdapter(mAdapter);
    }

    private void postChat(){
        if(!mMessage.getText().equals("")){
            Map<String, Object> newMessage = new HashMap<>();
            newMessage.put("Sender", mAuth.getCurrentUser().getEmail()+ ":");
            newMessage.put("Message", mMessage.getText().toString());
            db.collection("Groups").document(mGroupId).collection("Chat").document().set(newMessage);
            mMessage.setText("");
        }
    }

    private void leaveGroup(){
        db.collection("Groups").document(mGroupId).collection("groupUsers").document(mAuth.getCurrentUser().getEmail())
                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Intent intent = new Intent(GroupMainActivity.this, MainPageActivity.class);
                startActivity(intent);
            }
        });
    }

    private void createGroupEvent(){
        Intent intent = new Intent(GroupMainActivity.this, AddEventActivity.class);
        intent.putExtra("GROUP_ID", mGroupId);
        startActivity(intent);
    }

    @Override
    public void onClick(View v){
        int i = v.getId();

        if(i == R.id.send_chat){
            postChat();
        }else if(i == R.id.leave_group){
            leaveGroup();
        }else if(i == R.id.create_group_activity){
            createGroupEvent();
        }

    }


}
