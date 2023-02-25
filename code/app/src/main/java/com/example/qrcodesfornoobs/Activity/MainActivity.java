package com.example.qrcodesfornoobs.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.qrcodesfornoobs.Fragment.DashboardFragment;
import com.example.qrcodesfornoobs.Fragment.MapFragment;
import com.example.qrcodesfornoobs.R;
import com.example.qrcodesfornoobs.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationBarView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    private Fragment dashboardFragment;
    private Fragment searchFragment;
    private IntentIntegrator cameraIntentIntegrator;
    private Fragment leaderboardFragment;
    private Fragment mapFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dashboardFragment = new DashboardFragment();
        mapFragment = new MapFragment();

        cameraIntentIntegrator = new IntentIntegrator(this);
        cameraIntentIntegrator.setPrompt("Scan a barcode or QR Code");

        addListenerOnButtons();
        replaceFragment(dashboardFragment);
    }

    private void addListenerOnButtons() {

        binding.bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.home) {
                    System.out.println("home");
                    replaceFragment(dashboardFragment);
                } else if (item.getItemId() == R.id.search) {
                    replaceFragment(dashboardFragment);
//                    startActivity(searchIntent);
                } else if (item.getItemId() == R.id.camera) {
                    cameraIntentIntegrator.initiateScan();
                } else if (item.getItemId() == R.id.leaderboard) {
                    replaceFragment(new DashboardFragment());
//                    startActivity(leaderboardIntent);
                } else if (item.getItemId() == R.id.map) {
                    replaceFragment(mapFragment);
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

    /**
     * Replace fragment that is currently shown in frame_layout with a new one
     * @param fragment
     */

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}