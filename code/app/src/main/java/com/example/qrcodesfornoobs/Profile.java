package com.example.qrcodesfornoobs;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class Profile extends AppCompatActivity {
    Button backButton;
    ImageButton toggleFilterButton;
    ImageButton toggleListViewButton;
    ImageButton toggleSortButton;
    Spinner sortListSpinner;
    ListView listView;

    LinearLayout filterBar;
    private Intent dashboardIntent;

    private ArrayList<String> dataList;
    private ArrayAdapter<String> dataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        dataList = new ArrayList<>();
        dataList.add("NAME 1");
        dataList.add("NAME 2");
        initWidgets();

        ArrayAdapter<CharSequence> spinAdapter = ArrayAdapter.createFromResource(this,
                R.array.filter_options, android.R.layout.simple_spinner_item);
        spinAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        sortListSpinner.setAdapter(spinAdapter);
        dataAdapter = new CodeArrayAdapter(this, dataList);
        listView.setAdapter(dataAdapter);

        dashboardIntent = new Intent(this, Dashboard.class);

        addListenerOnButtons();
    }
    private void initWidgets(){
        backButton = findViewById(R.id.back_button);
        backButton.setBackgroundResource(R.drawable.back_arrow);
        toggleFilterButton = findViewById(R.id.toggle_filterbar_button);
        toggleListViewButton = findViewById(R.id.toggle_listview_button);
        toggleSortButton = findViewById(R.id.sort_listview_button);
        filterBar = findViewById(R.id.filterbar);
        sortListSpinner = findViewById(R.id.sort_list_spinner);
        listView = findViewById(R.id.list_view);
    }

    private void addListenerOnButtons() {

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(dashboardIntent);
            }
        });
        toggleFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (filterBar.getVisibility() == View.VISIBLE){
                    filterBar.setVisibility(View.GONE);
                } else {
                    filterBar.setVisibility(View.VISIBLE);
                }            }
        });
        toggleListViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listView.getVisibility() == View.VISIBLE){
                    toggleListViewButton.setImageResource(R.drawable.face_icon);
                    listView.setVisibility(View.GONE);
                } else {
                    toggleListViewButton.setImageResource(R.drawable.menu_icon);
                    listView.setVisibility(View.VISIBLE);
                }
            }
        });

    }
}
