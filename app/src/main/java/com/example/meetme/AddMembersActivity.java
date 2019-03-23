package com.example.meetme;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class AddMembersActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView mListView;
    private EditText mFilter;
    private ArrayAdapter mAdapter;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    ArrayList<String> members = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_members);

        FirebaseAuth auth = FirebaseAuth.getInstance();

        TextView textView = findViewById(R.id.username_textView);
        textView.setText(auth.getCurrentUser().getEmail());

        mListView = (ListView) findViewById(R.id.add_members_listView);
        mFilter = (EditText) findViewById(R.id.search_filter);

        findViewById(R.id.add_members_back_button).setOnClickListener(this);
        findViewById(R.id.skip_button).setOnClickListener(this);

       addUsersToArray();
       addMember();
       searchUser();
    }

    private void addUsersToArray() {
        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for(QueryDocumentSnapshot document : task.getResult()) {
                        members.add(document.getId());
                    }
                }
                setList();
            }
        });
    }

    private void addMember() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(AddMembersActivity.this);
                builder.setTitle("");

                builder.setMessage("Would you like to add this member to your group?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Add member to group
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

    private void searchUser() {
        mAdapter = new ArrayAdapter<>(this, R.layout.user_list_layout, members);

        mFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Do nothing.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mListView.setAdapter(mAdapter);
                (AddMembersActivity.this).mAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Do nothing
            }
        });
    }

    private void setList() {
        ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, members);
        mListView.setAdapter(adapter);
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
