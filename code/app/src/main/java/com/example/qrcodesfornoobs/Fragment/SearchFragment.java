package com.example.qrcodesfornoobs.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.qrcodesfornoobs.Activity.MainActivity;
import com.example.qrcodesfornoobs.R;
import com.example.qrcodesfornoobs.Search;
import com.example.qrcodesfornoobs.SearchUser;
import com.example.qrcodesfornoobs.SearchUserAdapter;
import com.example.qrcodesfornoobs.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.checkerframework.common.subtyping.qual.Bottom;

import java.util.ArrayList;


public class SearchFragment extends Fragment {

    private Button backButton;
    private Intent dashboardIntent;

    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private int selectRadioId;

    private SearchView searchView;
    private RecyclerView recyclerView;
    private ArrayList<SearchUser> arrayList = new ArrayList<>();
    private ArrayList<SearchUser> searchList;
    String[] userList = new String[]{"Dog","Cat","Bird"};

    Activity mActivity;
    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        searchView = view.findViewById(R.id.searchView);
        searchView.setIconified(false);
        searchView.clearFocus();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        SearchUserAdapter searchUserAdapter = new SearchUserAdapter(getContext(), arrayList);
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
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                    recyclerView.setLayoutManager(layoutManager);

                    SearchUserAdapter searchUserAdapter = new SearchUserAdapter(getContext(), searchList);
                    recyclerView.setAdapter(searchUserAdapter);
                }
                else{
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                    recyclerView.setLayoutManager(layoutManager);

                    SearchUserAdapter searchUserAdapter = new SearchUserAdapter(getContext(), arrayList);
                    recyclerView.setAdapter(searchUserAdapter);

                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchList = new ArrayList<>();

                if(newText.length() > 0){
                    for(int i = 0; i < arrayList.size(); i++){
                        if(arrayList.get(i).getUsername().toUpperCase().contains(newText.toUpperCase())){
                            SearchUser searchUser = new SearchUser();
                            searchUser.setUsername(arrayList.get(i).getUsername());
                            searchList.add(searchUser);
                        }
                    }
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                    recyclerView.setLayoutManager(layoutManager);

                    SearchUserAdapter searchUserAdapter = new SearchUserAdapter(getContext(), searchList);
                    recyclerView.setAdapter(searchUserAdapter);
                }
                else{
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
                    recyclerView.setLayoutManager(layoutManager);

                    SearchUserAdapter searchUserAdapter = new SearchUserAdapter(getContext(), arrayList);
                    recyclerView.setAdapter(searchUserAdapter);

                }
                return false;
            }
        });

        backButton = (Button) view.findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, new DashboardFragment());
                fragmentTransaction.commit();
            }
        });

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // adding samples
        if (arrayList.isEmpty()) {
            for (int i = 0; i < userList.length; i++) {
                SearchUser searchUser = new SearchUser();
                searchUser.setUsername(userList[i]);
                arrayList.add(searchUser);
            }
        }
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