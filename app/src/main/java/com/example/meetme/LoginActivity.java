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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "EmailPassword";
    public static String email = "";

    private EditText mEmailField;
    private EditText mPasswordField;
    private TextView mSuccess;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmailField = findViewById(R.id.email);
        mPasswordField = findViewById(R.id.password);
        mSuccess = findViewById(R.id.approved);

        findViewById(R.id.sign_in).setOnClickListener(this);
        findViewById(R.id.sign_up).setOnClickListener(this);

        mSuccess.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart(){
        super.onStart();
    }

    private void signIn(String email, String password){
        Log.d(TAG, "signIn: " + email);
        if(!validateForm()){
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>(){
            @Override
            public void onComplete(@NonNull Task<AuthResult> task){
                if(task.isSuccessful()){
                    Log.d(TAG, "Sign in success");
                    FirebaseUser user = mAuth.getCurrentUser();
                } else {
                    Log.w(TAG, "Sign in failure", task.getException());
                    Toast toast = Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 150);
                    toast.show();
                }
            }
        });
    }

    private void createAccount(String email, String password){
        Log.d(TAG, "CreateAccount: " + email);
        if(!validateForm()){
            return;
        }
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "Create user success");
                    FirebaseUser user = mAuth.getCurrentUser();
                }else {
                    Log.w(TAG, "Create user failure", task.getException());
                    Toast toast = Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 150);
                    toast.show();
                }
            }
        });
    }

    private void signOut(){
        mAuth.signOut();
    }
    private boolean validateForm(){
        boolean valid = true;

        email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();

        // Check if email is valid.
        if(TextUtils.isEmpty(email)){
            mEmailField.setError("Required.");
            valid = false;
        } else if(!email.contains("@")) {
            mEmailField.setError("Email does not meet requirements.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        // Check if password is valid.
        if(TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        }
        else if(!password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$")) {
            mPasswordField.setError("Password does not meet requirements.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }
        return valid;
    }

    @Override
    public void onClick(View v){
        int i = v.getId();
        if(i == R.id.sign_in){
            signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
            Intent intent = new Intent(LoginActivity.this, MainPageActivity.class);
            startActivity(intent);
        } else if(i == R.id.sign_up){
            createAccount(mEmailField.getText().toString(), mPasswordField.getText().toString());
            Intent intent = new Intent(LoginActivity.this, MainPageActivity.class);
            startActivity(intent);
        } else if(i == R.id.sign_out_button){
            signOut();
        }
    }

}