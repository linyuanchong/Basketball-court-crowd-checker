package com.example.basketballcourtcrowdchecker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class CourtPage extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback, GoogleMap.OnMarkerClickListener{

    ImageView courtIcon;
    Button addRatingButton, checkinButton;
    TextView courtName, crowdNum, courtRating;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;

    private GoogleMap courtMap;
    private LatLng myLocation;
    MapFragment mf;

    //Intents storage.
    String courtId, courtTitle;
    double courtLat, courtLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_court_page);

        courtIcon       = (ImageView) findViewById(R.id.courtIcon);
        addRatingButton = (Button) findViewById(R.id.addRatingButton);
        checkinButton   = (Button) findViewById(R.id.checkinButton);
        courtName       = (TextView) findViewById(R.id.courtName);
        crowdNum       = (TextView) findViewById(R.id.crowdNum);
        courtRating       = (TextView) findViewById(R.id.courtRating);

        fAuth           = FirebaseAuth.getInstance();
        fStore          = FirebaseFirestore.getInstance();
        mf              = (MapFragment) getFragmentManager().findFragmentById(R.id.courtMap);

        Bundle extras = getIntent().getExtras();
        courtId = extras.getString("courtIdIntent");
        courtTitle = extras.getString("courtTitleIntent");
        courtLat = extras.getDouble("courtLatIntent");
        courtLong = extras.getDouble("courtLongIntent");
        System.out.println(courtLat + ", " + courtLong);
        courtName.setText(courtTitle);

        //Sync map.
        mf.getMapAsync(this);

        DocumentReference documentReference = fStore.collection("courts").document(courtId);

        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                courtName.setText(documentSnapshot.getString("name"));
                crowdNum.setText("Crowd: " + documentSnapshot.getLong("crowd").toString());
                courtRating.setText("Rating: " + documentSnapshot.getDouble("rating").toString() + "(" + documentSnapshot.getLong("rated").toString() + ")");
            }
        });


    }

    @Override
    public void onMapLoaded() {
        courtMap.setOnMarkerClickListener(this);

        //Read user's current location, if possible.
        myLocation = getMyLocation();
        if (myLocation == null) {
            Toast.makeText(this, "Unable to access your location. Consider enabling Location in your device's Settings.", Toast.LENGTH_LONG).show();
        } else {
            courtMap.addMarker(new MarkerOptions()
                    .position(myLocation)
                    .title("ME!")
            );
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onMapReady(GoogleMap courtMap) {
        //Set camera location and zoom.
        courtMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(courtLat,courtLong), 15));

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
}