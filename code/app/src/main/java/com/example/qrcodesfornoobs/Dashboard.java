package com.example.qrcodesfornoobs;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Dashboard extends AppCompatActivity {
    Button profileButton;
    Button settingsButton;
    Button searchButton;
    Button mapButton;
    Button leaderboardButton;
    Button cameraButton;
    Button homeButton;
    private Intent profileIntent;
    private Intent settingsIntent;
    private Intent searchIntent;
    private Intent mapIntent;
    private Intent leaderboardIntent;
    private Intent cameraIntent;
    private Intent dashboardIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);
        profileIntent = new Intent(this, Profile.class);
        settingsIntent = new Intent(this, Settings.class);
        searchIntent = new Intent(this, Search.class);
        mapIntent = new Intent(this, Map.class);
        leaderboardIntent = new Intent(this, Leaderboard.class);
        cameraIntent = new Intent(this, Camera.class);
        dashboardIntent = new Intent(this, Dashboard.class);
        addListenerOnButtons();
    }

    private void addListenerOnButtons() {
        profileButton = (Button) findViewById(R.id.profile_button);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(profileIntent);
            }
        });

        settingsButton = (Button) findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(settingsIntent);
            }
        });

        homeButton = (Button) findViewById(R.id.dashboard_button);
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(dashboardIntent);
            }
        });

        searchButton = (Button) findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(searchIntent);
            }
        });

        cameraButton = (Button) findViewById(R.id.camera_button);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(cameraIntent);
            }
        });

        leaderboardButton = (Button) findViewById(R.id.leaderboard_button);
        leaderboardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(leaderboardIntent);
            }
        });

        mapButton = (Button) findViewById(R.id.map_button);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(mapIntent);
            }
        });
    }
}
