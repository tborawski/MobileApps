package com.example.meetme;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class CreateGroupActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        FirebaseAuth auth = FirebaseAuth.getInstance();

        // Display user's username on the top right corner of the screen.
        TextView textView = (TextView) findViewById(R.id.username_textView);
        textView.setText(auth.getCurrentUser().getEmail());

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

    @Override
    public void onClick(View v) {
        int i = v.getId();

        if(i == R.id.create_group_back_button) {
            Intent intent = new Intent(CreateGroupActivity.this, MainPageActivity.class);
            startActivity(intent);
        } else if(i == R.id.create_group_next_button) {
            Intent intent = new Intent(CreateGroupActivity.this, AddMembersActivity.class);
            startActivity(intent);
        } else if(i == R.id.upload_group_picture_button) {
            Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickPhoto, 1);
        } else if(i == R.id.private_button) {
            //Do something to make group private.
        }
    }
}
