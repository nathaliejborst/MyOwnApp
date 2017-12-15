/*
 * Copyright 2017 Nathalie Borst
 *
 * App implementing the use of a trivia API, which let the user play a quiz game
 * Only obtainable under permission of the creator
 *
 */

package com.example.nathalie.myownapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import java.util.ArrayList;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;


public class LeaderbordActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    List<String> leaderboardList = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderbord);

        mAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        getFromDB();

        // Initialize list adapter
        ListAdapter theAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                leaderboardList);

        // Get the XML listview
        ListView theListView = (ListView) findViewById(R.id.leaderboard_lv);

        // Set the adapter
        theListView.setAdapter(theAdapter);
    }

    // Retrieve userdata from the database
    public void getFromDB (){

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // get name and score from all users
                for (DataSnapshot childDataSnapshot : dataSnapshot.child("users").getChildren()) {
                    childDataSnapshot.getKey();
                    String name = String.valueOf(childDataSnapshot.child("score").getValue());
                    String score = String.valueOf(childDataSnapshot.child("username").getValue());
                    String leaderboardStats = name + ":   " + score;

                    // Add names and highscores to list
                    leaderboardList.add(leaderboardStats);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        mDatabase.addValueEventListener(postListener);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logout_action) {
            logOut();
        }
        return super.onOptionsItemSelected(item);
    }

    private void logOut() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(LeaderbordActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}
