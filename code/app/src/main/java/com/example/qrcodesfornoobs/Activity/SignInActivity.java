package com.example.qrcodesfornoobs.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.qrcodesfornoobs.R;
import com.example.qrcodesfornoobs.databinding.ActivitySigninBinding;

public class SignInActivity extends AppCompatActivity {
    public static final String SHARED_PREF_NAME = "SignInPreference";
    ActivitySigninBinding binding;
    Button signInButton;
    private Intent mainIntent;

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
        // TODO: commit username to db
        startActivity(mainIntent);
    }

    private boolean isLoggedInBefore() {
        SharedPreferences sharedPreferences = getSharedPreferences(SignInActivity.SHARED_PREF_NAME, MODE_PRIVATE);
        return sharedPreferences.getBoolean("hasLoggedInBefore", false);
    }
}