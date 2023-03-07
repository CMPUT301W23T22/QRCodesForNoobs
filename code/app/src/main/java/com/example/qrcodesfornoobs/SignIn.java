package com.example.qrcodesfornoobs;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class SignIn extends AppCompatActivity {
    Button signInButton;
    EditText usernameEditText;
    private Intent dashboardIntent;

    ArrayList<Player> playersList;
    FirebaseFirestore db;
    CollectionReference playerReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        dashboardIntent = new Intent(this, Dashboard.class);
        usernameEditText = findViewById(R.id.username_EditText);
        addListenerOnButtons();
        initializeFirebase();
    }

    private boolean checkDeviceUniqueness() {
        String deviceID = Settings.Secure.ANDROID_ID;
        for(Player player:playersList){
            if(Objects.equals(player.getDevice(), deviceID)){
                return false;
            }
        }
        return true;
    }

    private boolean checkUserUniqueness(String username){
        for(Player player:playersList){
            if(Objects.equals(player.getUsername(), username)){
                return false;
            }
        }
        return true;
    }

    private void addListenerOnButtons() {
        signInButton = (Button) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(dashboardIntent);
            }
        });
    }

    private void initializeFirebase(){

        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();
        playerReference = db.collection("Players");

        playerReference.addSnapshotListener((value, error) -> {

            playersList = new ArrayList<>();

            for (QueryDocumentSnapshot doc: value){
                String username = (String) doc.get("Username");
                String device = (String) doc.get("DeviceID");
                playersList.add(new Player(username, device));
            }
        });
    }
}