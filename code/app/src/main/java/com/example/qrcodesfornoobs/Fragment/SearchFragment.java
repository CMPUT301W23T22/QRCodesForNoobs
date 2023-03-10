package com.example.qrcodesfornoobs.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
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

import com.example.qrcodesfornoobs.R;
import com.example.qrcodesfornoobs.SearchAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;


public class SearchFragment extends Fragment {
    private RadioGroup radioGroup;

    private SearchView searchView;
    private RecyclerView recyclerView;

    // For Firebase
    private ArrayList<String> valueList;
    private SearchAdapter searchAdapter;
    private FirebaseFirestore db;
    private CollectionReference collectionReference;
    private String field;

    private ArrayList<String> searchList;

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        collectionReference = db.collection("Players");
        valueList = new ArrayList<>();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        radioGroup = view.findViewById(R.id.radioGroup);
        recyclerView = view.findViewById(R.id.recyclerView);
        searchView = view.findViewById(R.id.searchView);
        searchView.setIconified(false);


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        searchAdapter = new SearchAdapter(getContext(), valueList);
        recyclerView.setAdapter(searchAdapter);

        radioGroupCheck(db, radioGroup);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
        //TODO: Query works with username, need Location documentPath and change hardcoded field "username" to work with location as well
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchList = new ArrayList<>();
                if (query.length() > 0) {
                    searchView.clearFocus();
                    //TODO: Bug: searching finds values greater than query
                    // ex. testinggg --> thonas
                    // query value less than others works correctly
                    collectionReference.whereEqualTo("username", query).get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful()){

                                        if(!task.getResult().isEmpty()) { // if query exists
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                Log.d("equalToQuery", document.getId() + " => " + document.getData());
                                                searchList.add(String.valueOf(document.getData().get("username")));

                                                collectionReference.document(query).get()
                                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                Query moreQuery = collectionReference
                                                                        .orderBy("username")
                                                                        .startAt(documentSnapshot)
                                                                        .limit(10);

                                                                moreQuery.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                        if(task.isSuccessful()){
                                                                            for(QueryDocumentSnapshot moreDoc : task.getResult()){
                                                                                Log.d("existingQueryResult", moreDoc.getId()+ " => "+moreDoc.getData());
                                                                                if(!searchList.contains(moreDoc.getData().get("username").toString())) {
                                                                                    searchList.add(moreDoc.getData().get("username").toString());
                                                                                }
                                                                            }
                                                                        }
                                                                        else{ // moreQuery failed
                                                                            Log.d("existingQueryResult", "Error getting documents:", task.getException());
                                                                        }

                                                                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                                                                        recyclerView.setLayoutManager(layoutManager);

                                                                        SearchAdapter searchAdapter = new SearchAdapter(getContext(), searchList);
                                                                        recyclerView.setAdapter(searchAdapter);
                                                                    }
                                                                });
                                                            }
                                                        });

                                            }
                                        }// end task.getResult().isEmpty()
                                        else{
                                            Log.d("EqualToQuery", "Value '"+query+ "' does not exist in any document.");
                                            // See if other values startAt(query)
                                            collectionReference.orderBy("username").startAt(query).limit(10).get()
                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                            if(task.isSuccessful()){
                                                                for(QueryDocumentSnapshot neDoc: task.getResult()){
                                                                    Log.d("nonExistingQuery", neDoc.getId()+" => "+neDoc.getData());
                                                                    searchList.add(neDoc.getData().get("username").toString());
                                                                }
                                                            }
                                                            else{
                                                                Log.d("nonExistingQuery", "Error getting documents: ", task.getException());
                                                            }
                                                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                                                            recyclerView.setLayoutManager(layoutManager);

                                                            SearchAdapter searchAdapter = new SearchAdapter(getContext(), searchList);
                                                            recyclerView.setAdapter(searchAdapter);
                                                        }
                                                    });

                                        }

                                    } // end task.isSuccessful()
                                    else{
                                        Log.d("EqualToQuery", "Error getting documents: ", task.getException());
                                    }
                                }
                            });
                }
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        radioGroup.clearCheck();
    }

    public void radioGroupCheck (FirebaseFirestore db, RadioGroup radioGroup){
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.radioUser:
                        collectionReference = db.collection("Players");
                        field = "username";
                        break;
                    case R.id.radioLocation:
                        collectionReference = db.collection("Creatures");
                        field = "location";
                        break;
                }
                Log.d("RADIO_SELECTION", field);
                collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable
                            FirebaseFirestoreException error) {

                        // Clear the old list
                        valueList.clear();
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            // doc.get(field) gets document name only
                            String value = String.valueOf(doc.getData().get("username")); // field value
                            valueList.add(value);
                        }

                        searchAdapter.notifyDataSetChanged(); // Notifying the adapter to render any new data fetched from the cloud
                    }
                });
            }
        });
    }
}
