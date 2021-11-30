package com.example.basketballcourtcrowdchecker;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LandingPage extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLoadedCallback, GoogleMap.OnMarkerClickListener{

    private AppBarConfiguration mAppBarConfiguration;

    //General declarations.
    Toolbar toolbar;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;

    //For ListView.
    ListView courtListView;
    List<String> courtList;
    ArrayAdapter<String> courtAdapter;

    //For maps.
    private GoogleMap mapMap;
    private LatLng myLocation;
    double latitude, longitude;
    MapFragment mf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_page);

        toolbar                         = findViewById(R.id.toolbar);
        DrawerLayout drawer             = findViewById(R.id.drawer_layout);
        NavigationView navigationView   = findViewById(R.id.nav_view);
        courtListView                   = findViewById(R.id.courtListView);
        //Create firebase instance.
        fAuth                           = FirebaseAuth.getInstance();
        fStore                          = FirebaseFirestore.getInstance();
        mf                              = (MapFragment) getFragmentManager().findFragmentById(R.id.mapMap);

        //Set support action bar.
        setSupportActionBar(toolbar);

        //Sync ListView.
        String[] locationArray = new String[] {
                "Court 1",
                "Court 2",
                "Court 3",
                "Court 4",
                "Court 5",};
        //Create an empty list from String Array elements.
        courtList = new ArrayList<String>(Arrays.asList(locationArray));
        //Create an ArrayAdapter from List.
        courtAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, courtList);

        //Attach ListView with items from ArrayAdapter.
        courtListView.setAdapter(courtAdapter);

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

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int id = menuItem.getItemId();
                //it's possible to do more actions on several items, if there is a large amount of items I prefer switch(){case} instead of if()
                if (id==R.id.nav_map){
                    //Action here.
                }
                else if (id==R.id.nav_account){
                    //Action here.
                }
                else if (id==R.id.nav_favourites){
                    //Action here.
                }
                else if (id==R.id.nav_settings){
                    //Action here.
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
    public void onMapLoaded() {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }
}