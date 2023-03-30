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
import android.widget.Toast;

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

        radioGroupCheck(db, radioGroup);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                if (radioGroup.getCheckedRadioButtonId() == -1){
                    searchView.setQuery("", false);
                    Toast.makeText(getContext(), "Select Username or Location above.", Toast.LENGTH_SHORT).show();
                    return false;
                }
                else {
                    searchList = new ArrayList<>();
                    if (query.length() > 0) {
                        submitQuery(query);
                    }
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
        collectionReference = null;
        field = "";
    }

    /**
     * On submit of a nonempty query text, searches database for the "field" depending on which
     * radio button was selected (username / location).
     * <p>
     * Notify the RecyclerView's adapter to display the list of query documents.
     *
     * @param query User inputted string to be used to find documents in a Firebase collection.
     */
    public void submitQuery(String query) {
        searchView.clearFocus();
        collectionReference.orderBy(field).startAt(query.toUpperCase()).endAt(query.toUpperCase() + "\uf8ff").startAt(query).endAt(query + "\uf8ff").limit(10).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                Log.d("Query", doc.getId() + " => " + doc.getData());
                                searchList.add(doc.getData().get(field).toString());
                            }
                        } else {
                            Log.d("Query", "Error getting documents: ", task.getException());
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
                // Clear list on switching search by
                searchList = new ArrayList<>();
                SearchAdapter searchAdapter = new SearchAdapter(getContext(), searchList, rvInterface);
                recyclerView.setAdapter(searchAdapter);
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
        //TODO: on click of Location item, opens a "Location Profile"
        profileIntent = new Intent(getActivity(), ProfileActivity.class);
        if (searchList != null) {
            String userToOpen = searchList.get(pos);
            System.out.println(userToOpen);
            profileIntent.putExtra("userToOpen", userToOpen);
            getActivity().startActivity(profileIntent);
        }
    }
}
