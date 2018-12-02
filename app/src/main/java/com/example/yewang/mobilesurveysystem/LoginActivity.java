package com.example.yewang.mobilesurveysystem;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "Group-project";

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    //private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
       // populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);


        mAuth = FirebaseAuth.getInstance();

        Button mRegisterButton = (Button) findViewById(R.id.register_button);
        mRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRegister();
            }
        });
    }

    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mEmailView.getText().length() < 1) {
            Toast.makeText(LoginActivity.this, "Please enter an email address.", Toast.LENGTH_SHORT).show();
        } else if (mPasswordView.getText().length() < 1) {
            Toast.makeText(LoginActivity.this, "Please enter a password.", Toast.LENGTH_SHORT).show();
        } else {

            // Store values at the time of the login attempt.
            final String email = mEmailView.getText().toString();
            final String password = mPasswordView.getText().toString();


            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Log.i(TAG, "successful log in!");
                        Intent result = new Intent();
                        result.putExtra("user", email);
                        setResult(RESULT_OK, result);
                        finish();
                    } else {
                        Log.i(TAG, "Incorrect email/password");
                        Toast.makeText(LoginActivity.this, "Incorrect email/password.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void attemptRegister() {

        if (mEmailView.getText().length() < 1) {
            Toast.makeText(LoginActivity.this, "Please enter an email address.", Toast.LENGTH_SHORT).show();
        } else if (mPasswordView.getText().length() < 1) {
            Toast.makeText(LoginActivity.this, "Please enter a password.", Toast.LENGTH_SHORT).show();
        } else {
            final String email = mEmailView.getText().toString();
            final String password = mPasswordView.getText().toString();
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Log.i(TAG, "successful register!");
                        Toast.makeText(LoginActivity.this, "Successfully registered!", Toast.LENGTH_SHORT).show();
                        mAuth.signInWithEmailAndPassword(email, password);
                        Log.i(TAG, "successful log in!");
                        Intent result = new Intent();
                        result.putExtra("user", email);
                        setResult(RESULT_OK, result);
                        finish();
                        //updateUI(user);
                    } else {
                        Toast.makeText(LoginActivity.this, "Incorrect email/password.", Toast.LENGTH_SHORT).show();
                        //Toast.makeText(LoginActivity.this, "YO!!!!.", Toast.LENGTH_SHORT).show();
                        //updateUI(null);
                    }

                }
            });
        }
    }
}

