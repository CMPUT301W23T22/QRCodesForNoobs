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

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

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
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        dataList = new ArrayList<>();

        // Initialize buttons and spinners
        initWidgets();

        // Initialize data adapter and set it to listview
        dataAdapter = new CodeArrayAdapter(this, dataList, getSupportFragmentManager());
        listView.setAdapter(dataAdapter);

        dashboardIntent = new Intent(this, Dashboard.class);

        // Initialize button listeners
        addListenerOnButtons();

        // Firebase Initialization
        initializeFirebase();

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

        // Initialize spinner data
        ArrayAdapter<CharSequence> spinAdapter = ArrayAdapter.createFromResource(this,
                R.array.filter_options, android.R.layout.simple_spinner_item);
        spinAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        sortListSpinner.setAdapter(spinAdapter);
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
                // Hide and show the filter bar
                if (filterBar.getVisibility() == View.VISIBLE){
                    filterBar.setVisibility(View.GONE);
                } else {
                    filterBar.setVisibility(View.VISIBLE);
                }            }
        });
        toggleListViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide and show the listview
                // (Toggle between sliding view and listview)
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

    private void initializeFirebase() {
        FirebaseApp.initializeApp(this);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final CollectionReference playerReference = db.collection("/Players");
        final CollectionReference creatureReference = db.collection("/Creatures");

        playerReference.addSnapshotListener((value, error) -> {
            //TODO: Draw information from the player whose profile is being viewed
        });

        creatureReference.addSnapshotListener((value, error) -> {

            dataList = new ArrayList<>();
            String TAG = "FireBase: ";

            for(QueryDocumentSnapshot doc:value){
                //TODO: Have proper linking with the current player for later when we've established how they link.
                // For the time being, just add all available creatures
                // Also update this when there's a proper way to retrieve creatures from Firebase.
                String name = (String) doc.getData().get("name");
                dataAdapter.add(name);
            }
            dataAdapter.notifyDataSetChanged();
        });
    }
}
