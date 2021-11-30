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
import com.google.firebase.database.FirebaseDatabase;
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
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
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
        fStore              = FirebaseFirestore.getInstance();

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
                final String phoneNumber              = registerPhoneNumber.getText().toString();
                final FirebaseDatabase database = FirebaseDatabase.getInstance();

                //Error detections.
                if(TextUtils.isEmpty(email)){
                    registerEmail.setError("Require an Email.");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    registerPassword.setError("Require a password.");
                    return;
                }
                if(password.length() < 6){
                    registerPassword.setError("Require password to be more than 6 characters.");
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

                            //Set userID.
                            userID = fAuth.getCurrentUser().getUid();
                            //Get "users" reference.
                            DocumentReference documentReference = fStore.collection("users").document(userID);
                            //Create and store data using hashmap.
                            Map<String, Object> user = new HashMap<>();
                            //Store values into user hashmap.
                            user.put("user_email", email);
                            user.put("user_password", password);
                            user.put("user_full_name", fullName);
                            user.put("user_phone_number", phoneNumber);

                            //Additional documents:
                            //user.put("workout_list_monday", "");
                            //user.put("workout_list_tuesday", "");
                            //user.put("workout_list_wednesday", "");

                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    System.out.println("successfully created new user" + userID);
                                }
                            });

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
}