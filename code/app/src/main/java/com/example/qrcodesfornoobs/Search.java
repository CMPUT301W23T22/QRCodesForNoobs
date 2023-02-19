package com.example.qrcodesfornoobs;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qrcodesfornoobs.Dashboard;
import com.example.qrcodesfornoobs.searchUser;

import java.util.ArrayList;

public class Search extends AppCompatActivity {
    Button backButton;
    private Intent dashboardIntent;

    private ArrayList<searchUser> usersList;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        dashboardIntent = new Intent(this, Dashboard.class);
        addListenerOnButtons();

        recyclerView = findViewById(R.id.searchRecyclerView);
        usersList = new ArrayList<>();

        setUserInfo();
        setAdapter();

    }

    private void setAdapter() {
        recyclerAdapter adapter = new recyclerAdapter(usersList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    private void setUserInfo() {
        usersList.add(new searchUser("Dog"));
        usersList.add(new searchUser("Dog2"));
        usersList.add(new searchUser("Dog3"));
    }

    private void addListenerOnButtons() {
        backButton = (Button) findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(dashboardIntent);
            }
        });
    }
}