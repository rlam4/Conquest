package com.five.conquest;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Ray on 12/1/2016.
 *
 * Modified by Mike on 12/4/2016
 */

//This class is used to simulate the overworld of Conquest

public class GameBoard {

    // defines the coordinates for the grid around College Park, MD
    private LatLng _topLeft = new LatLng(39.000209, -76.950282);
    private LatLng _topRight = new LatLng(39.000209, -76.93504);
    private LatLng _btmLeft = new LatLng(38.980590,-76.93504);
    private LatLng _btmRight = new LatLng(38.980590, -76.950282);

    // amount of grids in the board is determined by gridLength/resolution
    private float _resolution = 500f;

    // Collection of all grids that make up the board
    private ArrayList<Grid> _grids = new ArrayList<>();

    public GameBoard(){
        LatLng tl, tr, bl, br;

        // equal to the side length of one grid
        double step = getStep();

        // iterates over the gameBoard and instantiates all of the grids starting from the top left
        for(double lat = _topLeft.latitude; lat < (_resolution + _topLeft.latitude)-step; lat -= step) {
            for (double lon = _topLeft.longitude; lon < (_resolution + _topLeft.longitude)+step; lon += step) {
                // instantiates all of the corners for the new Grid as new LatLng coordinate pairs
                tl = new LatLng(lat, lon);
                tr = new LatLng(lat, lon + step);
                bl = new LatLng(lat - step, lon);
                br = new LatLng(lat - step, lon + step);

                // adds the new Grid to the ArrayList of Grids
                _grids.add(new Grid(tl, tr, bl, br));
            }
        }
    }

    private double getStep(){
        double lon = _topLeft.longitude;
        double lon2 = _topRight.longitude;
        double lat = _topLeft.latitude;
        double lat2 = _topRight.latitude;

        // distance between two adjacent corners
        double dist = Math.sqrt(Math.pow((lat-lat2), 2) + Math.pow((lon-lon2),2));

        // returns the side lengths of the grid
        return dist/_resolution;
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


}
