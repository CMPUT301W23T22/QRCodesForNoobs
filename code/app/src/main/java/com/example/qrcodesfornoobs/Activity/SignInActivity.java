package com.example.qrcodesfornoobs.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;

import com.example.qrcodesfornoobs.Player;
import com.example.qrcodesfornoobs.databinding.ActivitySigninBinding;
import com.google.firebase.firestore.FirebaseFirestore;


public class SignInActivity extends AppCompatActivity {
    public static final String CACHE_NAME = "SignInCache";
    ActivitySigninBinding binding;
    private Intent mainIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mainIntent = new Intent(this, MainActivity.class);
        if (isLoggedInBefore()) {
            Player.LOCAL_USERNAME = getSharedPreferences(SignInActivity.CACHE_NAME, MODE_PRIVATE).getString("username", "");
            startActivity(mainIntent);
            finish();
            return;
        }
        super.onCreate(savedInstanceState);
        binding = ActivitySigninBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        addListenerOnButtons();

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
        SharedPreferences sharedPreferences = getSharedPreferences(SignInActivity.CACHE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.apply();

        String deviceID = Settings.System.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        Player player = new Player(username, deviceID);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Players")
                .document(username)
                .set(player);
        startActivity(mainIntent);
    }

    private boolean isLoggedInBefore() {
        SharedPreferences sharedPreferences = getSharedPreferences(SignInActivity.CACHE_NAME, MODE_PRIVATE);
        return !sharedPreferences.getString("username", "").isEmpty();
    }

}