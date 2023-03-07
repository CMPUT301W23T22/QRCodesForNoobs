package com.example.qrcodesfornoobs.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.qrcodesfornoobs.Player;
import com.example.qrcodesfornoobs.R;
import com.example.qrcodesfornoobs.databinding.ActivitySigninBinding;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class SignInActivity extends AppCompatActivity {
    public static final String SHARED_PREF_NAME = "SignInPreference";
    ActivitySigninBinding binding;
    Button signInButton;
    private Intent mainIntent;
    ArrayList<Player> playersList;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    final CollectionReference playerReference = db.collection("Players");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mainIntent = new Intent(this, MainActivity.class);
        if (isLoggedInBefore()) {
            startActivity(mainIntent);
            finish();
            return;
        }
        super.onCreate(savedInstanceState);
        binding = ActivitySigninBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        addListenerOnButtons();
        initiateFirebase();

        binding.usernameEditText.requestFocus();
    }

    private void addListenerOnButtons() {
        binding.signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login(binding.usernameEditText.getText().toString());
            }
        });
    }

    private void login(String username) {
        SharedPreferences sharedPreferences = getSharedPreferences(SignInActivity.SHARED_PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("hasLoggedInBefore", true);
        editor.commit();

        String device = Settings.System.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        HashMap<String, Object> data = new HashMap<>();
        data.put("Username", username);
        data.put("DeviceID", device);
        playerReference
                .document(device)
                .set(data);
        startActivity(mainIntent);
    }

    private boolean isLoggedInBefore() {
        SharedPreferences sharedPreferences = getSharedPreferences(SignInActivity.SHARED_PREF_NAME, MODE_PRIVATE);
        return sharedPreferences.getBoolean("hasLoggedInBefore", false);
    }

    private boolean checkDeviceUniqueness() {
        String deviceID = Settings.Secure.ANDROID_ID;
        for (Player player : playersList) {
            if (Objects.equals(player.getDevice(), deviceID)) {
                return false;
            }
        }
        return true;
    }
    private boolean checkUserUniqueness(String username) {
        for (Player player : playersList) {
            if (Objects.equals(player.getUsername(), username)) {
                return false;
            }
        }
        return true;
    }

    private void initiateFirebase() {
        playerReference.addSnapshotListener((queryDocumentSnapshots, error) -> {

            playersList = new ArrayList<>();

            for(QueryDocumentSnapshot doc: queryDocumentSnapshots)
            {
                String user = (String) doc.get("Username");
                String device = (String) doc.get("DeviceID");
                playersList.add(new Player(user, device));
            }
        });

    }
}