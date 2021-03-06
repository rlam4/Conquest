package com.five.conquest;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;

    protected static final String TAG = "Conquest";
    protected static final int GRID_MAX_VALUE = 100;

    public static final long UPDATE_INTERVAL = 1000;
    public static final long FASTEST_UPDATE_INTERVAL = UPDATE_INTERVAL/2;
    protected static int PERMISSION_REQUEST_FINE_LOCATION = 1;
    protected static int SETTINGS_REQUEST_CODE = 1;
    protected static int USER_REQUEST_CODE = 2;

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
    private long startTime;
    private long stopTime;
    private int attackPointsContributed;
    private int defensePointsContributed;

    private Button profileButton;
    private Button playButton;
    private Button chatButton;
    private Button settingsButton;
    private Button levelButton; //Dummy button to place an image.
    private Button sosButton;

    android.view.animation.Animation jiggle;


    //TODO: Replace this default user with actual player settings
    private User player;
    private Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //TODO: Replace this default user
        player = new User("Player 1");
        player.exp = 99;
        player.team = Team.BLUE;

        settings = new Settings();

        gameboard = new GameBoard();
        initializeGameboard();
        checkGPSPermission();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        //Button declarations
        profileButton = (Button) findViewById(R.id.profileButton);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Profile button clicked");
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                intent.putExtra("player", player);
                startActivityForResult(intent, USER_REQUEST_CODE);
//                startActivity(intent);
            }
        });

        chatButton = (Button) findViewById(R.id.chatButton);
        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Chat button clicked");
                Intent intent = new Intent(MainActivity.this, SocialActivity.class);
                startActivity(intent);
            }
        });

        sosButton = (Button) findViewById(R.id.sosButton);
        //sosButton.bringToFront();
        sosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Call For Backup")
                        .setMessage("Do you want to call for backup?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Log.i(TAG, "SOS button clicked");
                                //Add marker to Map
                                MarkerOptions sosMarkerOptions = new MarkerOptions();
                                sosMarkerOptions.position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
                                sosMarkerOptions.title("Need Backup!");
                                sosMarkerOptions.snippet(player.username);
                                Marker marker = mMap.addMarker(sosMarkerOptions);
                                marker.showInfoWindow();

                                //Send message in chat
                                Intent intent = new Intent(MainActivity.this, SocialActivity.class);
                                Bundle b = new Bundle();
                                b.putString("loc", "I need backup at the marked location!");
                                intent.putExtras(b);
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();


            }
        });

        playButton = (Button) findViewById(R.id.playButton);
        playButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(!isTrackingRun) {
                    playButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.stop_xml, 0, 0);
                    playButton.setText("Stop");
                    pathOptions = new PolylineOptions();
                    pathPoints = new ArrayList<LatLng>();
                    path = mMap.addPolyline(pathOptions);
                    startTime = System.currentTimeMillis();
                    attackPointsContributed = 0;
                    defensePointsContributed = 0;
                    isTrackingRun = true;
                } else {
                    playButton.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.play_xml, 0, 0);
                    playButton.setText("Play");
                    isTrackingRun = false;
                    path.remove();
                    stopTime = System.currentTimeMillis();

                    //Updates the gameboard
                    updateGrid();
                    mMap.clear();
                    drawGameBoard();
                    showPostRunAnalysis();
                }
            }
        });

        settingsButton = (Button) findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Chat button clicked");
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                intent.putExtra("settings", settings);
                startActivityForResult(intent, SETTINGS_REQUEST_CODE);
                //startActivity(intent);
            }
        });

        //This is not really a button
        jiggle = AnimationUtils.loadAnimation(this, R.anim.jump);

        updateView();



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
     * Initializes the gameboard based off of a preset text file scenario
     */
    private void initializeGameboard() {
        AssetManager am = getApplicationContext().getAssets();
        Random r = new Random();

        try {
            InputStream in = am.open("territories.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String line = "";
            String[] split;
            ArrayList<Grid> grids = gameboard.getGrids();
            while((line = reader.readLine()) != null) {
                split = line.split(",");
                int pos = Integer.parseInt(split[0]);
                switch(split[1]){
                    case "r":
                        grids.get(pos).setTeam(Team.RED);
                        break;
                    case "g":
                        grids.get(pos).setTeam(Team.GREEN);
                        break;
                    case "b":
                        grids.get(pos).setTeam(Team.BLUE);
                        break;
                }
                //Each grid gets a random value between 5 and 20
                grids.get(pos).setValue(r.nextInt(15) + 5);
            }
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }

    }


    /**
     * Updates the view accordingly
     * Currently only updates the level up
     */
    private void updateView() {
        //Level up adjustment
        levelButton = (Button) findViewById(R.id.button4);
        if(player.points > 0) {
            Log.d("CONQUEST", "Player points at (s) : " + player.points);
            levelButton.setVisibility(View.VISIBLE);
            levelButton.setAnimation(jiggle);
        } else {
            Log.d("CONQUEST", "Player points at (f) : " + player.points);
            levelButton.clearAnimation();
            levelButton.setVisibility(View.INVISIBLE);
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
            gridDrawing.setClickable(true);
            mapGrid.add(gridDrawing);
        }

        //Sets a click listener for the polygons on the map grid so that you can click a grid to see which team owns it and how many points they have
        mMap.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
            @Override
            public void onPolygonClick(Polygon polygon) {
                //Find the corresponding Grid object
                double centralLat = (polygon.getPoints().get(0).latitude + polygon.getPoints().get(3).latitude)/2;
                double centralLon = (polygon.getPoints().get(0).longitude + polygon.getPoints().get(1).longitude)/2;
                for(Grid grid : gameboard.getGrids()) {
                    if(centralLat > grid.getBottomLeft().latitude && centralLat < grid.getTopLeft().latitude
                            && centralLon > grid.getBottomLeft().longitude && centralLon < grid.getBottomRight().longitude) {
                        //Add an invisible marker so that you can show an info window on the map
                        MarkerOptions gridInfoOptions = new MarkerOptions();
                        gridInfoOptions.position(new LatLng(centralLat, centralLon));
                        gridInfoOptions.title(grid.getTeam().toString());
                        gridInfoOptions.snippet("Points: " + grid.getValue());
                        gridInfoOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.transparent_background));
                        Marker newMarker = mMap.addMarker(gridInfoOptions);
                        newMarker.showInfoWindow();
                    }
                }
            }
        });
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
                            defensePointsContributed += GRID_MAX_VALUE - grid.getValue();
                        } else {
                            grid.setValue(grid.getValue() + player.defense);
                            defensePointsContributed += player.defense;
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
                        attackPointsContributed += player.attack;
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
                if(settings.getColorblind()) {
                    green = 255;
                }
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
                if(settings.getColorblind()) {
                    color = Color.YELLOW;
                } else {
                    color = Color.RED;
                }
                break;
            case GREEN:
                color = Color.GREEN;
                break;
            case BLUE:
                color = Color.BLUE;
                break;
            case NEUTRAL:
                color = Color.BLACK;
        }
        return color;
    }

    /**
     * Shows run statistics after hitting the stop button
     */
    private void showPostRunAnalysis() {
        //TODO: Change distance and pace programatically based on user's options of miles or kilometers
        //Distance is in meters initially
        double distance = 0;
        for(int i = 1; i < pathPoints.size(); i++) {
            float[] results = new float[3];
            LatLng start = pathPoints.get(i-1);
            LatLng stop = pathPoints.get(i);
            Location.distanceBetween(start.latitude, start.longitude, stop.latitude, stop.longitude, results);
            distance += results[0];
        }

        //Convert to miles if needed
        if(!settings.getKilometers()) {
            distance = distance/1609.34;
        } else {
            distance = distance/1000;
        }

        long[] readableTime = new long[3];
        long timeElapsedInMillis = stopTime - startTime;
        convertTimeToReadable(timeElapsedInMillis, readableTime);

        double pace = (timeElapsedInMillis/distance)/60000;

        StringBuilder dialogMessage = new StringBuilder();
        dialogMessage.append("Distance: " + (Math.floor(distance * 100) / 100)) ;
        if(settings.getKilometers()) {
            dialogMessage.append(" kilometers\n");
        } else {
            dialogMessage.append(" miles\n");
        }
        dialogMessage.append("Time: ");
        if(readableTime[0] < 10) {
            dialogMessage.append("0");
        }
        dialogMessage.append(readableTime[0] + ":");
        if(readableTime[1] < 10) {
            dialogMessage.append("0");
        }
        dialogMessage.append(readableTime[1] + ":");
        if(readableTime[2] < 10) {
            dialogMessage.append("0");
        }
        dialogMessage.append(readableTime[2] + "\n");
        dialogMessage.append("Pace: " + (Math.floor(pace * 100) / 100));
        if(settings.getKilometers()) {
            dialogMessage.append(" min/kilometer\n\n");
        } else {
            dialogMessage.append(" min/mile\n\n");
        }
        dialogMessage.append("Attack Points Contributed: " + attackPointsContributed + "\n");
        dialogMessage.append("Defense Points Contributed: " + defensePointsContributed + "\n");
        dialogMessage.append("EXP Gained: " + (attackPointsContributed + defensePointsContributed) + "\n");

        //TODO: Update user with the information accordingly.

        player.exp += attackPointsContributed + defensePointsContributed;
        player.distance += (Math.floor(distance * 100) / 100);
        Log.d("Player Stats: ", "EXP : " + player.exp + " | Distance : " + player.distance);

        //EXP/Point/Level Adjustment
        while(player.exp >= 100) {
            player.exp = player.exp - 100;
            player.points++;
            player.level++;
            Log.d("Player Stats: ", "EXP : " + player.exp + " | Distance : " + player.distance);
        }

        final AlertDialog.Builder runAnalysis = new AlertDialog.Builder(this);
        runAnalysis.setTitle("Post-Run Analysis");
        runAnalysis.setMessage(dialogMessage.toString());
        runAnalysis.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //Do nothing
            }
        });

        updateView();
        runAnalysis.show();

    }

    /**
     * Takes in a time elapsed and converts into hours, minutes, and seconds.
     * @param timeElapsed
     * @param results
     */
    private void convertTimeToReadable(long timeElapsed, long[] results) {
        long remainingTime = timeElapsed;
        results[0] = remainingTime/3600000;
        remainingTime -= results[0] * 3600000;
        results[1] = remainingTime/60000;
        remainingTime -= results[1] * 60000;
        results[2] = remainingTime/1000;
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Changes settings when you return from the settings activity
        if(requestCode == SETTINGS_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Settings newSettings = (Settings) data.getSerializableExtra("new settings");
            //If colorblind settings were changed, then redraw the gameboard
            if(newSettings.getColorblind() != settings.getColorblind()) {
                settings = newSettings;
                mMap.clear();
                drawGameBoard();
            } else {
              settings = newSettings;
            }
        }

        if(requestCode == USER_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            player = (User) data.getSerializableExtra("new player");
            //Update the level up
            updateView();
        }
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
            mMap.animateCamera(CameraUpdateFactory.zoomTo(16));
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