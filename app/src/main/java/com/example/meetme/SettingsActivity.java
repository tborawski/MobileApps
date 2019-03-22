package com.example.meetme;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView mImageView;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();

        // Display user's username on the top right corner of the screen.
        TextView textView = (TextView) findViewById(R.id.username_textView);
        textView.setText(mAuth.getCurrentUser().getEmail());

        mImageView = (ImageView) findViewById(R.id.profile_imageView);

        findViewById(R.id.version_button).setOnClickListener(this);
        findViewById(R.id.upload_button).setOnClickListener(this);
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

        if(i == R.id.settings_back_button) {
            Intent intent = new Intent(SettingsActivity.this, MainPageActivity.class);
            startActivity(intent);
        } else if(i == R.id.sign_out_button) {
            mAuth.signOut();
            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
            startActivity(intent);
        } else if(i == R.id.version_button) {
            Snackbar popUp = Snackbar.make(v, "Version 1.0.0", Snackbar.LENGTH_LONG).setAction("Action", null);
            View view = popUp.getView();
            TextView textView = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            popUp.show();
        } else if(i == R.id.upload_button) {
            Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickPhoto, 1);
        }
    }
}
