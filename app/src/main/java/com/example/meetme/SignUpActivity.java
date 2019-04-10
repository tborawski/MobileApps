package com.example.meetme;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * A sign up screen that offers sign up via first name/last name/email/password.
 */
public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "SignUp";

    private EditText mFirstName;
    private EditText mLastName;
    private EditText mEmailField;
    private EditText mPasswordField;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mFirstName = findViewById(R.id.first_name);
        mLastName = findViewById(R.id.last_name);
        mEmailField = findViewById(R.id.email);
        mPasswordField = findViewById(R.id.password);

        findViewById(R.id.sign_up_button).setOnClickListener(this);
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();
        String first = mFirstName.getText().toString();
        String last = mLastName.getText().toString();

        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else if (!email.contains("@")) {
            mEmailField.setError("Email does not meet requirements");
            valid = false;
        }

        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else if (!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$")) {
            mPasswordField.setError("Password does not meet requirements.");
            valid = false;
        }

        return valid;
    }

    private void createAccount(String email, String password, String first, String last) {
        if (!validateForm()) {
            return;
        }
        final String e = email;
        final String f = first;
        final String l = last;
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    Map<String, Object> addUser = new HashMap<>();
                    addUser.put("Email", e);
                    addUser.put("First", f);
                    addUser.put("Last", l);
                    db.collection("users").document(user.getEmail()).set(addUser);

                    UserProfileChangeRequest update = new UserProfileChangeRequest.Builder().setDisplayName(f + " " + l).build();
                    user.updateProfile(update);
                    Intent intent = new Intent(SignUpActivity.this, MainPageActivity.class);
                    startActivity(intent);
                } else {
                    Toast toast = Toast.makeText(SignUpActivity.this, "Sign up failed or email is already in use.", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();

        switch (i) {
            case R.id.sign_up_button:
                createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString(), mFirstName.getText().toString(), mLastName.getText().toString());
                break;
        }
    }
}
