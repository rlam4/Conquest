package com.five.conquest;

        import com.google.android.gms.maps.model.LatLng;

        import static com.five.conquest.Grid.Color.GRAY;

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

    // enum for who owns the grid
    public enum Color {RED, GREEN, BLUE, GRAY;}
    private Color _owner;

    // value is the amount of hold a given team has on a grid
    private float _value;

    public Grid(LatLng topLeft, LatLng topRight, LatLng btmLeft, LatLng btmRight){
        _topLeft = topLeft;
        _topRight = topRight;
        _btmLeft = btmLeft;
        _btmRight = btmRight;
        _value = 0f;
        _owner = GRAY;
    }

    public LatLng get_topLeft(){
        return _topLeft;
    }

    public LatLng get_btmLeft() {
        return _btmLeft;
    }

    public LatLng get_btmRight() {
        return _btmRight;
    }

    public LatLng get_topRight() {
        return _topRight;
    }

    public float get_value() {
        return _value;
    }

    public void set_value(float value) {
        _value = value;
    }

}