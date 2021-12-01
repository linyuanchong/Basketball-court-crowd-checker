package com.example.basketballcourtcrowdchecker;

import android.accounts.Account;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.AdapterView;
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

        //Sync ListView.
        String[] locationArray = new String[] {
                "Court 0",
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

        //When ListView's items are clicked.
        courtListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Option 0.
                if (position==0) {
                    startActivity(new Intent(getApplicationContext(), CourtPage.class));
                    System.out.println("Option 0");
                }
                //Option 1.
                else if (position==1) {
                    System.out.println("Option 1");
                }
                //Option 2.
                else if (position==2) {
                    //Action here.
                    System.out.println("Option 2");
                }
                //Option 3.
                else if (position==3) {
                    //Action here.
                    System.out.println("Option 3");
                }
                //Option 4.
                else if (position==4) {
                    //Action here.
                    System.out.println("Option 4");
                }
                //Option 5.
                else if (position==5) {
                    //Action here.
                    System.out.println("Option 5");
                }

                //Refer location.
                //Intent allIntents = new Intent(LandingPage.this, CourtPage.class);
                //allIntents.putExtra("idIntent", position);
                //allIntents.putExtra("intentName1", intent1);
                //allIntents.putExtra("intentName2", intent2);
                //startActivity(allIntents);
            }
        });


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
                else if (id==R.id.nav_favourites){
                    startActivity(new Intent(getApplicationContext(), FavouritesPage.class));
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