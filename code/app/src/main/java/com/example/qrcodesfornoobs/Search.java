package com.example.qrcodesfornoobs;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
// TODO: delete with Dashboard.java, using DashboardFragment and SearchFragment instead


public class Search extends AppCompatActivity {
    private Button backButton;
    private Intent dashboardIntent;

    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private int selectRadioId;

    private SearchView searchView;
    private RecyclerView recyclerView;
    private ArrayList<String> arrayList = new ArrayList<>();
    private ArrayList<String> searchList;
    String[] userList = new String[]{"Dog","Cat","Bird"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        dashboardIntent = new Intent(this, Dashboard.class);


        // Radio
        //radioGroup = findViewById(R.id.radioGroup);


        // List
        recyclerView = findViewById(R.id.recyclerView);
        searchView = findViewById(R.id.searchView);
        searchView.setIconified(false);
        searchView.clearFocus();


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(Search.this);
        recyclerView.setLayoutManager(layoutManager);

        SearchAdapter searchUserAdapter = new SearchAdapter(Search.this, arrayList);
        recyclerView.setAdapter(searchUserAdapter);
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