package com.example.qrcodesfornoobs.Fragment;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.qrcodesfornoobs.Profile;
import com.example.qrcodesfornoobs.ProfileCodeArrayAdapter;
import com.example.qrcodesfornoobs.R;
import com.example.qrcodesfornoobs.SearchUser;
import com.example.qrcodesfornoobs.SearchUserAdapter;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class SearchFragment extends Fragment {


    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private int selectRadioId;

    private SearchView searchView;
    private RecyclerView recyclerView;

    // For Firebase
    private ArrayList<String> dataList;
    private ProfileCodeArrayAdapter codeArrayAdapter;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    final CollectionReference collectionReference = db.collection("QRCodePath");

    private ArrayList<String> searchList;
    String[] userList = new String[]{"Dog","Cat","Bird"};

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_search, container, false); // Inflate the layout for this fragment
        dataList = new ArrayList<>();

        // temp: adding samples
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable
                    FirebaseFirestoreException error) {

                // Clear the old list
                dataList.clear();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    String QR = doc.getId();
                    dataList.add(QR); // Adding from FireStore
                }
                codeArrayAdapter.notifyDataSetChanged(); // Notifying the adapter to render any new data fetched from the cloud
                //codeArrayAdapter.notifyItemRemoved(position);
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

    public void onClickRadio(View view){
        selectRadioId = radioGroup.getCheckedRadioButtonId();
        radioButton = getView().findViewById(selectRadioId);
        if(selectRadioId == 1){
            Toast.makeText(getActivity(), "Selected", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getActivity(), radioButton.getText(), Toast.LENGTH_SHORT).show();
        }
    }



}