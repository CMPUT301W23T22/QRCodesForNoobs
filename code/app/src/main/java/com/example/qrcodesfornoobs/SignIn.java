package com.example.qrcodesfornoobs;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class SignIn extends AppCompatActivity {
    Button signInButton;
    private Intent dashboardIntent;

    ArrayList<Player> playersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin);
        dashboardIntent = new Intent(this, Dashboard.class);
        addListenerOnButtons();

        // Call Firebase to ensure that a potential added username/device/email is unique
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

    private boolean checkUserUniqueness(String username, String email){
        for(Player player:playersList){
            if(Objects.equals(player.getUsername(), username) ||
               Objects.equals(player.getContact().toString(), email)){
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
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final CollectionReference playerReference = db.collection("Players");
        playerReference.addSnapshotListener((value, error) -> {

            playersList = new ArrayList<>();

            for (QueryDocumentSnapshot doc: value){
                String username = (String) doc.get("Username");
                String device = (String) doc.get("Device");
                ContactsContract.CommonDataKinds.Email contact =
                        (ContactsContract.CommonDataKinds.Email) doc.get("Contact");
                playersList.add(new Player(username, device, contact));
            }
        });
    }
}