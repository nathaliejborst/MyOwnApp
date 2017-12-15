package com.example.nathalie.myownapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class LoginActivity extends AppCompatActivity {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mDatabase = database.getReference();
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private EditText email_input, password_input;
    private Button register_button, login_button;
    private ProgressDialog mProgress;

    private FirebaseAuth.AuthStateListener mAuthListener;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mProgress = new ProgressDialog(this);

        email_input = (EditText)findViewById(R.id.input_email);
        password_input = (EditText)findViewById(R.id.input_password);
        register_button = (Button)findViewById(R.id.register_button);
        login_button = (Button)findViewById(R.id.login_button);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // Redirect if the user is already logged in
                if(firebaseAuth.getCurrentUser() != null) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        };

        // Anonymous listeners because switch - case in listener does not work
        login_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startSignIn();
            }
        });
        register_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    public void startSignIn() {
        String email = email_input.getText().toString();
        String password = password_input.getText().toString();

        // Make sure user fills in a password and e-mail address
        if(TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {
            email_input.setError("Fill in e-mail address");
            password_input.setError("Fill in password");
        } else {
            mProgress.setMessage("Logging in ...");
            mProgress.show();

            // If both fields are not empty, sign in user
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    // Shows error when login is not succesful
                    if(task.isSuccessful()) {
                        mProgress.dismiss();
                        Toast.makeText(getApplicationContext(), "Welcome", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);

                    } else {
                        mProgress.dismiss();
                        Toast.makeText(getApplicationContext(), "This user does not exist. Please try again", Toast.LENGTH_SHORT).show();
                        email_input.setText("");
                        password_input.setText("");
                    }
                }
            });
        }
    }

}