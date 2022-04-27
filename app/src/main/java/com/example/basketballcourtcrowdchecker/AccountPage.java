package com.example.basketballcourtcrowdchecker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.ContentValues.TAG;

public class AccountPage extends AppCompatActivity {

    Button accountEditButton, homeButton;
    TextView accountTitle, accountUsername, accountEmail, accountPassword, accountPhone, notifyText;
    EditText manageUsername, manageEmail, managePassword, managePhone;

    //Firebase credentials.
    FirebaseAuth fAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference, usernameReference, passwordReference, emailReference, phoneReference;
    FirebaseUser currentUser;
    FirebaseUser user;

    //User credentials.
    String userId;
    String userName;
    String userPassword;
    String userEmail;

    //For prompting.
    Snackbar snackbar;
    String prompt;

    //For string checking.
    Pattern emailPattern;
    Matcher emailMatcher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_page);

        accountEditButton   = (Button) findViewById(R.id.accountEditButton);
        homeButton          = (Button) findViewById(R.id.homeButton);
        notifyText          = (TextView) findViewById(R.id.notifyText);
        accountTitle        = (TextView) findViewById(R.id.accountTitle);
        accountUsername     = (TextView) findViewById(R.id.accountUsername);
        accountEmail        = (TextView) findViewById(R.id.accountEmail);
        accountPassword     = (TextView) findViewById(R.id.accountPassword);
        accountPhone        = (TextView) findViewById(R.id.accountPhone);
        manageUsername      = (EditText) findViewById(R.id.manageUsername);
        manageEmail         = (EditText) findViewById(R.id.manageEmail);
        managePassword      = (EditText) findViewById(R.id.managePassword);
        managePhone         = (EditText) findViewById(R.id.managePhone);

        //Firebase stuff.
        fAuth               = FirebaseAuth.getInstance();
        firebaseDatabase    = FirebaseDatabase.getInstance("https://basketball-court-crowd-checker-default-rtdb.firebaseio.com/");
        currentUser         = fAuth.getCurrentUser();
        databaseReference   = firebaseDatabase.getReference();

        userId              = currentUser.getUid();

        usernameReference   = databaseReference.child(userId).child("name");
        emailReference      = databaseReference.child(userId).child("email");
        passwordReference   = databaseReference.child(userId).child("password");
        phoneReference      = databaseReference.child(userId).child("phone");
        user                = FirebaseAuth.getInstance().getCurrentUser();

        //Set.
        notifyText.setVisibility(View.GONE);
        manageUsername.setVisibility(View.GONE);
        manageEmail.setVisibility(View.GONE);
        managePassword.setVisibility(View.GONE);
        managePhone.setVisibility(View.GONE);
        accountUsername.setVisibility(View.VISIBLE);
        accountEmail.setVisibility(View.VISIBLE);
        accountPassword.setVisibility(View.VISIBLE);
        accountPhone.setVisibility(View.VISIBLE);

        homeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LandingPage.class));
            }
        });


        //Get username.
        usernameReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    userName = String.valueOf(task.getResult().getValue());
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));

                    userEmail           = currentUser.getEmail();
                    accountUsername.setText("Username: " + userName);
                    accountEmail.setText("Email: " + userEmail);
                    phoneReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (!task.isSuccessful()) {
                                Log.e("firebase", "Error getting data", task.getException());
                            }
                            else {
                                String phoneText = String.valueOf(task.getResult().getValue());
                                Log.d("firebase", String.valueOf(task.getResult().getValue()));

                                accountPhone.setText("Phone Number: " + phoneText);
                            }
                        }
                    });
                }
            }
        });

        accountEditButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                Button thisButton = (Button)accountEditButton;
                String buttonText = thisButton.getText().toString();
                System.out.println(buttonText);

                //To edit.
                if (buttonText.equals("Edit")) {

                    accountUsername.setVisibility(View.GONE);
                    accountEmail.setVisibility(View.GONE);
                    accountPassword.setVisibility(View.GONE);
                    accountPhone.setVisibility(View.GONE);
                    notifyText.setVisibility(View.VISIBLE);
                    manageUsername.setVisibility(View.VISIBLE);
                    manageEmail.setVisibility(View.VISIBLE);
                    managePassword.setVisibility(View.VISIBLE);
                    managePhone.setVisibility(View.VISIBLE);
                    accountEditButton.setText("Finish and return");

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

                                userEmail = currentUser.getEmail();
                                manageUsername.setText(userName);
                                manageEmail.setText(userEmail);

                                prompt = "Input new credentials into the fields.";
                                snackbar.make(findViewById(R.id.accountEditButton), prompt,
                                        Snackbar.LENGTH_SHORT)
                                        .show();
                            }
                        }
                    });
                }

                //To commit.
                else if (buttonText.equals("Finish and return")) {

                    //Get original password.
                    passwordReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {
                            if (!task.isSuccessful()) {
                                Log.e("firebase", "Error getting data", task.getException());
                            }
                            else {

                                //Get original email.
                                userEmail = currentUser.getEmail();
                                //Get original password.
                                userPassword = String.valueOf(task.getResult().getValue());

                                String newUsername = manageUsername.getText().toString();
                                String newEmail    = manageEmail.getText().toString();
                                String newPassword = managePassword.getText().toString();
                                String newPhone    = managePhone.getText().toString();

                                //Regex for phone number to match 9999999999, 1-999-999-9999 and 999-999-9999.
                                String phoneRegex = "^(1\\-)?[0-9]{3}\\-?[0-9]{3}\\-?[0-9]{4}$";
                                //Regex for full name to match Michael Jordan.
                                String nameRegex  = "^[A-Z][a-z]{2,}(?: [A-Z][a-z]*)*$";

                                Pattern pattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
                                Matcher mat = pattern.matcher(newEmail);


                                //Error detections.
                                if(TextUtils.isEmpty(newUsername)) {
                                    manageUsername.setError("Require an username.");
                                    return;
                                }
                                if (!newUsername.matches(nameRegex)) {
                                    manageUsername.setError("Not a valid full name.");
                                    return;
                                }
                                if (TextUtils.isEmpty(newEmail)) {
                                    manageEmail.setError("Require an Email.");
                                    return;
                                }
                                if (!mat.matches()){
                                    manageEmail.setError("Not a valid email.");
                                    return;
                                }
                                if (TextUtils.isEmpty(newPassword)) {
                                    managePassword.setError("Require a password.");
                                    return;
                                }
                                if (newPassword.length() < 6) {
                                    managePassword.setError("Require password to be more than 6 characters.");
                                    return;
                                }
                                if (RegisterPage.checkString(newPassword) == false) {
                                    managePassword.setError("Password needs to have at least 1 uppercase, 1 lowercase and 1 digit.");
                                    return;
                                }
                                if(TextUtils.isEmpty(newPhone)){
                                    managePhone.setError("Require a phone number.");
                                    return;
                                }
                                if (!newPhone.matches(phoneRegex)) {
                                    managePhone.setError("Not a valid phone number.");
                                    return;
                                }

                                //Update username.
                                usernameReference.setValue(newUsername);
                                emailReference.setValue(newEmail);
                                passwordReference.setValue(newPassword);
                                phoneReference.setValue(newPhone);

                                //Get credentials (email + password).
                                AuthCredential credential = EmailAuthProvider.getCredential(userEmail, userPassword);

                                user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            user.updateEmail(newEmail);
                                            user.updatePassword(newPassword);

                                            startActivity(new Intent(getApplicationContext(), LandingPage.class));

                                        } else {
                                            Log.d(TAG, "Error auth failed");
                                        }
                                    }
                                });

                            }
                        }
                    });


                }

            }
        });






    }
}