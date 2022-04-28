package com.example.basketballcourtcrowdchecker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AboutPage extends AppCompatActivity {

    TextView aboutBg1;
    Button homeButton3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_page);

        aboutBg1    = (TextView)findViewById(R.id.aboutBg1);
        homeButton3 = (Button)findViewById(R.id.homeButton3);

        homeButton3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LandingPage.class));
            }
        });

        aboutBg1.setMovementMethod(new ScrollingMovementMethod());
    }
}