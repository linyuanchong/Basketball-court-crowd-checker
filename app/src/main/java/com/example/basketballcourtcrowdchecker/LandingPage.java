package com.example.basketballcourtcrowdchecker;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import static android.content.ContentValues.TAG;

public class LandingPage extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback, GoogleMap.OnMarkerClickListener{

    private AppBarConfiguration mAppBarConfiguration;

    //General declarations.
    Toolbar toolbar;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    String userID;
    FirebaseUser currentUser;

    DatabaseReference presenceReference;
    DatabaseReference currCourtReference;
    DocumentReference courtDocRef;
    CollectionReference crowdColRef;

    //Strings.
    String userId;
    String currCourtId;
    String thisCurrCourtId;

    //Store all intents.
    Intent courtIntents;

    //Components.
    TextView currentCourt;

    //For maps.
    private GoogleMap mapMap;
    private LatLng myLocation;
    double latitude, longitude;
    MapFragment mf;

    //For Firebase.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);

        toolbar                         = findViewById(R.id.toolbar);
        DrawerLayout drawer             = findViewById(R.id.drawer_layout);
        NavigationView navigationView   = findViewById(R.id.nav_view);
        currentCourt                    = (TextView) findViewById(R.id.currentCourt);


        //Create firebase instance.
        fAuth                           = FirebaseAuth.getInstance();
        fStore                          = FirebaseFirestore.getInstance();
        firebaseDatabase                = FirebaseDatabase.getInstance("https://basketball-court-crowd-checker-default-rtdb.firebaseio.com/");
        databaseReference               = firebaseDatabase.getReference();

        //User stuff.
        currentUser                     = fAuth.getCurrentUser();
        userId                          = currentUser.getUid();
        presenceReference               = databaseReference.child(userId).child("presence");
        currCourtReference              = databaseReference.child(userId).child("currentCourt");

        mf                              = (MapFragment) getFragmentManager().findFragmentById(R.id.mapMap);

        //Read once.
        currCourtReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    currCourtId = String.valueOf(task.getResult().getValue());

                    if (currCourtId.equals("none")) {
                        currentCourt.setText("You are currently not checked in into any courts. Select a court to check in.");
                    }
                    else {
                        Log.d("firebase", String.valueOf(task.getResult().getValue()));
                        displayCurrCourt(currCourtId);
                    }
                }
            }
        });


        courtIntents = new Intent(LandingPage.this, CourtPage.class);

        //Sync map.
        mf.getMapAsync(this);

        //Set support action bar.
        setSupportActionBar(toolbar);

        //Set userID.
        userID = fAuth.getCurrentUser().getUid();

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        //Navigation drawer listener.
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                //it's possible to do more actions on several items, if there is a large amount of items I prefer switch(){case} instead of if()
                if (id==R.id.nav_map){
                    startActivity(new Intent(getApplicationContext(), LandingPage.class));
                }
                else if (id==R.id.nav_account){
                    startActivity(new Intent(getApplicationContext(), AccountPage.class));
                }
                else if (id==R.id.nav_manage){
                    startActivity(new Intent(getApplicationContext(), ManagePage.class));
                }
                else if (id==R.id.nav_settings){
                    startActivity(new Intent(getApplicationContext(), SettingsPage.class));
                }
                else if (id==R.id.nav_logout){
                    FirebaseAuth.getInstance().signOut();//logout
                    startActivity(new Intent(getApplicationContext(), LoginPage.class));
                    finish();
                }
                //This is for maintaining the behavior of the Navigation view
                NavigationUI.onNavDestinationSelected(menuItem,navController);
                //This is for closing the drawer after acting on it
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.landing_page, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onMapReady(GoogleMap mapMap) {
        this.mapMap = mapMap;

        //Set camera location and zoom(currently Dublin).
        mapMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(53.34433532118153,-6.265035915434364), 10));

        mapMap.getUiSettings().setZoomControlsEnabled(true);
        //Calls onMapLoaded when layout done.
        mapMap.setOnMapLoadedCallback(this);
    }

    @Override
    public void onMapLoaded() {
        // code to run when the map has loaded
        readCourts();
        mapMap.setOnMarkerClickListener(this);

        // read user's current location, if possible
        myLocation = getMyLocation();
        if (myLocation == null) {
            Toast.makeText(this, "Unable to access your location. Consider enabling Location in your device's Settings.", Toast.LENGTH_LONG).show();
        } else {
            mapMap.addMarker(new MarkerOptions()
                    .position(myLocation)
                    .title("ME!")
            );
        }
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
    public boolean onMarkerClick(Marker marker) {
        if (myLocation != null) {
            LatLng markerLatLng = marker.getPosition();
            mapMap.addPolyline(new PolylineOptions()
                    .add(myLocation)
                    .add(markerLatLng)
            );
            return true;
        } else {

            //Save location title intent.
            courtIntents.putExtra("courtIdIntent", marker.getId());
            courtIntents.putExtra("courtTitleIntent", marker.getTitle());
            courtIntents.putExtra("courtLatIntent", marker.getPosition().latitude);
            courtIntents.putExtra("courtLongIntent", marker.getPosition().longitude);
            startActivity(courtIntents);
            return false;
        }
    }

    //1st of two functions to read all courts.
    private void readCourts() {

        final int[] length = {0};

        fStore.collection("courts")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int count = 0;
                            for (DocumentSnapshot document : task.getResult()) {
                                count++;
                            }
                            length[0] = count;
                            getCourts(length[0]);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    //2nd of two functions to read all courts.
    private void getCourts(int length) {

        for (int i = 0; i < length; i++) {

            DocumentReference documentReference = fStore.collection("courts").document("m" + Integer.toString(i));
            System.out.println(documentReference);

            documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {

                    GeoPoint geoPoint = documentSnapshot.getGeoPoint("location");
                    String name = documentSnapshot.getString("name");

                    double lat = geoPoint.getLatitude();
                    double lng = geoPoint.getLongitude();
                    LatLng latLng = new LatLng(lat, lng);

                    mapMap.addMarker(new MarkerOptions()
                            .position(new LatLng(lat, lng))
                            .title(name)
                    );
                }
            });
        }

    }

    //Function to display current court.
    public void displayCurrCourt(String currCourtId) {

        courtDocRef = fStore.collection("courts").document(currCourtId);

        courtDocRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                currentCourt.setText("You are currently checked in into: " + documentSnapshot.getString("name") + ".");
            }
        });
    }

}