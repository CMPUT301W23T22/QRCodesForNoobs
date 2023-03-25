package com.example.qrcodesfornoobs.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.qrcodesfornoobs.Adapter.LeaderboardPlayerAdapter;
import com.example.qrcodesfornoobs.Models.Player;
import com.example.qrcodesfornoobs.R;
import com.example.qrcodesfornoobs.databinding.FragmentLeaderboardBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LeaderboardFragment} factory method to
 * create an instance of this fragment.
 */
public class LeaderboardFragment extends Fragment {

    FragmentLeaderboardBinding binding;
    LeaderboardPlayerAdapter adapter;


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

        setRecyclerView();
    }

    private void setRecyclerView() {
        binding.leaderboardRecyclerView.setHasFixedSize(true);
        binding.leaderboardRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        adapter = new LeaderboardPlayerAdapter(this.getContext(), getList());
        binding.leaderboardRecyclerView.setAdapter(adapter);
    }

    private List<Player> getList() {
        List<Player> players = new ArrayList<>();
        players.add(new Player("Player 1", "100"));
        players.add(new Player("Player 2", "200"));
        players.add(new Player("Player 3", "300"));
        players.add(new Player("Player 4", "400"));
        players.add(new Player("Player 5", "500"));
        players.add(new Player("Player 6", "600"));
        players.add(new Player("Player 7", "700"));
        players.add(new Player("Player 8", "800"));
        players.add(new Player("Player 9", "900"));
        players.add(new Player("Player 10", "1000"));
        players.add(new Player("Player 11", "1100"));
        players.add(new Player("Player 12", "1200"));
        players.add(new Player("Player 13", "1300"));
        players.add(new Player("Player 14", "1400"));
        players.add(new Player("Player 15", "1500"));
        return players;
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