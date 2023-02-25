package com.example.qrcodesfornoobs.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.qrcodesfornoobs.R;

public class SignInActivity extends AppCompatActivity {
    Button signInButton;
    private Intent mainIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        mainIntent = new Intent(this, MainActivity.class);
        addListenerOnButtons();
    }

    private void addListenerOnButtons() {
        signInButton = (Button) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(mainIntent);
            }
        });
    }
}