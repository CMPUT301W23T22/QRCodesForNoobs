package com.example.qrcodesfornoobs.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.qrcodesfornoobs.Adapter.CodeSliderAdapter;
import com.example.qrcodesfornoobs.Models.Player;
import com.example.qrcodesfornoobs.Activity.ProfileActivity;
import com.example.qrcodesfornoobs.Activity.SettingsActivity;
import com.example.qrcodesfornoobs.databinding.FragmentDashboardBinding;



import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DashboardFragment} method to
 * create an instance of this fragment.
 */
public class DashboardFragment extends Fragment {

    FragmentDashboardBinding binding;
    private Intent profileIntent;
    private Intent settingsIntent;


    /**
     * Required empty public constructor
     */
    public DashboardFragment() {
        // Required empty public constructor
    }

    /**
     * Called to do initial creation of a fragment.
     *
     * @param savedInstanceState If the fragment is being re-created from a previous saved state,
     * this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentDashboardBinding.inflate(getLayoutInflater());
        binding.usernameTextView.setText(Player.LOCAL_USERNAME);

        profileIntent = new Intent(getActivity(), ProfileActivity.class);
        settingsIntent = new Intent(getActivity(), SettingsActivity.class);
        setUpSliders();
        addListenerOnButtons();

    }

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as
     * given here.
     * @return Return the View for the fragment's UI.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return binding.getRoot();
//        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    /**
     * This method is used to set the click listeners on the buttons in the dashboard fragment.
     */
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


    /**
     * This method is used to set up the slider on the dashboard fragment.
     * It sets up a slider adapter with code URLs and sets the adapter on the slider view.
     */
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
