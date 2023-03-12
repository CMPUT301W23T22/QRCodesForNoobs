package com.example.qrcodesfornoobs.Fragment;

import android.content.Intent;
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

import com.example.qrcodesfornoobs.Activity.ProfileActivity;
import com.example.qrcodesfornoobs.R;
import com.example.qrcodesfornoobs.Adapter.SearchAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * A Fragment subclass that displays a search feature for querying player data stored in the Firestore database.
 * The user can search for player usernames and locations.
 * Results are displayed in a RecyclerView.
 */
public class SearchFragment extends Fragment implements SearchAdapter.RecyclerViewInterface {
    private RadioGroup radioGroup;

    private SearchView searchView;
    private RecyclerView recyclerView;

    // For Firebase
    private ArrayList<String> valueList;
    private SearchAdapter searchAdapter;
    private FirebaseFirestore db;
    private CollectionReference collectionReference;
    private String field;

    private Intent profileIntent;
    private ArrayList<String> searchList;
    private SearchAdapter.RecyclerViewInterface rvInterface;

    /**
     * Empty public constructor
     */
    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Initializes the Firebase Firestore instance and CollectionReference.
     * Initializes an empty ArrayList to store search results.
     *
     * @param savedInstanceState A Bundle object containing the instance's previously saved state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        collectionReference = db.collection("Players");
        valueList = new ArrayList<>();
    }

    /**
     * Inflates the layout for this Fragment.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the Fragment.
     * @param container The parent view that the Fragment's UI should be attached to.
     * @param savedInstanceState A Bundle object containing the instance's previously saved state.
     * @return A View object inflated from the fragment_search.xml layout file.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        return view;
    }

    /**
     * Initializes the UI elements and RecyclerView adapter for this Fragment.
     * Sets the OnQueryTextListener for the SearchView.
     *
     * @param view The View returned by onCreateView().
     * @param savedInstanceState A Bundle object containing the instance's previously saved state.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        radioGroup = view.findViewById(R.id.radioGroup);
        recyclerView = view.findViewById(R.id.recyclerView);
        searchView = view.findViewById(R.id.searchView);
        searchView.setIconified(false);


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        rvInterface = new SearchAdapter.RecyclerViewInterface() {
            @Override
            public void onItemClick(int pos) {
                launchPlayerProfile(pos);
            }
        };

        searchAdapter = new SearchAdapter(getContext(), valueList, rvInterface);
        recyclerView.setAdapter(searchAdapter);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

        //TODO: Query works with username, need Location documentPath and change hardcoded field "username" to work with location as well
            @Override
            public boolean onQueryTextSubmit(String query) {


                // Added this inside the onQueryTextSubmit portion so users had to search for the names to pop up
                radioGroupCheck(db, radioGroup);


                searchList = new ArrayList<>();
                if (query.length() > 0) {
                    searchView.clearFocus();
                    //TODO: Bug: searching finds values greater than query
                    // ex. 'testinggg' --> 'thonas'
                    // query value less than others works correctly
                    // Doesn't account for lowercase uppercase (uppercase < lowercase)
                    // ex. Search: 'p' doesn't show 'Pola'
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

                                                                        SearchAdapter searchAdapter = new SearchAdapter(getContext(), searchList, rvInterface);
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

                                                            SearchAdapter searchAdapter = new SearchAdapter(getContext(), searchList, rvInterface);
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

    /**
     * Called when the fragment is visible to the user and actively running
     */
    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * Called when the Fragment is no longer resumed
     * Clears the focus from the SearchView when the Fragment is paused.
     */
    @Override
    public void onPause() {
        super.onPause();
        radioGroup.clearCheck();
        searchView.setQuery("", false);
        searchView.clearFocus();
    }

    /**
     * Queries the Firestore database based on the selected radio button.
     *
     * @param db The FirebaseFirestore instance.
     * @param radioGroup The RadioGroup containing the radio buttons for the search type.
     */
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

    /**
     * Callback function for item click events in the RecyclerView.
     *
     * @param pos The position of the clicked item in the RecyclerView.
     */
    @Override
    public void onItemClick(int pos) {

    }

    /**
     * Launches the ProfileActivity with the document path of the clicked item.
     *
     * @param pos The position of the clicked item in the RecyclerView.
     */
    private void launchPlayerProfile(int pos){
        profileIntent = new Intent(getActivity(), ProfileActivity.class);
        if (searchList != null){
            String userToOpen = searchList.get(pos);
            System.out.println(userToOpen);
            profileIntent.putExtra("userToOpen",userToOpen);
            getActivity().startActivity(profileIntent);
        }
    }
}
