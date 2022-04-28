package com.example.basketballcourtcrowdchecker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SubmitPage extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback, GoogleMap.OnMarkerClickListener {

    EditText latEdit, longEdit, nameEdit;
    Button goButton, submitButton, homeButton4;

    FirebaseFirestore fStore;
    FirebaseAuth fAuth;
    DocumentReference submissionsRef;

    String currLat, currLong;

    //For prompting.
    Snackbar snackbar;
    String prompt;

    private GoogleMap mapMap;
    private LatLng myLocation;
    MapFragment mf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_page);

        latEdit        = (EditText) findViewById(R.id.latEdit);
        longEdit       = (EditText) findViewById(R.id.longEdit);
        nameEdit       = (EditText) findViewById(R.id.nameEdit);
        goButton       = (Button) findViewById(R.id.goButton);
        submitButton   = (Button) findViewById(R.id.submitButton);
        homeButton4    = (Button) findViewById(R.id.homeButton4);
        mf             = (MapFragment) getFragmentManager().findFragmentById(R.id.submitMap);

        fAuth          = FirebaseAuth.getInstance();
        fStore         = FirebaseFirestore.getInstance();

        //Sync map.
        mf.getMapAsync(this);

        currLat = Double.toString(getMyLocation().latitude);
        currLong = Double.toString(getMyLocation().longitude);

        latEdit.setText(currLat);
        longEdit.setText(currLong);

        homeButton4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LandingPage.class));
            }
        });

        //Go to location.
        goButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                double newLatDoub, newLongDoub;
                String newLat = latEdit.getText().toString();
                String newLong = longEdit.getText().toString();

                if (newLat.isEmpty()) {
                    latEdit.setError("Provide a latitude.");
                    return;
                }
                else if (newLong.isEmpty()) {
                    latEdit.setError("Provide a longitude.");
                    return;
                }

                newLatDoub = Double.parseDouble(newLat);
                newLongDoub = Double.parseDouble(newLong);

                LatLng targetLoc = new LatLng(newLatDoub, newLongDoub);

                mapMap.moveCamera(CameraUpdateFactory.newLatLngZoom(targetLoc, 15));

                //Clear all markers.
                mapMap.clear();
                //Add marker to designated location.
                mapMap.addMarker(new MarkerOptions()
                        .position(targetLoc)
                        .title("New court here")
                        .icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                );
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                System.out.println("____________________________________");
                double newLatDoub, newLongDoub;
                String newLat = latEdit.getText().toString();
                String newLong = longEdit.getText().toString();
                String newName = nameEdit.getText().toString();

                if (newLat.isEmpty()) {
                    latEdit.setError("Provide a latitude.");
                    return;
                }
                else if (newLong.isEmpty()) {
                    latEdit.setError("Provide a longitude.");
                    return;
                }
                else if (newName.isEmpty()) {
                    nameEdit.setError("Provide a name for court submission.");
                    return;
                }

                newLatDoub = Double.parseDouble(newLat);
                newLongDoub = Double.parseDouble(newLong);

                String submissionId = fAuth.getCurrentUser().getUid() + "submit";

                //Create and store data using hashmap.
                Map<String, Object> newCourt = new HashMap<>();
                //Store values into user hashmap.
                newCourt.put("latitude", newLatDoub);
                newCourt.put("longitude", newLongDoub);
                newCourt.put("name", newName);
                newCourt.put("contributor", fAuth.getCurrentUser().getUid());

                fStore.collection("submissions").document(submissionId).set(newCourt);

                prompt = "Submitted. One user can only have one submission at a time, next submission will replace the previous.";
                snackbar.make(findViewById(R.id.submitButton), prompt,
                        Snackbar.LENGTH_SHORT)
                        .show();
            }
        });

    }

    private LatLng getMyLocation() {
        // try to get location three ways: GPS, cell/wifi network, and 'passive' mode
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        /*if (ActivityCompat.checkSelfPermission(this, Manifest.permissio) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }*/
        Location loc = null;
        try {
            loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (loc == null) {
                // fall back to network if GPS is not available
                loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            if (loc == null) {
                // fall back to "passive" location if GPS and network are not available
                loc = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            }
        }
        catch (SecurityException ex)
        {

        }

        if (loc == null) {
            return null;   // could not get user's location
        } else {
            double myLat = loc.getLatitude();
            double myLng = loc.getLongitude();
            return new LatLng(myLat, myLng);
        }
    }

    @Override
    public void onMapLoaded() {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onMapReady(GoogleMap mapMap) {
        this.mapMap = mapMap;

        //Set camera location and zoom(currently Dublin).
        //mapMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(53.34433532118153,-6.265035915434364), 10));
        mapMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(getMyLocation().latitude, getMyLocation().longitude), 15));

        //Check permissions.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        //Set location to true.
        mapMap.setMyLocationEnabled(true);
        //Zoom and currLocation settings enabled.
        mapMap.getUiSettings().setZoomControlsEnabled(true);
        mapMap.getUiSettings().setMyLocationButtonEnabled(true);
        //Calls onMapLoaded when layout done.
        mapMap.setOnMapLoadedCallback(this);
    }
}