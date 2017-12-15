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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class RegisterActivity extends AppCompatActivity {
    private EditText name_input, email_input, password_input;
    private Button register_button, login_button;
    private String name, email, password;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mProgress = new ProgressDialog(this);

        // Get views from XML
        name_input = (EditText) findViewById(R.id.name_email);
        email_input = (EditText) findViewById(R.id.input_email2);
        password_input = (EditText) findViewById(R.id.input_password2);
        login_button = (Button) findViewById(R.id.login_button2);
        register_button = (Button) findViewById(R.id.register_button2);

        // Anonymous listeners because switch - case in listener does not work
        login_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
        register_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startRegister();
            }
        });
    }


        private void startRegister() {
            name = name_input.getText().toString().trim();
            email = email_input.getText().toString().trim();
            password = password_input.getText().toString().trim();

            if (!validEmailAdress(email)) {
                email_input.setError("Not a valid e-mail address");
                startRegister();
            }
            // Make sure user fills in a name, password and e-mail address
            if (TextUtils.isEmpty(name)) {
                name_input.setError("Fill in name");
            } else {
                name_input.setHint("Name");
            }
            if (TextUtils.isEmpty(email)) {
                email_input.setError("Fill in e-mail address");
            } else {
                email_input.setHint("E-mail address");
            }
            if (TextUtils.isEmpty(password)) {
                email_input.setError("Fill in password");
            } else {
                password_input.setHint("Password");

                // Show user app is processing their
                mProgress.setMessage("Registering ...");
                mProgress.show();

                // Register user to database
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            // Let's user have a personal profile picture and name
                            String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            writeNewUser(userID, name);

                            mProgress.dismiss();

                            // Send user to main activity to start the quiz
                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    }
                });
            }
    }

    // Add new user to database
    private void writeNewUser(String userID, String name) {
        User user = new User(name, 0);
        mDatabase.child("users").child(userID).setValue(user);
    }

    // Check if user filled in a e-mail address
    boolean validEmailAdress(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}

