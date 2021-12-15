package com.example.basketballcourtcrowdchecker;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
        databaseReference   = firebaseDatabase.getReference(currentUser.getUid());

        //Temporarily using user id.
        userName = currentUser.getUid();
        userEmail = currentUser.getEmail();

        accountUsername.setText(userName);
        accountEmail.setText(userEmail);
    }
}