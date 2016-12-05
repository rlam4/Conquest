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
    protected Boolean isInitialCameraMove = true;

    private ArrayList<Polygon> mapGrid = new ArrayList<Polygon>();

    private ImageButton profileButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

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

        //TODO: Replace hard-coded numbers with the getters from Gameboard
        playableAreaOptions.add(new LatLng(39.000209, -76.950282));
        playableAreaOptions.add(new LatLng(39.000209, -76.93504));
        playableAreaOptions.add(new LatLng(38.980590, -76.93504));
        playableAreaOptions.add(new LatLng(38.98050, -76.950282));
        /*
        playableAreaOptions.add(gameboard.getTopLeft());
        playableAreaOptions.add(gameboard.getTopRight());
        playableAreaOptions.add(gameboard.getBottomLeft());
        playableAreaOptions.add(gameboard.getBottomRight());
         */

        playableAreaOptions.strokeColor(Color.BLACK);
        playableAreaOptions.strokeWidth(7);
        Polygon playableArea = mMap.addPolygon(playableAreaOptions);
        mapGrid.add(playableArea);

        //TODO: Fix this code depending on how GameBoard is implemented
        /*
        //Adds all the gameboard grids to the map
        for(Grid grid : gameboard.getGrids()) {
            PolygonOptions gridOptions = new PolygonOptions();
            gridOptions.add(new LatLng(grid.getTopLeft()));
            gridOptions.add(new LatLng(grid.getTopRight()));
            gridOptions.add(new LatLng(grid.getBottomLeft()));
            gridOptions.add(new LatLng(grid.getBottomRight()));
            gridOptions.strokeWidth(4);
            gridOptions.strokeColor(getBorderColor(grid));
            gridOptions.fillColor(getGridColor(grid));
        }
        */
    }

    //TODO: Fix this code depending on how the enumerated Team class and grid is implemented
    /*
    private int getGridColor(Grid grid) {
        int red = 0;
        int green = 0;
        int blue = 0;
        switch(grid.getTeam()) {
            case "Knights":
                red = 255;
                break;
            case "Pirates":
                green = 255;
                break;
            case "Ninjas":
                blue = 255;
                break;
            case "Neutral":
                return(Color.TRANSPARENT);
        }
        int alpha = Math.round((255/GRID_MAX_VALUE) * grid.getValue());
        return(Color.argb(alpha, red, green, blue));
    }

    private int getBorderColor(Grid grid) {
        int color = 0;
        switch(grid.getTeam()) {
            case "Knights":
                color = Color.RED;
                break;
            case "Pirates":
                color = Color.GREEN;
                break;
            case "Ninjas":
                color = Color.BLUE;
                break;
            case "Neutral":
                color = Color.BLACK;
        }
        return color;
    }
    */

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

        //TODO: Add in position tracking for when player hits play button here

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