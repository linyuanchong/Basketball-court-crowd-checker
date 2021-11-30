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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginPage extends AppCompatActivity {

    EditText loginEmail, loginPassword;
    Button loginButton;
    TextView loginToRegister;
    ProgressBar loginProgressBar;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        loginToRegister     = findViewById(R.id.loginToRegister);
        loginEmail          = findViewById(R.id.loginEmail);
        loginPassword       = findViewById(R.id.loginPassword);
        loginButton         = findViewById(R.id.loginButton);
        loginProgressBar    = findViewById(R.id.loginProgressBar);
        //Create firebase instance.
        fAuth               = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get text from editTexts.
                final String email = loginEmail.getText().toString().trim();
                String password = loginPassword.getText().toString().trim();

                //Error detections.
                if(TextUtils.isEmpty(email)){
                    loginEmail.setError("Require an Email.");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    loginPassword.setError("Require a password.");
                    return;
                }
                if(password.length() < 6){
                    loginPassword.setError("Require password to be more than 6 characters.");
                    return;
                }

                //Set progress bar to be visible.
                loginProgressBar.setVisibility(View.VISIBLE);

                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginPage.this, "Logged in successfully.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), LandingPage.class));
                        }
                        else {
                            Toast.makeText(LoginPage.this, "Error, can't log you in. " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            loginProgressBar.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        loginToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterPage.class));
            }
        });

    }


}