package com.example.meetme;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {

    private TextView mTextView;
    private ImageView mImageView;
    private View mView;
    private Snackbar mPopUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Display user's username on the top right corner of the screen.
        String username = LoginActivity.email;
        TextView textView = (TextView) findViewById(R.id.username_textView);
        textView.setText(username);

        mImageView = (ImageView) findViewById(R.id.profile_imageView);

        Button version = (Button) findViewById(R.id.version_button);
        version.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopUp = Snackbar.make(v, "Version 1.0.0", Snackbar.LENGTH_LONG).setAction("Action",null);
                mView = mPopUp.getView();
                mTextView = (TextView) mView.findViewById(android.support.design.R.id.snackbar_text);
                mTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                mPopUp.show();
            }
        });

        Button uploadPicture = (Button) findViewById(R.id.upload_button);
        uploadPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, 1);
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if(requestCode == 1 && resultCode == RESULT_OK) {
            Uri selectedImage = imageReturnedIntent.getData();
            mImageView.setImageURI(selectedImage);
        }
    }

    /** Called when the user taps the Back button */
    public void goBackToMainPageActivity(View view) {
        Intent intent = new Intent(SettingsActivity.this, MainPageActivity.class);
        startActivity(intent);
    }

    /** Called when the user taps the Sign Out button */
    public void goBackToLoginActivity(View view) {
        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}
