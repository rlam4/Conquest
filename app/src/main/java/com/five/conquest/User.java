package com.five.conquest;

import com.five.conquest.Chat.Team;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable{

    Integer level;
    Integer exp;
    Integer attack;
    Integer defense;
    Integer points; //Points to allocate
    Double distance; //Will be measured in miles, can change to kilometers, make a function to do that.

    String username;
    String password; //Not yet used, should not be here for security measures.
    Team team; //Not yet used.

    ArrayList<User> friendList;

    //TODO: Figure out how we want to store pictures, for convenience sake we can just use a knight.

    //for testing purposes only at the moment
    public User (String username) {

        this.username = username;

        //Set the stats
        level = 1;
        exp = 0;
        attack = 10;
        defense = 10;
        points = 0;

        distance = 0.0;

    }
}