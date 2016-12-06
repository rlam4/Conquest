package com.five.conquest;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.five.conquest.Chat.SocialActivity;
import com.five.conquest.Chat.Team;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;

    protected static final String TAG = "Conquest";
    protected static final int GRID_MAX_VALUE = 100;

    public static final long UPDATE_INTERVAL = 1000;
    public static final long FASTEST_UPDATE_INTERVAL = UPDATE_INTERVAL/2;
    protected static int PERMISSION_REQUEST_FINE_LOCATION = 1;

    protected GoogleApiClient googleApiClient;
    protected LocationRequest locationRequest;
    protected Location currentLocation;

    protected GameBoard gameboard;

    protected Boolean isInitialCameraMove = true;
    protected Boolean isTrackingRun = false;

    private ArrayList<Polygon> mapGrid;
    private ArrayList<LatLng> pathPoints;
    private PolylineOptions pathOptions;
    private Polyline path;

    private ImageButton profileButton;
    private ImageButton playButton;
    private ImageButton chatButton;

    //TODO: Replace this default user with actual player settings
    private User player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //TODO: Replace this default user
        player = new User("Default");
        player.attack = 20;
        player.defense = 20;
        player.team = Team.BLUE;

        gameboard = new GameBoard();
        checkGPSPermission();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Connects the buttons to their respective functions
        profileButton = (ImageButton) findViewById(R.id.profileButton);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Profile button clicked");
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        chatButton = (ImageButton) findViewById(R.id.chatButton);
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Chat button clicked");
                Intent intent = new Intent(MainActivity.this, SocialActivity.class);
                startActivity(intent);
            }
        });

        playButton = (ImageButton) findViewById(R.id.playButton);
        playButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(!isTrackingRun) {
                    //Switches the image in the button, starts tracking the run path
                    playButton.setImageResource(R.drawable.stop_xml);
                    pathPoints = new ArrayList<LatLng>();
                    pathOptions = new PolylineOptions();
                    path = mMap.addPolyline(pathOptions);
                    isTrackingRun = true;
                } else {
                    //Switches the image in the button, stops tracking the run path
                    playButton.setImageResource(R.drawable.play_xml);
                    isTrackingRun = false;
                    path.remove();

                    //Updates the gameboard
                    updateGrid();
                    mMap.clear();
                    drawGameBoard();
                }
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //Draws the grids of the gameboard over the map
        drawGameBoard();


        //Checks if you have location permission, then starts the location updates
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }


    /**
     * Draws the boundary of the playable area and the grids over the google map
     */
    private void drawGameBoard() {
        mapGrid = new ArrayList<Polygon>();

        //Adds the boundary of the playable area to the map
        PolygonOptions playableAreaOptions = new PolygonOptions();

        playableAreaOptions.add(gameboard.getTopLeft());
        playableAreaOptions.add(gameboard.getTopRight());
        playableAreaOptions.add(gameboard.getBottomRight());
        playableAreaOptions.add(gameboard.getBottomLeft());

        playableAreaOptions.strokeColor(Color.BLACK);
        playableAreaOptions.strokeWidth(7);
        Polygon playableArea = mMap.addPolygon(playableAreaOptions);
        mapGrid.add(playableArea);

        //Adds all the gameboard grids to the map
        for(Grid grid : gameboard.getGrids()) {
            PolygonOptions gridOptions = new PolygonOptions();
            gridOptions.add(grid.getTopLeft());
            gridOptions.add(grid.getTopRight());
            gridOptions.add(grid.getBottomRight());
            gridOptions.add(grid.getBottomLeft());
            gridOptions.strokeWidth(4);
            gridOptions.strokeColor(getBorderColor(grid));
            gridOptions.fillColor(getGridColor(grid));
            Polygon gridDrawing = mMap.addPolygon(gridOptions);
            mapGrid.add(gridDrawing);
        }
    }

    /**
     * After finishing a run, this function checks every GPS coordinate in the path
     * to see if it is inside a grid and updates the value inside of the grid accordingly
     */
    private void updateGrid() {
        ArrayList<Grid> visited = new ArrayList<Grid>();
        for(LatLng coordinate : pathPoints) {
            for(Grid grid : gameboard.getGrids()) {
                //If there was already a point in the path that was in this grid then don't update twice
                if(visited.contains(grid)) {
                    continue;
                }
                if(coordinate.latitude > grid.getBottomLeft().latitude && coordinate.latitude < grid.getTopLeft().latitude
                        && coordinate.longitude > grid.getBottomLeft().longitude && coordinate.longitude < grid.getBottomRight().longitude) {
                    if(grid.getTeam() == player.team) {
                        //If it belongs to the user's team, then add the player's defense to the points value
                        if(grid.getValue() + player.defense > GRID_MAX_VALUE) {
                            grid.setValue(GRID_MAX_VALUE);
                        } else {
                            grid.setValue(grid.getValue() + player.defense);
                        }
                    } else {
                        if(grid.getValue() - player.attack > 0) {
                            //If the player doesn't have enough attack to conquer it, then subtract points but don't change the grid owner
                            grid.setValue(grid.getValue() - player.attack);
                        } else if (grid.getValue() - player.attack == 0) {
                            //If the player has just enough to make it have 0 points then make it neutral
                            grid.setValue(0);
                            grid.setTeam(Team.NEUTRAL);
                        } else {
                            //If the player has enough attack to convert it to his team then change the grid owner and set the value as the "overkill" amount
                            grid.setValue(player.attack - grid.getValue());
                            grid.setTeam(player.team);
                        }
                    }
                    visited.add(grid);
                    break;
                }
            }
        }
    }

    /**
     * Calculates the color int of the grid according to how many points it has and which team it belongs to
     * @param grid
     * @return
     */
    private int getGridColor(Grid grid) {
        int red = 0;
        int green = 0;
        int blue = 0;
        switch(grid.getTeam()) {
            case RED:
                red = 255;
                break;
            case GREEN:
                green = 255;
                break;
            case BLUE:
                blue = 255;
                break;
            case NEUTRAL:
                return(Color.TRANSPARENT);
        }
        //Changes the transparency based on how strongly the team holds it
        int alpha = Math.round((255/GRID_MAX_VALUE) * grid.getValue());
        return(Color.argb(alpha, red, green, blue));
    }

    /**
     * Returns the appropriate color for the border of the grid, the border is always the team's color and is 100% opaque
     * @param grid
     * @return
     */
    private int getBorderColor(Grid grid) {
        int color = 0;
        switch(grid.getTeam()) {
            case RED:
                color = Color.RED;
                break;
            case GREEN:
                color = Color.GREEN;
                break;
            case BLUE:
                Log.i(TAG, "Setting color to blue");
                color = Color.BLUE;
                break;
            case NEUTRAL:
                color = Color.BLACK;
        }
        return color;
    }


    public void checkGPSPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_FINE_LOCATION);
        }
    }


    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        googleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;

        Log.i(TAG, "Latitude: " + currentLocation.getLatitude() + " Longitude: " + currentLocation.getLongitude());

        LatLng currentPos = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());

        //When initally launching the app, moves the camera to the user's location. We don't want the camera to move whenever you change location
        if(isInitialCameraMove) {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(currentPos));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(20));
            isInitialCameraMove = false;
        }

        //If this creates janky paths, then you can move this code into a timer which checks the current position every so often
        //If the user is currently on a run, then this saves the current location to the path and updates the path line
        if(isTrackingRun) {
            Log.i(TAG, "Adding new point to run path");
            pathPoints.add(currentPos);
            path.setPoints(pathPoints);
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed error code " + result.getErrorCode());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] permissionResults) {
        if(requestCode == PERMISSION_REQUEST_FINE_LOCATION) {
            if(permissionResults.length > 0 && permissionResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "GPS permission granted");
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    if(googleApiClient == null) {
                        buildGoogleApiClient();
                    }
                    mMap.setMyLocationEnabled(true);
                }
            } else {
                Log.i(TAG, "GPS permission denied");
            }
        }
    }
}