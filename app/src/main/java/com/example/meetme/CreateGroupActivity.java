package com.example.meetme;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CreateGroupActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Group";
    private ImageView mImageView;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private EditText mGroupName;
    private EditText mGroupDes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        mAuth = FirebaseAuth.getInstance();
        mGroupName = findViewById(R.id.group_name);
        mGroupDes = findViewById(R.id.group_description);

        // Display user's username on the top right corner of the screen.
        TextView textView = (TextView) findViewById(R.id.username_textView);
        textView.setText(mAuth.getCurrentUser().getEmail());

        mImageView = (ImageView) findViewById(R.id.group_imageView);

        findViewById(R.id.create_group_back_button).setOnClickListener(this);
        findViewById(R.id.create_group_next_button).setOnClickListener(this);
        findViewById(R.id.private_button).setOnClickListener(this);
        findViewById(R.id.upload_group_picture_button).setOnClickListener(this);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if(requestCode == 1 && resultCode == RESULT_OK) {
            Uri selectedImage = imageReturnedIntent.getData();
            mImageView.setImageURI(selectedImage);
        }
    }

    public void validateCreation(){

    }

    public void back(){
        Intent intent = new Intent(CreateGroupActivity.this, MainPageActivity.class);
        startActivity(intent);
    }

    public void toAddUsers(){
        Map<String, Object> description = new HashMap<>();
        description.put("Description", mGroupDes.getText().toString());
        db.collection("Groups").document(mGroupName.getText().toString()).set(description);
        Map<String, Object> users = new HashMap<>();
        users.put("Owner", mAuth.getCurrentUser().getEmail());
        db.collection("Groups").document(mGroupName.getText().toString()).collection("groupUsers").document(mAuth.getCurrentUser().getEmail()).set(users);
        Intent intent = new Intent(CreateGroupActivity.this, AddMembersActivity.class);
        intent.putExtra("GROUP_NAME", mGroupName.getText().toString());
        startActivity(intent);
    }

    public void uploadPicture(){
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, 1);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();

        if(i == R.id.create_group_back_button) {
            back();
        } else if(i == R.id.create_group_next_button) {
            if(TextUtils.isEmpty(mGroupName.getText().toString())) {
                mGroupName.setError("Name must not be blank");
            } else{
                toAddUsers();
            }
        } else if(i == R.id.upload_group_picture_button) {
            uploadPicture();
        }
    }
}
