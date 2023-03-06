package com.example.qrcodesfornoobs.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.qrcodesfornoobs.Creature;
import com.example.qrcodesfornoobs.Fragment.DashboardFragment;
import com.example.qrcodesfornoobs.Fragment.LeaderboardFragment;
import com.example.qrcodesfornoobs.Fragment.MapFragment;
import com.example.qrcodesfornoobs.Fragment.SearchFragment;
import com.example.qrcodesfornoobs.R;
import com.example.qrcodesfornoobs.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    final String TAG = "";
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

        initializeFragments();

        addListenerOnButtons();
        replaceFragment(dashboardFragment);
    }

    private void initializeFragments() {
        dashboardFragment = new DashboardFragment();
        mapFragment = new MapFragment();
        leaderboardFragment = new LeaderboardFragment();
        searchFragment = new SearchFragment();
        cameraIntentIntegrator = new IntentIntegrator(this);
        cameraIntentIntegrator.setPrompt("Scan a barcode or QR Code");
    }

    private void addListenerOnButtons() {

        binding.bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.home) {
                    System.out.println("home");
                    replaceFragment(dashboardFragment);
                } else if (item.getItemId() == R.id.search) {
                    replaceFragment(searchFragment);
                } else if (item.getItemId() == R.id.camera) {
                    cameraIntentIntegrator.initiateScan();
                } else if (item.getItemId() == R.id.leaderboard) {
                    replaceFragment(leaderboardFragment);
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

    /**
    Adds a creature to the Firebase db
     @param creature
     */

    public void addCreature(Creature creature) {

// Add a new document with a generated ID
        db.collection("QRCodePath")
                .add(creature)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "Creature added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding creature", e);
                    }
                });

    }


    /**
    Retrieves all creatures in the db
     */
    public void getCreatures() {

        db.collection("QRCodePath")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w(TAG, "Error getting creatures", task.getException());
                        }
                    }
                });
    }
}