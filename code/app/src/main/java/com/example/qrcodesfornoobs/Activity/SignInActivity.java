package com.example.qrcodesfornoobs.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.qrcodesfornoobs.R;

public class SignInActivity extends AppCompatActivity {
    public static final String SHARED_PREF_NAME = "SignInPreference";
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
        setContentView(R.layout.activity_signin);
        addListenerOnButtons();
    }

    private void addListenerOnButtons() {
        signInButton = (Button) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login("hello");
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