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
 * This class defines the search UI fragment that shows on click of the search icon.
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
     * On create of the fragment, initializes non-view components of the class.
     *
     * @param savedInstanceState Unused. Bundle to be used if carrying information over from parent activity.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        collectionReference = db.collection("Players");
        valueList = new ArrayList<>();
    }

    /**
     * Initializes view to be used.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        return view;
    }

    /**
     * After successful creation of view, initialize xml components.
     * Contains searching function and click to view player profile function.
     *
     * @param view
     * @param savedInstanceState
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

                //TODO: implement radiobutton check when we can get location
                // Added this inside the onQueryTextSubmit portion so users had to search for the names to pop up
                //radioGroupCheck(db, radioGroup);


                searchList = new ArrayList<>();
                if (query.length() > 0) {
                    submitQuery(query);
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
     * Called when app is closed or when switching fragments/activities.
     * Clears the radio group selection and any SearchView text.
     */
    @Override
    public void onPause() {
        super.onPause();
        radioGroup.clearCheck();
        searchView.setQuery("", false);
        searchView.clearFocus();
    }

    /**
     * On submit of a nonempty query text, first checks if the exact query is in any Firebase document.
     * If an exact query document exists, adds that document to a list.
     * Then uses that query to look for more documents that has the query with more characters.
     * Add those documents to a list.
     * <p>
     * If the query does not exist, use the query to look for documents that has the query with more characters.
     * Add those documents to a list.
     * <p>
     * Notify the RecyclerView's adapter to display the list of query documents.
     *
     * @param query User inputted string to be used to find documents in a Firebase collection.
     */
    public void submitQuery(String query) {
        searchView.clearFocus();
        //TODO: Bug: searching finds values greater than query
        // ex. 'testinggg' --> 'thonas'
        // query value less than others works correctly
        // Doesn't account for lowercase uppercase (uppercase < lowercase)
        // ex. Search: 'p' doesn't show 'Pola'

        collectionReference.orderBy("username").startAt(query.toUpperCase()).endAt(query.toUpperCase() + "\uf8ff").startAt(query).endAt(query + "\uf8ff").limit(10).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot neDoc : task.getResult()) {
                                Log.d("nonExistingQuery", neDoc.getId() + " => " + neDoc.getData());
                                searchList.add(neDoc.getData().get("username").toString());
                            }
                        } else {
                            Log.d("nonExistingQuery", "Error getting documents: ", task.getException());
                        }
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                        recyclerView.setLayoutManager(layoutManager);

                        SearchAdapter searchAdapter = new SearchAdapter(getContext(), searchList, rvInterface);
                        recyclerView.setAdapter(searchAdapter);
                    }
            });
    }

    /**
     * Checks which radio button is selected and changes the Firebase collection path accordingly.
     *
     * @param db         Firebase database collection.
     * @param radioGroup Contains radio buttons.
     */
    public void radioGroupCheck(FirebaseFirestore db, RadioGroup radioGroup) {
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
     * Opens a player's profile on click of a searched user.
     *
     * @param pos Position of selected searched user.
     */
    private void launchPlayerProfile(int pos) {
        profileIntent = new Intent(getActivity(), ProfileActivity.class);
        if (searchList != null) {
            String userToOpen = searchList.get(pos);
            System.out.println(userToOpen);
            profileIntent.putExtra("userToOpen", userToOpen);
            getActivity().startActivity(profileIntent);
        }
    }
}
