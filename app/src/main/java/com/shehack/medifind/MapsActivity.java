package com.shehack.medifind;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.shehack.medifind.ui.login.LoginActivity;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener,
        GoogleMap.OnMyLocationClickListener, GoogleMap.OnMarkerDragListener,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private GoogleMap googleMap;
    private LatLng currentLocation = null;
    private UiSettings uiSettings;
    private boolean locationPermissionDenied = true;
    private LatLng gps_location = null;
    boolean selectedLocation = false;
    private Marker currentLocationMarker = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);
        Button locateButton = findViewById(R.id.btnRequest);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentLocation!=null){
                    SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("location_lat", Double.toString(currentLocation.latitude)); // Storing float
                    editor.putString("location_lng", Double.toString(currentLocation.longitude)); // Storing float
                    editor.commit();
                }
                else{
                    SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString("location_lat", Double.toString(12.972442)); // Storing float
                    editor.putString("location_lng", Double.toString(77.580643)); // Storing float
                    editor.commit();
                }
                Intent i = new Intent(MapsActivity.this, HomeActivity.class);
                startActivity(i);
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
        Log.d("in Map", "mapready");
        this.googleMap = googleMap;
        uiSettings = this.googleMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);
        this.googleMap.setOnMarkerDragListener(this);

        setUpMap();
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

        Log.d("Second Activity", "MarkerDrag Ended");
        currentLocation = marker.getPosition();
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        Log.d("Second Activity", "Marker Drag Started");
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        Log.d("Second Activity", "Marker is dragged");
    }

    @Override
    public boolean onMyLocationButtonClick() {

        checkLocationPermission();
        if (currentLocation==null || !currentLocation.equals(gps_location)) {
            currentLocation = gps_location;
            updateCurrentCameraAndMarker();
        }

        Log.d("Service Map Activity", "Clicked My location Button");
        return false;
    }


    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Log.d("Service Map Activity", "Clicked My Location");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setUpMap();
                    locationPermissionDenied = false;
                    selectedLocation = true;
                    Log.d("MapActivity", "Permission Granted");

                } else {
                    locationPermissionDenied = true;
                }
            }

        }

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;


    public boolean checkLocationPermission() {

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            Log.d("Service Map Activity", "Location not enabled");
            if (ActivityCompat.shouldShowRequestPermissionRationale(MapsActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(MapsActivity.this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
                return false;
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(MapsActivity.this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
                return false;
            }
        } else {
            locationPermissionDenied = false;
            selectedLocation = true;
            Log.d("MapActivity", "Location already enabled");
            return true;
        }
    }

    public void updateCurrentCameraAndMarker() {

        if (currentLocationMarker != null) {
            currentLocationMarker.remove();
        }

        currentLocationMarker = this.googleMap.addMarker(new MarkerOptions().position(currentLocation).title("Selected Location").draggable(true));
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
        this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
        this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 17.0f));
        Log.d("Service Map Activity", "Added Marker at Current Location " + currentLocation.toString());

    }

    public void setUpMap(){

        boolean result = checkLocationPermission();

        if(result){
            this.googleMap.setMyLocationEnabled(true);
            this.googleMap.setOnMyLocationClickListener(this);
            this.googleMap.setOnMyLocationButtonClickListener(this);

            this.googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

                @Override
                public void onMyLocationChange(Location arg0) {

                    Log.d("Location Changed", arg0.toString());
                    gps_location = new LatLng(arg0.getLatitude(), arg0.getLongitude());
                    if(currentLocation==null){
                        currentLocation = gps_location;
                        updateCurrentCameraAndMarker();
                    }
                }
            });
        }
    }

}
