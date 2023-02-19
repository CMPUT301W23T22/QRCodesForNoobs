package com.example.qrcodesfornoobs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

public class Profile extends AppCompatActivity {
    Button backButton;
    Button toggleButton;
    ListView listView;
    private Intent dashboardIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        listView = findViewById(R.id.list_view);
        dashboardIntent = new Intent(this, Dashboard.class);


        addListenerOnButtons();
    }

    private void addListenerOnButtons() {
        backButton = (Button) findViewById(R.id.back_button);
        toggleButton = (Button) findViewById(R.id.toggle_listview_button);
        backButton.setBackgroundResource(R.drawable.back_arrow);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(dashboardIntent);
            }
        });
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listView.getVisibility() == View.VISIBLE){
                    listView.setVisibility(View.GONE);
                } else {
                    listView.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
