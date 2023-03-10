package com.example.qrcodesfornoobs.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import com.example.qrcodesfornoobs.Player;
import com.example.qrcodesfornoobs.databinding.ActivitySigninBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class SignInActivity extends AppCompatActivity {
    public static final String CACHE_NAME = "SignInCache";
    ActivitySigninBinding binding;
    private Intent mainIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainIntent = new Intent(this, MainActivity.class);
        if (isLoggedInBefore()) {
            Player.LOCAL_USERNAME = getSharedPreferences(SignInActivity.CACHE_NAME, MODE_PRIVATE).getString("username", "");
            startActivity(mainIntent);
            finish();
            return;
        }
        binding = ActivitySigninBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        addListenerOnButtons();

        binding.usernameEditText.requestFocus();
    }

    private void addListenerOnButtons() {
        binding.signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptToLogin(binding.usernameEditText.getText().toString());
            }
        });
    }

    private void attemptToLogin(String username) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference playerRef = db.collection("Players").document(username);
        String deviceID = Settings.System.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        Player localPlayer = new Player(username, deviceID);
        playerRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Player dbPlayer = document.toObject(Player.class);
                        if (localPlayer.equals(dbPlayer)) {
                            login(localPlayer,false);
                            Toast.makeText(getBaseContext(), "Welcome back!", Toast.LENGTH_SHORT).show();
                        }
                    } else { // login as new user
                        login(localPlayer, true);
                        Toast.makeText(getBaseContext(), "Welcome!", Toast.LENGTH_SHORT).show();
                    }
                    return;
                }
                Toast.makeText(getBaseContext(), "Cannot connect to server!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void login(Player localPlayer, boolean isNewUser) {
        SharedPreferences sharedPreferences = getSharedPreferences(SignInActivity.CACHE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", localPlayer.getUsername());
        editor.apply();
        Player.LOCAL_USERNAME = localPlayer.getUsername();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if (isNewUser) {
            db.collection("Players")
                    .document(localPlayer.getUsername())
                    .set(localPlayer);
        }

        startActivity(mainIntent);
    }

    private boolean isLoggedInBefore() {
        SharedPreferences sharedPreferences = getSharedPreferences(SignInActivity.CACHE_NAME, MODE_PRIVATE);
        return !sharedPreferences.getString("username", "").isEmpty();
    }

}