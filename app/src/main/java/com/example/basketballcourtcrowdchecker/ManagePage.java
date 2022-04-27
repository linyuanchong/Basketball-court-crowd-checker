package com.example.basketballcourtcrowdchecker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
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

public class ManagePage extends AppCompatActivity {


    ConstraintLayout cl;
    ImageView courtIcon;
    Button addRatingButton, checkinButton, homeButton2;
    TextView currentCourt2, crowdDisplay, courtRating;
    ImageView checkinDisplay;
    Switch checkSwitch;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseRef;
    DatabaseReference presenceReference;
    DatabaseReference currCourtReference;
    DocumentReference courtDocRef;
    CollectionReference crowdColRef;
    FirebaseUser currentUser;

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
        setContentView(R.layout.activity_manage_page);

        homeButton2         = (Button) findViewById(R.id.homeButton2);
        checkSwitch         = (Switch) findViewById(R.id.checkSwitch);
        currentCourt2       = (TextView) findViewById(R.id.currentCourt2);
        checkinDisplay      = (ImageView) findViewById(R.id.checkinDisplay);
        crowdDisplay        = (TextView) findViewById(R.id.crowdDisplay);

        fAuth               = FirebaseAuth.getInstance();
        currentUser         = fAuth.getCurrentUser();
        userId              = currentUser.getUid();
        fStore              = FirebaseFirestore.getInstance();
        firebaseDatabase    = FirebaseDatabase.getInstance("https://basketball-court-crowd-checker-default-rtdb.firebaseio.com/");
        databaseRef         = firebaseDatabase.getReference();
        presenceReference   = databaseRef.child(userId).child("presence");
        currCourtReference  = databaseRef.child(userId).child("currentCourt");

        homeButton2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LandingPage.class));
            }
        });

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
                        currentCourt2.setText("You are currently not checked in into any courts. Select a court to check in on the homepage.");
                    }
                    else {
                        Log.d("firebase", String.valueOf(task.getResult().getValue()));
                        displayCurrCourt(currCourtId);
                    }
                }
            }
        });

        //Check.
        presenceReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    presence = (Boolean) task.getResult().getValue();
                    if (presence == true) {

                        checkinDisplay.setImageResource(R.drawable.greencircle);
                        checkSwitch.setChecked(true);
                        checkSwitch.setVisibility(View.VISIBLE);
                        checkSwitch.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (checkSwitch.isChecked()) {
                                    System.out.println("Checked.");
                                }
                                else if (!checkSwitch.isChecked()) {
                                    System.out.println("Nope.");

                                    //Read once.
                                    currCourtReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                                            if (!task.isSuccessful()) {
                                                Log.e("firebase", "Error getting data", task.getException());
                                            }
                                            else {
                                                currCourtId = String.valueOf(task.getResult().getValue());
                                                courtDocRef         = fStore.collection("courts").document(currCourtId);
                                                courtDocRef.update("crowd", FieldValue.increment(-1));
                                            }
                                        }
                                    });

                                    prompt = "Checked out successfully.";
                                    snackbar.make(findViewById(R.id.checkSwitch), prompt,
                                            Snackbar.LENGTH_SHORT)
                                            .show();

                                    checkSwitch.setChecked(false);
                                    checkSwitch.setVisibility(View.GONE);

                                    presenceReference.setValue(false);
                                    currCourtReference.setValue("none");

                                    finish();
                                    startActivity(getIntent());
                                }
                            }
                        });
                    }
                    else if (presence == false) {
                        checkinDisplay.setImageResource(R.drawable.whitecircle);

                        checkSwitch.setChecked(false);
                        checkSwitch.setVisibility(View.GONE);
                    }

                    checkinDisplay.getLayoutParams().height = 100;
                    checkinDisplay.getLayoutParams().width = 100;

                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                }
            }
        });
    }

    //Function to display current court.
    public void displayCurrCourt(String currCourtId) {

        courtDocRef = fStore.collection("courts").document(currCourtId);

        courtDocRef.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                currentCourt2.setText("You are currently checked in into: " + documentSnapshot.getString("name") + ".");
                crowdDisplay.setText("Crowd: " + documentSnapshot.getLong("crowd").toString());
            }
        });
    }

}