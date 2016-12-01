package com.five.conquest;

import java.util.ArrayList;

public class User {

    int level;
    int exp;
    int attack;
    int defense;
    int points; //Points to allocate
    Double distance; //Will be measured in miles

    String username;
    String password; //Not yet used, should not be here for security measures.
    String team; //Not yet used.

    ArrayList<User> friendList;

    //TODO: Figure out how we want to store pictures, for convenience sake we can just use a knight.

    //for testing purposes only at the moment
    public User (String username) {

        this.username = username;

        //Set the stats
        level = 2;
        exp = 0;
        attack = 0;
        defense = 0;
        points = 1;

        distance = 1.0;

    }
}
