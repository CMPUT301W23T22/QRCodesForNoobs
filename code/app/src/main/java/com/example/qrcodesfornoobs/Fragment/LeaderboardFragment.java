package com.example.qrcodesfornoobs.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.qrcodesfornoobs.R;
import com.example.qrcodesfornoobs.databinding.FragmentLeaderboardBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LeaderboardFragment} factory method to
 * create an instance of this fragment.
 */
public class LeaderboardFragment extends Fragment {

    FragmentLeaderboardBinding binding;


    /**
     * Required empty public constructor for fragment.
     */
    public LeaderboardFragment() {
        // Required empty public constructor
    }

    /**
     * Called to do initial creation of the fragment.
     *
     * @param savedInstanceState If the fragment is being re-created from a previous
     * saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentLeaderboardBinding.inflate(getLayoutInflater());
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment,
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous
     * saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return binding.getRoot();
    }
}