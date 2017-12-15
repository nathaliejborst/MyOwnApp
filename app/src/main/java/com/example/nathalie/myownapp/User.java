/*
 * Copyright 2017 Nathalie Borst
 *
 * App implementing the use of a trivia API, which let the user play a quiz game
 * Only obtainable under permission of the creator
 *
 */

package com.example.nathalie.myownapp;

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
