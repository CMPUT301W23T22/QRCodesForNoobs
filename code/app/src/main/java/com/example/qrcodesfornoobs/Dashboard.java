package com.example.qrcodesfornoobs;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qrcodesfornoobs.Activity.SettingsActivity;
import com.example.qrcodesfornoobs.Adapter.CodeSliderAdapter;
import com.example.qrcodesfornoobs.databinding.DashboardBinding;
import com.google.android.material.navigation.NavigationBarView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
// TODO: Delete with Search.java, using DashboardFragment and SearchFragment instead.

public class Dashboard extends AppCompatActivity {

    DashboardBinding binding; // xml binding class (to avoid using R.id.)

    private Intent profileIntent;
    private Intent settingsIntent;
    private Intent searchIntent;
    private Intent leaderboardIntent;
    private Intent dashboardIntent;
    private IntentIntegrator cameraIntentIntegrator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        setContentView(R.layout.dashboard);

        profileIntent = new Intent(this, Profile.class);
        settingsIntent = new Intent(this, SettingsActivity.class);
        searchIntent = new Intent(this, Search.class);
        leaderboardIntent = new Intent(this, Leaderboard.class);
        dashboardIntent = new Intent(this, Dashboard.class);
        cameraIntentIntegrator = new IntentIntegrator(Dashboard.this);
        cameraIntentIntegrator.setPrompt("Scan a barcode or QR Code");
        addListenerOnButtons();
        setUpSliders();
    }

    private void setUpSliders() {
        String test_url1 = "https://i.insider.com/57910997dd0895a56e8b456d?width=700&format=jpeg&auto=webp";
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
                if (item.getItemId() == R.id.home) {
                    startActivity(dashboardIntent);
                } else if (item.getItemId() == R.id.search) {
                    startActivity(searchIntent);
                } else if (item.getItemId() == R.id.camera) {
                    cameraIntentIntegrator.initiateScan();
                } else if (item.getItemId() == R.id.leaderboard) {
                    startActivity(leaderboardIntent);
                } else if (item.getItemId() == R.id.map) {
//                    startActivity(mapIntent);
                }
                return true;
            }
        });
    }

    /**
     * Process scanned code info
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null) { // if it's the result of the scan
            if (intentResult.getContents() == null) { // if user cancelled
                Toast.makeText(getBaseContext(), "Cancelled", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getBaseContext(), intentResult.getContents(), Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
