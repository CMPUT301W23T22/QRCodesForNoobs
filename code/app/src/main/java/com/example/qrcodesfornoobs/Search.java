package com.example.qrcodesfornoobs;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class Search extends AppCompatActivity {
    Button backButton;
    private Intent dashboardIntent;

    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private int selectRadioId;

    private SearchView searchView;
    private RecyclerView recyclerView;
    private ArrayList<SearchUser> arrayList = new ArrayList<>();
    private ArrayList<SearchUser> searchList;
    String[] userList = new String[]{"Dog","Cat","Bird"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        dashboardIntent = new Intent(this, Dashboard.class);
        addListenerOnButtons();

        // Radio
        //radioGroup = findViewById(R.id.radioGroup);


        // List
        recyclerView = findViewById(R.id.recyclerView);
        searchView = findViewById(R.id.searchView);
        searchView.setIconified(false);
        searchView.clearFocus();

        // adding samples
        for (int i = 0; i < userList.length; i++){
            SearchUser searchUser = new SearchUser();
            searchUser.setUsername(userList[i]);
            arrayList.add(searchUser);
        }

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(Search.this);
        recyclerView.setLayoutManager(layoutManager);

        SearchUserAdapter searchUserAdapter = new SearchUserAdapter(Search.this, arrayList);
        recyclerView.setAdapter(searchUserAdapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchList = new ArrayList<>();

                if(query.length() > 0){
                    for(int i = 0; i < arrayList.size(); i++){
                        if(arrayList.get(i).getUsername().toUpperCase().contains(query.toUpperCase()) || arrayList.get(i).getUsername().toUpperCase().contains(query.toUpperCase())){
                            SearchUser searchUser = new SearchUser();
                            searchUser.setUsername(arrayList.get(i).getUsername());
                            searchList.add(searchUser);
                        }
                    }
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(Search.this);
                    recyclerView.setLayoutManager(layoutManager);

                    SearchUserAdapter searchUserAdapter = new SearchUserAdapter(Search.this, searchList);
                    recyclerView.setAdapter(searchUserAdapter);
                }
                else{
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(Search.this);
                    recyclerView.setLayoutManager(layoutManager);

                    SearchUserAdapter searchUserAdapter = new SearchUserAdapter(Search.this, arrayList);
                    recyclerView.setAdapter(searchUserAdapter);

                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchList = new ArrayList<>();

                if(newText.length() > 0){
                    for(int i = 0; i < arrayList.size(); i++){
                        if(arrayList.get(i).getUsername().toUpperCase().contains(newText.toUpperCase()) || arrayList.get(i).getUsername().toUpperCase().contains(newText.toUpperCase())){
                            SearchUser searchUser = new SearchUser();
                            searchUser.setUsername(arrayList.get(i).getUsername());
                            searchList.add(searchUser);
                        }
                    }
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(Search.this);
                    recyclerView.setLayoutManager(layoutManager);

                    SearchUserAdapter searchUserAdapter = new SearchUserAdapter(Search.this, searchList);
                    recyclerView.setAdapter(searchUserAdapter);
                }
                else{
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(Search.this);
                    recyclerView.setLayoutManager(layoutManager);

                    SearchUserAdapter searchUserAdapter = new SearchUserAdapter(Search.this, arrayList);
                    recyclerView.setAdapter(searchUserAdapter);

                }
                return false;
            }
        });
    }


    public void onClickRadio(View view){
        selectRadioId = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(selectRadioId);
        if(selectRadioId == 1){
            Toast.makeText(Search.this, "Selected", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(Search.this, radioButton.getText(), Toast.LENGTH_SHORT).show();
        }
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