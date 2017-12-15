package com.example.nathalie.myownapp;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

public class QuizActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // Check if user is logged in
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            //Go to login
            Intent intent = new Intent(QuizActivity.this, LoginActivity.class);
            startActivity(intent);
        }
        else {
            // Show fragment with question and answers
            QuestionFragment fragment = new QuestionFragment();
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fragment_container, fragment, "answers");
            ft.commit();
        }
    }
}
