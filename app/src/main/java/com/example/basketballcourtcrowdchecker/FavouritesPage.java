package com.example.basketballcourtcrowdchecker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FavouritesPage extends AppCompatActivity {

    //For ListView.
    ListView favouriteListView;
    List<String> favouriteList;
    ArrayAdapter<String> favouriteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites_page);

        favouriteListView    = findViewById(R.id.favouriteListView);

        //Sync ListView.
        String[] favouriteArray = new String[] {
                "Favourite 0",
                "Favourite 1",
                "Favourite 2",
                "Favourite 3",
                "Favourite 4",
                "Favourite 5",};
        //Create an empty list from String Array elements.
        favouriteList = new ArrayList<String>(Arrays.asList(favouriteArray));
        //Create an ArrayAdapter from List.
        favouriteAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, favouriteList);

        //Attach ListView with items from ArrayAdapter.
        favouriteListView.setAdapter(favouriteAdapter);

        //When ListView's items are clicked.
        favouriteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Option 0.
                if (position==0) {
                    startActivity(new Intent(getApplicationContext(), CourtPage.class));
                    System.out.println("Favourite 0");
                }
                //Option 1.
                else if (position==1) {
                    System.out.println("Favourite 1");
                }
                //Option 2.
                else if (position==2) {
                    //Action here.
                    System.out.println("Favourite 2");
                }
                //Option 3.
                else if (position==3) {
                    //Action here.
                    System.out.println("Favourite 3");
                }
                //Option 4.
                else if (position==4) {
                    //Action here.
                    System.out.println("Favourite 4");
                }
                //Option 5.
                else if (position==5) {
                    //Action here.
                    System.out.println("Favourite 5");
                }

                //Refer location.
                //Intent allIntents = new Intent(LandingPage.this, CourtPage.class);
                //allIntents.putExtra("idIntent", position);
                //allIntents.putExtra("intentName1", intent1);
                //allIntents.putExtra("intentName2", intent2);
                //startActivity(allIntents);
            }
        });
    }


}