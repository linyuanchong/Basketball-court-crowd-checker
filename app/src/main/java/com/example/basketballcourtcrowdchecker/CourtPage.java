package com.example.basketballcourtcrowdchecker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firestore.v1.WriteResult;

public class CourtPage extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback, GoogleMap.OnMarkerClickListener{

    ConstraintLayout cl;
    ImageView courtIcon;
    Button addRatingButton, checkinButton, homeButton1;
    TextView courtName, crowdNum, courtRating;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseRef;
    DatabaseReference presenceReference;
    DatabaseReference currCourtReference;
    DatabaseReference ratedCourtReference;
    DocumentReference courtDocRef;
    CollectionReference crowdColRef;
    FirebaseUser currentUser;
    ImageView circleDisplay;


    private GoogleMap courtMap;
    private LatLng myLocation;
    MapFragment mf;

    //User personal data.
    String userId;
    String currCourtId;
    boolean presence;

    //Intents storage.
    String courtId, courtTitle;
    double courtLat, courtLong;

    //For prompting.
    Snackbar snackbar;
    String prompt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_court_page);

        courtIcon       = (ImageView) findViewById(R.id.courtIcon);
        circleDisplay   = (ImageView) findViewById(R.id.circleDisplay);
        checkinButton   = (Button) findViewById(R.id.checkinButton);
        homeButton1     = (Button) findViewById(R.id.homeButton1);
        courtName       = (TextView) findViewById(R.id.courtName);
        crowdNum        = (TextView) findViewById(R.id.crowdNum);
        cl              = findViewById(R.id.cl);

        //Retrieve intents.
        Bundle extras = getIntent().getExtras();
        courtId = extras.getString("courtIdIntent");
        courtTitle = extras.getString("courtTitleIntent");
        courtLat = extras.getDouble("courtLatIntent");
        courtLong = extras.getDouble("courtLongIntent");
        courtName.setText(courtTitle);

        //Get all instances.
        fAuth               = FirebaseAuth.getInstance();
        fStore              = FirebaseFirestore.getInstance();
        firebaseDatabase    = FirebaseDatabase.getInstance("https://basketball-court-crowd-checker-default-rtdb.firebaseio.com/");

        //User info.
        currentUser         = fAuth.getCurrentUser();
        userId              = currentUser.getUid();

        //Detailed references.
        databaseRef         = firebaseDatabase.getReference();
        presenceReference   = databaseRef.child(userId).child("presence");
        currCourtReference  = databaseRef.child(userId).child("currentCourt");
        ratedCourtReference = databaseRef.child(userId).child("ratedCourt");
        courtDocRef         = fStore.collection("courts").document(courtId);
        crowdColRef         = fStore.collection("courts").document(courtId).collection("crowd");
        mf                  = (MapFragment) getFragmentManager().findFragmentById(R.id.courtMap);

        //Sync map.
        mf.getMapAsync(this);

        homeButton1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LandingPage.class));
            }
        });

        courtDocRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                courtName.setText(documentSnapshot.getString("name"));
                crowdNum.setText("Crowd: " + documentSnapshot.getLong("crowd").toString());
                }
        });

        //Check presence and define display.
        databaseRef.child(userId).child("presence").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    presence = (Boolean) task.getResult().getValue();

                    //Read once.
                    currCourtReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (!task.isSuccessful()) {
                                Log.e("firebase", "Error getting data", task.getException());
                            }
                            else {
                                currCourtId = String.valueOf(task.getResult().getValue());

                                //If checked in.
                                if (presence == true ) {
                                    //Check out button when user is on the court's page of their current court.
                                    if (currCourtId.equals(courtId)) {
                                        checkinButton.setVisibility(View.VISIBLE);
                                        checkinButton.setText("CHECK OUT");
                                        circleDisplay.getLayoutParams().height = 60;
                                        circleDisplay.getLayoutParams().width = 60;
                                        circleDisplay.setImageResource(R.drawable.greencircle);
                                    }
                                    //Hide button when user is not on the court's page of their current court.
                                    else if (!currCourtId.equals(courtId)) {
                                        checkinButton.setVisibility(View.GONE);
                                        circleDisplay.getLayoutParams().height = 60;
                                        circleDisplay.getLayoutParams().width = 60;
                                        circleDisplay.setImageResource(R.drawable.redcircle);
                                    }
                                }
                                //Check in button when user is not checked in at all.
                                else if (presence == false) {
                                    checkinButton.setVisibility(View.VISIBLE);
                                    checkinButton.setText("CHECK IN");
                                    circleDisplay.getLayoutParams().height = 60;
                                    circleDisplay.getLayoutParams().width = 60;
                                    circleDisplay.setImageResource(R.drawable.whitecircle);
                                }
                            }
                        }
                    });
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                }
            }
        });

        //When check in button clicked.
        checkinButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){

                presenceReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (!task.isSuccessful()) {
                            Log.e("firebase", "Error getting data", task.getException());
                        }
                        else {
                            presence = (Boolean)task.getResult().getValue();
                            //If checked in.
                            if (presence == true) {
                                prompt = "Checked out successfully.";
                                snackbar.make(findViewById(R.id.checkinButton), prompt,
                                        Snackbar.LENGTH_SHORT)
                                        .show();
                                //Set user presence to false.
                                presenceReference.setValue(false);
                                currCourtReference.setValue("none");
                                presence = (Boolean)task.getResult().getValue();
                                checkInOrOut();
                            }
                            //If not checked in.
                            else if (presence == false) {
                                prompt = "Checked in successfully.";
                                snackbar.make(findViewById(R.id.checkinButton), prompt,
                                        Snackbar.LENGTH_SHORT)
                                        .show();
                                //Set user presence to true.
                                presenceReference.setValue(true);
                                currCourtReference.setValue(courtId);
                                presence = (Boolean)task.getResult().getValue();
                                checkInOrOut();
                            }
                            finish();
                            startActivity(getIntent());
                            Log.d("firebase", String.valueOf(task.getResult().getValue()));
                        }
                    }
                });
            }
        });


    }

    //Check in or ot function.
    public void checkInOrOut() {
        if (presence == false) {
            courtDocRef.update("crowd", FieldValue.increment(1));
        }
        else if (presence == true) {
            courtDocRef.update("crowd", FieldValue.increment(-1));
        }
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