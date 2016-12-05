package com.five.conquest;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

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
    private LatLng _btmLeft = new LatLng(38.980590, -76.950282);
    private LatLng _btmRight = new LatLng(38.980590, -76.93504);

    // amount of grids in the board is determined by gridLength/resolution
    private float _resolution = 20;

    // Collection of all grids that make up the board
    private ArrayList<Grid> _grids = new ArrayList<>();

    public GameBoard(){
        LatLng tl, tr, bl, br;

        // equal to the side length of one grid
        double latStep = getLatStep();
        double lonStep = getLonStep();

        // iterates over the gameBoard and instantiates all of the grids starting from the top left
        for(double lat = _btmLeft.latitude; lat < (_topLeft.latitude-latStep); lat += latStep) {
            for (double lon = _topLeft.longitude; lon < (_topRight.longitude-lonStep); lon += lonStep) {
                // instantiates all of the corners for the new Grid as new LatLng coordinate pairs
                tl = new LatLng(lat + latStep, lon);
                tr = new LatLng(lat + latStep, lon + lonStep);
                bl = new LatLng(lat, lon);
                br = new LatLng(lat, lon + lonStep);

                // adds the new Grid to the ArrayList of Grids
                _grids.add(new Grid(tl, tr, bl, br));
            }
        }
    }

    private double getLatStep(){
        double lat = _topLeft.latitude;
        double lat2 = _btmLeft.latitude;

        // returns the side lengths of the grid
        return (lat-lat2)/_resolution;
    }

    private double getLonStep() {
        double lon = _topRight.longitude;
        double lon2 = _topLeft.longitude;

        // returns the side lengths of the grid
        return (lon-lon2)/_resolution;
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

    public ArrayList<Grid> getGrids () { return _grids; }


}
