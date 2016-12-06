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

import com.five.conquest.Chat.SocialActivity;
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

    private ArrayList<Polygon> mapGrid = new ArrayList<Polygon>();
    private ArrayList<LatLng> pathPoints = new ArrayList<LatLng>();
    private PolylineOptions pathOptions;
    private Polyline path;

    private ImageButton profileButton;
    private ImageButton playButton;
    private ImageButton chatButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        gameboard = new GameBoard();
        checkGPSPermission();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

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
                    playButton.setImageResource(R.drawable.stop_xml);
                    pathOptions = new PolylineOptions();
                    path = mMap.addPolyline(pathOptions);
                    isTrackingRun = true;
                } else {
                    playButton.setImageResource(R.drawable.play_xml);
                    isTrackingRun = false;
                    path.remove();

                    //TODO: Implement the capturing of territories by checking the list of LatLng
                }
            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        drawGameBoard();

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }
    }




    private void drawGameBoard() {
        //Adds the boundary of the playable area to the map
        PolygonOptions playableAreaOptions = new PolygonOptions();

        //TODO: Delete this code
        /*
        playableAreaOptions.add(new LatLng(39.000209, -76.950282));
        playableAreaOptions.add(new LatLng(39.000209, -76.93504));
        playableAreaOptions.add(new LatLng(38.980590, -76.93504));
        playableAreaOptions.add(new LatLng(38.98050, -76.950282));
        */

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
        int alpha = Math.round((255/GRID_MAX_VALUE) * grid.getValue());
        return(Color.argb(alpha, red, green, blue));
    }

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

        if(isInitialCameraMove) {
            mMap.moveCamera(CameraUpdateFactory.newLatLng(currentPos));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(20));
            isInitialCameraMove = false;
        }

        //If this creates janky paths, then you can move this code into a timer which checks the current position every so often
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