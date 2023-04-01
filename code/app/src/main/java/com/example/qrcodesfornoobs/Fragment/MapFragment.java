package com.example.qrcodesfornoobs.Fragment;

import static java.lang.Thread.sleep;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.qrcodesfornoobs.Models.Creature;
import com.example.qrcodesfornoobs.R;
import com.example.qrcodesfornoobs.databinding.FragmentMapBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 */
public class MapFragment extends Fragment {

    int range = 5;
    boolean userFound;
    GoogleMap mMap;
    FragmentMapBinding binding;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    final CollectionReference creatureReference = db.collection("Creatures");

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MapFragment newInstance(String param1, String param2) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Required empty public constructor for fragment.
     */
    public MapFragment() {
        // Required empty public constructor
    }

    /**
     * Called when the fragment is created. Gets the arguments passed in and inflates the layout.
     *
     * @param savedInstanceState The saved instance state bundle.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userFound = false;
        binding = FragmentMapBinding.inflate(getLayoutInflater());
        showMap();
    }

    public void showMap() {
        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.google_map);
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {

                // Initialize map
                MapsInitializer.initialize(getActivity());
                mMap = googleMap;
                // Implementation from
                // https://stackoverflow.com/questions/31021000/android-google-maps-v2-remove-default-markers/49090477#49090477
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.map_style));
                requestPermissions();
                followPlayer();
            }
        });
    }

    /**
     * Sets the map to follow player, and also call function to display markers around player
     * Does nothing if permissions have not been granted.
     */
    public void followPlayer() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        displayNearbyMarkers();
    }

    /**
     * Requests for location permissions in order to display user location and display nearby creatures.
     */
    public void requestPermissions() {
        if ( ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 667);
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 668);
        }
    }
    /**
     * Method call to make a listener that displays Creatures around the player within a certain range.
     */
    public void displayNearbyMarkers() {

        mMap.setOnMyLocationChangeListener( location -> {

            // One time call to set camera to player's location
            if (!userFound) {
                centerCamera(location);
                userFound = true;
            }

            // A circular radius is too much for me. I'm just making a square
            creatureReference
                .whereLessThanOrEqualTo("longitude", location.getLongitude() + range)
                .whereGreaterThanOrEqualTo("longitude", location.getLongitude() - range)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        for (DocumentSnapshot doc: task.getResult()){
                            // All cases have a longitude, it is assumed they also have a latitude
                            String field = "latitude";
                            double creatureLatitude = doc.getDouble(field);

                            // Based on this Stack discussion, querying outside the db is a valid option:
                            // https://stackoverflow.com/questions/26700924/query-based-on-multiple-where-clauses-in-firebase
                            if (creatureLatitude >= location.getLatitude() - range &&
                                creatureLatitude <= location.getLatitude() + range) {
                                Creature creature = doc.toObject(Creature.class);
                                LatLng marker = new LatLng(creature.getLatitude(), creature.getLongitude());
                                mMap.addMarker(new MarkerOptions().position(marker).title(creature.getScore() + ""));
                            }
                        }
                    }
            });
        });

    }

    /**
     * Called when the fragment's view is created. Returns the root view for the fragment.
     * @param inflater The layout inflater for inflating the layout.
     * @param container The container view for the fragment.
     * @param savedInstanceState The saved instance state bundle.
     * @return The root view for the fragment.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return binding.getRoot();
//        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    /**
     * Method to center the view of the map to a target location.
     */
    public void centerCamera(Location location) {

        LatLng target = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(target, 15f));
    }
    public void notifyLocationNotGiven() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder
                .setMessage("Location Permissions were not found. Please enable them to find nearby creatures.")
                .setPositiveButton("Ok", null)
                .create()
                .show();
    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // If results are empty end early
        if (grantResults.length <= 0) {
            return;
        }
        //If the code refers to a location permission
        if (requestCode == 667 || requestCode == 668) {
            //If permission granted, follow the player
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                followPlayer(); // TODO: Get this to work I don't know why it doesn't
            // Otherwise, notify the player that it failed
            } else {
                notifyLocationNotGiven();
            }
        }
    }
}