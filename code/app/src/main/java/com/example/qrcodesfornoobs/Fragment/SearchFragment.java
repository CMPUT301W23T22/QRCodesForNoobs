package com.example.qrcodesfornoobs.Fragment;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.example.qrcodesfornoobs.ProfileCodeArrayAdapter;
import com.example.qrcodesfornoobs.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class SearchFragment extends Fragment {
    private RadioGroup radioGroup;

    private SearchView searchView;
    private RecyclerView recyclerView;

    // For Firebase
    private ArrayList<String> dataList;
    private ProfileCodeArrayAdapter codeArrayAdapter;
    CollectionReference collectionReference;
    private String field;

    private ArrayList<String> searchList;

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();


        View view = inflater.inflate(R.layout.fragment_search, container, false); // Inflate the layout for this fragment
        dataList = new ArrayList<>();

//
        // Radio Set Up
        radioGroup = view.findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch(i){
                    case R.id.radioUser:
                        collectionReference = db.collection("Players");
                        field = "Username";
                        break;
                    case R.id.radioLocation:
                        collectionReference = db.collection("Locations");
                        field = "City";
                        break;
                }
//                 temp: adding samples
                 collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable
                    FirebaseFirestoreException error) {

                    // Clear the old list
                    dataList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        //String QR = doc.getId();
                        String Username = (String) doc.get(field);
                        dataList.add(Username); // Adding from FireStore
                    }
                    codeArrayAdapter.notifyDataSetChanged(); // Notifying the adapter to render any new data fetched from the cloud
                    }
                });
            }
        });

        // Initializing recycler and search views
        recyclerView = view.findViewById(R.id.recyclerView);
        searchView = view.findViewById(R.id.searchView);
        searchView.setIconified(false);
        searchView.clearFocus();

        // Setting adapter
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        codeArrayAdapter = new ProfileCodeArrayAdapter(getContext(), dataList);
        recyclerView.setAdapter(codeArrayAdapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchList = new ArrayList<>();

                if(query.length() > 0){
                    for(int i = 0; i < dataList.size(); i++){
                        if(dataList.get(i).toUpperCase().contains(query.toUpperCase()) || dataList.get(i).toUpperCase().contains(query.toUpperCase())){
                            String str = dataList.get(i);
                            searchList.add(str);
                        }
                    }
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                    recyclerView.setLayoutManager(layoutManager);

                    ProfileCodeArrayAdapter profileCodeArrayAdapter = new ProfileCodeArrayAdapter(getContext(), dataList);
                    recyclerView.setAdapter(profileCodeArrayAdapter);
                }
                else{
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                    recyclerView.setLayoutManager(layoutManager);

                    ProfileCodeArrayAdapter profileCodeArrayAdapter = new ProfileCodeArrayAdapter(getContext(), dataList);
                    recyclerView.setAdapter(profileCodeArrayAdapter);

                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchList = new ArrayList<>();

                if(newText.length() > 0){
                    for(int i = 0; i < dataList.size(); i++){
                        if(dataList.get(i).toUpperCase().contains(newText.toUpperCase())){
                            String str = dataList.get(i);
                            searchList.add(str);
                        }
                    }
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                    recyclerView.setLayoutManager(layoutManager);

                    ProfileCodeArrayAdapter profileCodeArrayAdapter = new ProfileCodeArrayAdapter(getContext(), searchList);
                    recyclerView.setAdapter(profileCodeArrayAdapter);
                }
                else{
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                    recyclerView.setLayoutManager(layoutManager);

                    ProfileCodeArrayAdapter profileCodeArrayAdapter = new ProfileCodeArrayAdapter(getContext(), searchList);
                    recyclerView.setAdapter(profileCodeArrayAdapter);
                }
                return false;
            }
        });

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }



}