package com.example.qrcodesfornoobs;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;

public class Dashboard extends AppCompatActivity {
    ImageButton profileImageButton;
    ImageButton settingImageButton;
    SliderView codeSliderView;
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
        setUpSliders();
    }

    private void setUpSliders() {
        String test_url1 = "https://www.geeksforgeeks.org/wp-content/uploads/gfg_200X200-1.png";
        String test_url2 = "https://bizzbucket.co/wp-content/uploads/2020/08/Life-in-The-Metro-Blog-Title-22.png";

        codeSliderView = findViewById(R.id.dashboard_sliderView);
        ArrayList<String> codeURLs = new ArrayList<>();
        codeURLs.add(test_url1);
        codeURLs.add(test_url2);

        CodeSliderAdapter adapter = new CodeSliderAdapter(this, codeURLs);
        codeSliderView.setSliderAdapter(adapter);
    }


    private void addListenerOnButtons() {
        profileImageButton = (ImageButton) findViewById(R.id.profile_imageButton);
        profileImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(profileIntent);
            }
        });

        settingImageButton = (ImageButton) findViewById(R.id.setting_imageButton);
        settingImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(settingsIntent);
            }
        });

//        homeButton = (Button) findViewById(R.id.dashboard_button);
//        homeButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(dashboardIntent);
//            }
//        });
//
//        searchButton = (Button) findViewById(R.id.search_button);
//        searchButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(searchIntent);
//            }
//        });
//
//        cameraButton = (Button) findViewById(R.id.camera_button);
//        cameraButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(cameraIntent);
//            }
//        });
//
//        leaderboardButton = (Button) findViewById(R.id.leaderboard_button);
//        leaderboardButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(leaderboardIntent);
//            }
//        });
//
//        mapButton = (Button) findViewById(R.id.map_button);
//        mapButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(mapIntent);
//            }
//        });
    }
}
