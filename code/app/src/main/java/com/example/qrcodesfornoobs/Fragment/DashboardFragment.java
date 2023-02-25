package com.example.qrcodesfornoobs.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.qrcodesfornoobs.Adapter.CodeSliderAdapter;
import com.example.qrcodesfornoobs.Profile;
import com.example.qrcodesfornoobs.Settings;
import com.example.qrcodesfornoobs.databinding.FragmentDashboardBinding;



import java.util.ArrayList;

public class DashboardFragment extends Fragment {

    FragmentDashboardBinding binding;
    private Intent profileIntent;
    private Intent settingsIntent;


    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("creating dashboard fragment");
        binding = FragmentDashboardBinding.inflate(getLayoutInflater());

        profileIntent = new Intent(getActivity(), Profile.class);
        settingsIntent = new Intent(getActivity(), Settings.class);
        setUpSliders();
        addListenerOnButtons();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return binding.getRoot();
//        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    private void addListenerOnButtons() {
        binding.profileImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(profileIntent);
            }
        });

        binding.settingImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(settingsIntent);
            }
        });
    }


    private void setUpSliders() {
        String test_url1 = "https://i.insider.com/57910997dd0895a56e8b456d?width=700&format=jpeg&auto=webp";
        String test_url2 = "https://bizzbucket.co/wp-content/uploads/2020/08/Life-in-The-Metro-Blog-Title-22.png";

        ArrayList<String> codeURLs = new ArrayList<>();
        codeURLs.add(test_url1);
        codeURLs.add(test_url2);

        CodeSliderAdapter adapter = new CodeSliderAdapter(this.getContext(), codeURLs);
        binding.dashboardSliderView.setSliderAdapter(adapter);
    }
}