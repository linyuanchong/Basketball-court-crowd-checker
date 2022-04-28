package com.example.basketballcourtcrowdchecker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterPage extends AppCompatActivity {


    //Declare variables.
    EditText registerFullName,registerEmail,registerPassword,registerPhoneNumber;
    Button registerButton;
    TextView registerToLogin;
    ProgressBar registerProgressBar;

    //Firebase credentials.
    FirebaseAuth fAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    FirebaseUser currentUser;

    //User credentials.
    User newUser;
    String userID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        registerFullName    = findViewById(R.id.registerFullName);
        registerEmail       = findViewById(R.id.registerEmail);
        registerPassword    = findViewById(R.id.registerPassword);
        registerPhoneNumber = findViewById(R.id.registerPhoneNumber);
        registerButton      = findViewById(R.id.registerButton);
        registerToLogin     = findViewById(R.id.registerToLogin);
        registerProgressBar = findViewById(R.id.registerProgressBar);

        //Create firebase instance.
        fAuth               = FirebaseAuth.getInstance();
        firebaseDatabase    = FirebaseDatabase.getInstance("https://basketball-court-crowd-checker-default-rtdb.firebaseio.com/");


        //If user not exist.
        if(fAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), LandingPage.class));
            finish();
        }

        //When register button clicked.
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get text from editTexts.
                final String email              = registerEmail.getText().toString().trim();
                String password                 = registerPassword.getText().toString().trim();
                final String fullName           = registerFullName.getText().toString();
                final String phoneNumber        = registerPhoneNumber.getText().toString();
                final String currentCourt       = "none";
                final boolean presence          = false;
                final String ratedCourt         = "";
                final FirebaseDatabase database = FirebaseDatabase.getInstance();

                //Regex for phone number to match 9999999999, 1-999-999-9999 and 999-999-9999.
                String phoneRegex = "^(1\\-)?[0-9]{3}\\-?[0-9]{3}\\-?[0-9]{4}$";
                //Regex for full name to match Michael Jordan.
                String nameRegex = "^[A-Z][a-z]{2,}(?: [A-Z][a-z]*)*$";

                //Error detections.
                if(TextUtils.isEmpty(email)){
                    registerEmail.setError("Require an Email.");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    registerPassword.setError("Require a password.");
                    return;
                }
                if (!fullName.matches(nameRegex)) {
                    registerFullName.setError("Not a valid full name.");
                    return;
                }
                if(password.length() < 6){
                    registerPassword.setError("Require password to be more than 6 characters.");
                    return;
                }
                if (checkString(password) == false) {
                    registerPassword.setError("Password needs to have at least 1 uppercase, 1 lowercase and 1 digit.");
                    return;
                }
                if(TextUtils.isEmpty(phoneNumber)){
                    registerPhoneNumber.setError("Require a phone number.");
                    return;
                }
                if (!phoneNumber.matches(phoneRegex)) {
                    registerPhoneNumber.setError("Not a valid phone number.");
                    return;
                }


                //Set progress bar to be visible.
                registerProgressBar.setVisibility(View.VISIBLE);

                //Register the user into firebase.
                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(RegisterPage.this, "User Created successfully.", Toast.LENGTH_SHORT).show();

                            //Get user credentials.
                            currentUser         = fAuth.getCurrentUser();
                            databaseReference   = firebaseDatabase.getReference(currentUser.getUid());

                            addDataToFirebase(email, password, fullName, phoneNumber, currentCourt, presence);

                            startActivity(new Intent(getApplicationContext(), LandingPage.class));
                        }
                        else {
                            Toast.makeText(RegisterPage.this, "Error, can't sign you up." + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            registerProgressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        registerToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginPage.class));
            }
        });
    }

    private void addDataToFirebase (String thisEmail, String thisPassword, String thisName, String thisPhone, String thisCurrentCourt, boolean thisPresence) {

        //Create user object.
        newUser = new User();

        //Set.
        newUser.setEmail(thisEmail);
        newUser.setPassword(thisPassword);
        newUser.setName(thisName);
        newUser.setPhone(thisPhone);
        newUser.setCurrentCourt(thisCurrentCourt);
        newUser.setPresence(thisPresence);

        //Add into database.
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                databaseReference.setValue(newUser);
                Toast.makeText(RegisterPage.this, "Successfully added data.", Toast.LENGTH_SHORT).show();
            }
            //If cancelled/failed.
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RegisterPage.this, "Fail to add data." + error, Toast.LENGTH_SHORT).show();
            }
        });

    }

    //Check if password is valid.
    public static boolean checkString(String str) {

        char ch;
        boolean capitalFlag = false;
        boolean lowerCaseFlag = false;
        boolean numberFlag = false;

        for (int i=0;i < str.length();i++) {

            ch = str.charAt(i);

            if (Character.isDigit(ch)) {
                numberFlag = true;
            } else if (Character.isUpperCase(ch)) {
                capitalFlag = true;
            } else if (Character.isLowerCase(ch)) {
                lowerCaseFlag = true;
            }
        }

        if (numberFlag && capitalFlag && lowerCaseFlag) {
            return true;
        }
        else {
            return false;
        }

    }
}