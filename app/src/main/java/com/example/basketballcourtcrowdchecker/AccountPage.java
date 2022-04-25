package com.example.basketballcourtcrowdchecker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class AccountPage extends AppCompatActivity {

    Button accountEditButton;
    TextView accountTitle, accountUsername, accountEmail;
    ImageView accountPicture;

    //Firebase credentials.
    FirebaseAuth fAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseUser currentUser;

    //User credentials.
    String userId;
    String userName;
    String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_page);

        accountEditButton = (Button) findViewById(R.id.accountEditButton);
        accountTitle     = (TextView) findViewById(R.id.accountTitle);
        accountUsername   = (TextView) findViewById(R.id.accountUsername);
        accountEmail       = (TextView) findViewById(R.id.accountEmail);
        accountPicture       = (ImageView) findViewById(R.id.accountPicture);

        //Create firebase instance.
        fAuth               = FirebaseAuth.getInstance();
        firebaseDatabase    = FirebaseDatabase.getInstance("https://basketball-court-crowd-checker-default-rtdb.firebaseio.com/");
        currentUser         = fAuth.getCurrentUser();
        databaseReference   = firebaseDatabase.getReference();

        //Temporarily using user id.
        userId = currentUser.getUid();
        userEmail = currentUser.getEmail();

        //Read once.
        databaseReference.child(userId).child("name").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    userName = String.valueOf(task.getResult().getValue());
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));

                    accountUsername.setText(userName);
                    accountEmail.setText(userEmail);
                }
            }
        });


    }
}