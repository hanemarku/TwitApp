package com.example.twitapp.activities;
import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.twitapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.checkerframework.checker.nullness.qual.NonNull;

public class LoginActivity extends AppCompatActivity {

    private EditText emailET;
    private TextInputLayout emailTIL;
    private EditText passwordET;
    private TextInputLayout passwordTIL;
    private View loginProgressLayout;
    private FirebaseAuth firebaseAuth;
    private Button loginButton;


    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();
        emailET = findViewById(R.id.emailET);
        emailTIL = findViewById(R.id.emailTIL);
        passwordET = findViewById(R.id.passwordET);
        passwordTIL = findViewById(R.id.passwordTIL);
        loginProgressLayout = findViewById(R.id.loginProgressLayout);

        setTextChangeListener(emailET, emailTIL);
        setTextChangeListener(passwordET, passwordTIL);

        loginButton = findViewById(R.id.buttonLogin);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email, password;
                email = String.valueOf(emailET.getText());
                password = String.valueOf(passwordET.getText());
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
                if (proceed) {
                    loginProgressLayout.setVisibility(View.VISIBLE);
                    firebaseAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "Login successfull", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                                        startActivity(intent);
                                        finish();
                                        Log.d(TAG, "signInWithEmail:success");
//                                        FirebaseUser user = mAuth.getCurrentUser();
//                                        updateUI(user);
                                    } else {
                                        loginProgressLayout.setVisibility(View.GONE);
                                        Handler handler = new Handler();
                                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                        builder.setTitle("Kujdes");
                                        builder.setMessage("Kredencialet nuk jane te sakta.");
                                        AlertDialog alertDialog = builder.create();
                                        alertDialog.show();
                                        int duration = 3000;
                                        handler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                alertDialog.dismiss();
                                            }
                                        }, duration);
//                                        Toast.makeText(getApplicationContext(), "Login failed", Toast.LENGTH_SHORT).show();

                                        // If sign in fails, display a message to the user.
                                        Log.w(TAG, "signInWithEmail:failure", task.getException());
//                                        Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
//                                                Toast.LENGTH_SHORT).show();
//                                        updateUI(null);
                                    }
                                }
                            });
                }
            }
        });

        loginProgressLayout.setOnTouchListener((v, event) -> true);


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


    public void goToSignup(View v) {
        startActivity(SignupActivity.newIntent(this));
        finish();
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        firebaseAuth.addAuthStateListener(firebaseAuthListener);
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        firebaseAuth.removeAuthStateListener(firebaseAuthListener);
//    }

    public static Intent newIntent(Context context) {
        return new Intent(context, LoginActivity.class);
    }

}
