package com.example.qrcodesfornoobs;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qrcodesfornoobs.databinding.DashboardBinding;
import com.google.android.material.navigation.NavigationBarView;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;

public class Dashboard extends AppCompatActivity {

    DashboardBinding binding; // xml binding class (to avoid using R.id.)

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
        binding = DashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        setContentView(R.layout.dashboard);

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

        ArrayList<String> codeURLs = new ArrayList<>();
        codeURLs.add(test_url1);
        codeURLs.add(test_url2);

        CodeSliderAdapter adapter = new CodeSliderAdapter(this, codeURLs);
        binding.dashboardSliderView.setSliderAdapter(adapter);
    }


    private void addListenerOnButtons() {
        binding.profileImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(profileIntent);
            }
        });

        binding.settingImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(settingsIntent);
            }
        });

        binding.bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        startActivity(dashboardIntent);
                        break;
                    case R.id.search:
                        startActivity(searchIntent);
                        break;
                    case R.id.camera:
                        startActivity(cameraIntent);
                        break;
                    case R.id.leaderboard:
                        startActivity(leaderboardIntent);
                        break;
                    case R.id.map:
                        startActivity(mapIntent);
                        break;

                }
                return true;
            }
        });
    }
}
