package com.example.nathalie.myownapp;

/**
 * Created by Nathalie on 15-12-2017.
 */

public class User {

    public String username;
    int score;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, int score) {
        this.username = username;
        this.score = score;

    }

}
