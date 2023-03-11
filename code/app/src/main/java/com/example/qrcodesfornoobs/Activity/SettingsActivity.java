package com.example.qrcodesfornoobs.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.qrcodesfornoobs.R;

public class SettingsActivity extends AppCompatActivity {
    Button backButton;
    private Intent mainIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mainIntent = new Intent(this, MainActivity.class);
        addListenerOnButtons();
    }

    private void addListenerOnButtons() {
        backButton = (Button) findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(mainIntent);
            }
        });
    }
}