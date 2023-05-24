package com.example.twitapp.activities;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.twitapp.R;
import com.example.twitapp.util.Constants;
import com.example.twitapp.util.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;

public class SignupActivity extends AppCompatActivity {
    private EditText emailET;
    private TextInputLayout emailTIL;
    private EditText usernameET;
    private TextInputLayout usernameTIL;
    private EditText passwordET;
    private TextInputLayout passwordTIL;
    private View signupProgressLayout;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseFirestore.getInstance();
        emailET = findViewById(R.id.emailET);
        emailTIL = findViewById(R.id.emailTIL);
        passwordET = findViewById(R.id.passwordET);
        passwordTIL = findViewById(R.id.passwordTIL);
        usernameET = findViewById(R.id.usernameET);
        usernameTIL = findViewById(R.id.usernameTIL);
        signupProgressLayout = findViewById(R.id.signupProgressLayout);

        setTextChangeListener(usernameET, usernameTIL);
        setTextChangeListener(emailET, emailTIL);
        setTextChangeListener(passwordET, passwordTIL);

        signupProgressLayout.setOnTouchListener((v, event) -> true);
    }

    public void onSignup(View v) {
        String email, password, username;
        email = String.valueOf(emailET.getText());
        password = String.valueOf(passwordET.getText());
        username = String.valueOf(usernameET.getText());
        boolean proceed = true;
        if (email.isEmpty()) {
            emailTIL.setError("Email is required");
            emailTIL.setErrorEnabled(true);
            proceed = false;
        }
        if (password.isEmpty()) {
            passwordTIL.setError("Password is required");
            passwordTIL.setErrorEnabled(true);
            proceed = false;
        }
        if (password.isEmpty()) {
            usernameTIL.setError("Username is required");
            usernameTIL.setErrorEnabled(true);
            proceed = false;
        }
        if (proceed) {
            signupProgressLayout.setVisibility(View.VISIBLE);
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                User user = new User(email, username, "", new ArrayList<>(), new ArrayList<>());
                                firebaseDatabase.collection(Constants.DATA_USERS)
                                        .document(firebaseAuth.getUid())
                                        .set(user);

                                Toast.makeText(getApplicationContext(), "Signup: success", Toast.LENGTH_SHORT).show();
                                signupProgressLayout.setVisibility(View.GONE);

                                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(intent);
                                finish();
                                Log.d(TAG, "signUpWithEmail:success");
                            } else {
                                signupProgressLayout.setVisibility(View.GONE);
                                Handler handler = new Handler();
                                AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
                                builder.setTitle("Falure");
                                builder.setMessage("Authentication failed.");
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                                int duration = 3000;
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        alertDialog.dismiss();
                                    }
                                }, duration);
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
//
                            }
                        }
                    });
        }

    }


    public void setTextChangeListener(EditText et, TextInputLayout til) {
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                til.setErrorEnabled(false);
            }
        });
    }

    public static Intent newIntent(Context context) {
        return new Intent(context, SignupActivity.class);
    }

    public void goToLogin(View v) {
        startActivity(LoginActivity.newIntent(this));
        finish();
    }

}