package com.example.qrcodesfornoobs.Activity;


import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qrcodesfornoobs.Adapter.ProfileCodeArrayAdapter;
import com.example.qrcodesfornoobs.Models.Creature;
import com.example.qrcodesfornoobs.Models.Player;
import com.example.qrcodesfornoobs.Tools.ProfileCreatureScoreComparator;
import com.example.qrcodesfornoobs.Fragment.ProfileEditInfoFragment;
import com.example.qrcodesfornoobs.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {
    Button backButton;
    ImageButton editProfileButton;
    ImageButton toggleFilterButton;
    ImageButton toggleRecyclerViewButton;
    Spinner sortListSpinner;
    RecyclerView recyclerView;
    ProfileCodeArrayAdapter codeArrayAdapter;
    TextView playerName;
    TextView codeCount;
    LinearLayout filterBar;
    Intent mainIntent;
    private Intent profileIntent;
    private ArrayList<Creature> creaturesToDisplay;
    private ArrayList<String> playerCreatureList;
    private DocumentReference playerRef;
    private String userToOpen;

    // FIREBASE INITIALIZE
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    final CollectionReference creatureCollectionReference = db.collection("Creatures");
    final CollectionReference playerCollectionReference = db.collection("Players");
    final String TAG = "tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);

        // When we add a new creature we need to update the datalist first
        // From the datalist we will add them into the database
        creaturesToDisplay = new ArrayList<>();
        playerCreatureList = new ArrayList<>();
        mainIntent = new Intent(this, MainActivity.class);

        setProfileUser();

        // Initialize buttons and spinners
        initWidgets();

        // Get reference to player's collection
        playerRef = playerCollectionReference.document(userToOpen);

        playerRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            // Listens for changes to the player's collection on the database
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                playerRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    // Gets data from player collection
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Gets players codes from their 'creatures' database array list
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                Player dbPlayer = document.toObject(Player.class);
                                // Fill local array with creatures from database
                                playerCreatureList = dbPlayer.getCreatures();
                                if (!playerCreatureList.isEmpty()){
                                    // Queries the Creature collection on db for creatures that the player owns
                                    creatureCollectionReference.whereIn("hash",playerCreatureList)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    Log.d(TAG, "task success: " + task.isSuccessful());
                                                    if (task.isSuccessful()){
                                                        // query success
                                                        creaturesToDisplay.clear();
                                                        for (QueryDocumentSnapshot doc : task.getResult()){
                                                            // Add creatures that the player owns to the local creatureToDisplay
                                                            Log.d(TAG, "Doc data: " + doc.getId());
                                                            Creature creature;
                                                            creature = doc.toObject(Creature.class);
                                                            creaturesToDisplay.add(creature);
                                                        }

                                                        codeCount.setText(creaturesToDisplay.size() + " Codes Scanned");
                                                        String sortValue = sortListSpinner.getSelectedItem().toString();
                                                        sort(sortValue);
                                                        codeArrayAdapter.notifyDataSetChanged();
                                                    } else {
                                                        Log.d(TAG, "get failed with ", task.getException());
                                                    }
                                                }
                                            });
                                }
                                else{
                                    //empty list
                                    creaturesToDisplay.clear();
                                    codeArrayAdapter.notifyDataSetChanged();
                                    codeCount.setText("0 Codes Scanned");
                                }
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });

            }
        });

        addListenerOnButtons(); // Initialize button listeners

        // RECYCLER VIEW  CHANGES 230301
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ProfileActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        codeArrayAdapter = new ProfileCodeArrayAdapter(ProfileActivity.this, creaturesToDisplay);
        recyclerView.setAdapter(codeArrayAdapter);

        if (userToOpen == Player.LOCAL_USERNAME){
            setSwipeToDelete();
        }

    }

    // For RecyclerView Delete
    private void setSwipeToDelete() {
        final String TAG = "Sample";

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                // Get the swiped Creature and delete it from the database
                Creature QR = creaturesToDisplay.get(position);
                db.collection("Players")
                        .document(userToOpen)
                        .update("creatures", FieldValue.arrayRemove(QR.getHash()))
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully deleted!");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error deleting document", e);
                            }
                        });

                // UNDO DELETE: not the same hashmap tho
                Snackbar.make(recyclerView, "Deleted " + QR.getName(), Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                    // Undo the delete, re-add the deleted Creature to database
                    @Override
                    public void onClick(View view) {

                        if (QR.getHash().length() > 0) {

                            playerCollectionReference
                                    .document(userToOpen)
                                    .update("creatures",FieldValue.arrayUnion(QR.getHash()))
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // These are a method which gets executed when the task is succeeded
                                            Log.d(TAG, "Data has been re-added successfully!");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // These are a method which gets executed if there’s any problem
                                            Log.d(TAG, "Data could not be re-added!" + e.toString());
                                        }
                                    });
                        }
                        recyclerView.scrollToPosition(position);
                    }
                }).show();
            }

            @Override
            public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
                return 1f;
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY, int actionState, boolean isCurrentlyActive) {
                setDeleteIcon(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }).attachToRecyclerView(recyclerView);
    }

    private void setDeleteIcon(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        Paint mClearPaint = new Paint();
        mClearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        ColorDrawable mBackground = new ColorDrawable();
        int backgroundColor = Color.parseColor("#B80F0A");
        Drawable deleteDrawable = ContextCompat.getDrawable(this, R.drawable.delete_icon);
        int intrinsicWidth = deleteDrawable.getIntrinsicWidth();
        int intrinsicHeight = deleteDrawable.getIntrinsicHeight();

        View itemView = viewHolder.itemView;
        int itemHeight = itemView.getHeight();

        boolean isCancelled = dX == 0 && !isCurrentlyActive;

        if (isCancelled) {
            c.drawRect(itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(),
                    (float) itemView.getBottom(), mClearPaint);
            return;
        }

        mBackground.setColor(backgroundColor);
        mBackground.setBounds(itemView.getRight() + (int) dX, itemView.getTop(),
                itemView.getRight(), itemView.getBottom());
        mBackground.draw(c);

        int deleteIconTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
        int deleteIconMargin = (itemHeight - intrinsicHeight) / 2;
        int deleteIconLeft = itemView.getRight() - deleteIconMargin - intrinsicWidth;
        int deleteIconRight = itemView.getRight() - deleteIconMargin;
        int deleteIconBottom = deleteIconTop + intrinsicHeight;

        deleteDrawable.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom);
        deleteDrawable.draw(c);
    }

    private void initWidgets() {
        backButton = findViewById(R.id.back_button);
        backButton.setBackgroundResource(R.drawable.back_arrow);
        editProfileButton = findViewById(R.id.edit_profile_button);
        if (!Objects.equals(userToOpen, Player.LOCAL_USERNAME)){
            editProfileButton.setVisibility(View.GONE);
        }

        toggleFilterButton = findViewById(R.id.toggle_filterbar_button);
        toggleRecyclerViewButton = findViewById(R.id.toggle_recyclerView_button);
        filterBar = findViewById(R.id.filterbar);
        sortListSpinner = findViewById(R.id.sort_list_spinner);
        recyclerView = findViewById(R.id.recyclerView);
        playerName = findViewById(R.id.profile_playername_textview);
        playerName.setText(userToOpen);
        codeCount = findViewById(R.id.profile_playercodecount_textview);
        // Initialize spinner data
        ArrayAdapter<CharSequence> spinAdapter = ArrayAdapter.createFromResource(this,
                R.array.filter_options, R.layout.spinner_item);
        spinAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        sortListSpinner.setAdapter(spinAdapter);
    }

    private void addListenerOnButtons() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(mainIntent);
            }
        });

        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Replace 'contact' with Player.getInfo() or something when we have that set up
                DialogFragment editInfoFrag = ProfileEditInfoFragment.newInstance("contact");
                editInfoFrag.show(getSupportFragmentManager(),"Edit Contact Info");
            }
        });
        toggleFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Hide and show the filter bar
                if (filterBar.getVisibility() == View.VISIBLE) {
                    filterBar.setVisibility(View.GONE);
                } else {
                    filterBar.setVisibility(View.VISIBLE);
                }
            }
        });

        toggleRecyclerViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide and show the listview
                // (Toggle between sliding view and listview)
                if (recyclerView.getVisibility() == View.VISIBLE) {
                    toggleRecyclerViewButton.setImageResource(R.drawable.face_icon);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    toggleRecyclerViewButton.setImageResource(R.drawable.menu_icon);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }
        });
        sortListSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selected = sortListSpinner.getItemAtPosition(i).toString();
                sort(selected);
                codeArrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void sort(String sortValue){
        if (sortValue.equals("SCORE (ASCENDING)")) {
            creaturesToDisplay.sort(new ProfileCreatureScoreComparator());
        } else if (sortValue.equals("SCORE (DESCENDING)")) {
            creaturesToDisplay.sort(new ProfileCreatureScoreComparator());
            Collections.reverse(creaturesToDisplay);
        }
    }

    private void setProfileUser(){
        profileIntent = getIntent();
        userToOpen = profileIntent.getStringExtra("userToOpen");
        if (userToOpen == null){
            userToOpen = Player.LOCAL_USERNAME;
        }
        System.out.println("Opening profile of user " + userToOpen);
    }
}