package com.five.conquest;

import com.five.conquest.Chat.Team;
import com.google.android.gms.maps.model.LatLng;


/**
 * Created by Mike on 12/4/2016.
 */

//This class defines the grids that create the GameBoard

public class Grid{

    // defines the coordinates for the gameBoard
    private LatLng _topLeft;
    private LatLng _topRight;
    private LatLng _btmLeft;
    private LatLng _btmRight;

    private Team _owner;

    // value is the amount of hold a given team has on a grid
    private float _value;

    public Grid(LatLng topLeft, LatLng topRight, LatLng btmLeft, LatLng btmRight){
        _topLeft = topLeft;
        _topRight = topRight;
        _btmLeft = btmLeft;
        _btmRight = btmRight;
        _value = 0f;
        _owner = Team.NEUTRAL;
    }

    public LatLng getTopLeft(){
        return _topLeft;
    }

    public LatLng getBottomLeft() {
        return _btmLeft;
    }

    public LatLng getBottomRight() {
        return _btmRight;
    }

    public LatLng getTopRight() {
        return _topRight;
    }

    public float getValue() {
        return _value;
    }

    public Team getTeam() { return _owner; }

    public void setValue(float value) {
        _value = value;
    }

    public void setTeam (Team team) {_owner = team; }

}