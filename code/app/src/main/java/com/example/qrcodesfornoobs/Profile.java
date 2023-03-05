package com.example.qrcodesfornoobs;


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
import android.widget.ListView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qrcodesfornoobs.Dashboard;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class Profile extends AppCompatActivity {
    Button backButton;
    ImageButton toggleFilterButton;
    ImageButton toggleRecyclerViewButton;
    ImageButton toggleSortButton;
    Spinner sortListSpinner;
    ListView listView;
    RecyclerView recyclerView;
    com.example.qrcodesfornoobs.ProfileCodeArrayAdapter codeArrayAdapter;

    LinearLayout filterBar;
    private Intent dashboardIntent;

    private ArrayList<Creature> dataList;
    private ArrayAdapter<String> dataAdapter;

    // FIREBASE INITIALIZE
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    final CollectionReference collectionReference = db.collection("QRCodePath");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        // When we add a new creature we need to update the datalist first
        // From the datalist we will add them into the database
        dataList = new ArrayList<>();

        try {
            dataList.add(new Creature("DSA66GW54"));
            dataList.add(new Creature("55555GW54"));

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        System.out.println(dataList.size());
        // Temporary
        //TODO: Implement actual adding function when that is finished
        for (int i = 0; i < dataList.size(); i++){
            HashMap<String, String> dataToAdd = new HashMap<>();

            dataToAdd.put("Name", dataList.get(i).getName());
            dataToAdd.put("Score", Integer.toString(dataList.get(i).getScore()));
            dataToAdd.put("Hash", dataList.get(i).getHash());

            collectionReference
                    .document(dataToAdd.get("Hash"))
                    .set(dataToAdd);
        }



        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable
            FirebaseFirestoreException error) {

                // Clear the old list
                dataList.clear();
                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                    // Get attributes from the document using doc.getData(attribute name)
                    // Create a creature object and use setters to set its attributes to the ones from document
                    // Add the creature to database
                    try {
                        dataList.add(new Creature("DSA66GW54"));
                        dataList.add(new Creature("55555GW54"));
                    } catch (NoSuchAlgorithmException e) {
                        throw new RuntimeException(e);
                    }
                }
                codeArrayAdapter.notifyDataSetChanged(); // Notifying the adapter to render any new data fetched from the cloud

            }
        });

        // INITIALIZATION
        initWidgets(); // Initialize buttons and spinners
        addListenerOnButtons(); // Initialize button listeners

        // RECYCLER VIEW  CHANGES 230301
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(Profile.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        codeArrayAdapter = new com.example.qrcodesfornoobs.ProfileCodeArrayAdapter(Profile.this, dataList);
        recyclerView.setAdapter(codeArrayAdapter);
        setSwipeToDelete();

        // INTENT
        dashboardIntent = new Intent(this, Dashboard.class);
    }

    // For RecyclerView Delete
    private void setSwipeToDelete() {
        final String TAG = "Sample";

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT){
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Creature QR = dataList.get(position);
                System.out.println(dataList.size());

                db.collection("QRCodePath")
                        .document(QR.getHash())
                        .delete()
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

                collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable
                            FirebaseFirestoreException error) {

                        // Clear the old list
                        dataList.clear();
                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                            try {
                                dataList.add(new Creature("DSA66GW54")); // Adding from FireStore
                            } catch (NoSuchAlgorithmException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        codeArrayAdapter.notifyDataSetChanged(); // Notifying the adapter to render any new data fetched from the cloud
                        //codeArrayAdapter.notifyItemRemoved(position);
                    }
                });

                // UNDO DELETE: not the same hashmap tho
                Snackbar.make(recyclerView, QR.getHash(), Snackbar.LENGTH_LONG).setAction("Undo", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        HashMap<String, String> data = new HashMap<>();
                        if (QR.getHash().length() > 0) {
                            data.put("QR Code", QR.getHash());

                            collectionReference
                                    .document(QR.getHash())
                                    .set(data)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // These are a method which gets executed when the task is succeeded
                                            Log.d(TAG, "Data has been added successfully!");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // These are a method which gets executed if there’s any problem
                                            Log.d(TAG, "Data could not be added!" + e.toString());
                                        }
                                    });
                        }

//                        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
//                            @Override
//                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable
//                                    FirebaseFirestoreException error) {
//
//                                // Clear the old list
//                                dataList.clear();
//                                for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
//                                    try {
//                                        dataList.add(new Creature("VF765GW54"));
//                                        dataList.add(new Creature("F9321SA54"));
//                                        dataList.add(new Creature("G9FS215FG")); // Adding from FireStore
//                                    } catch (NoSuchAlgorithmException e) {
//                                        throw new RuntimeException(e);
//                                    }
//                                }
//                                codeArrayAdapter.notifyDataSetChanged(); // Notifying the adapter to render any new data fetched from the cloud
//                                //codeArrayAdapter.notifyItemInserted(position);
//                                recyclerView.scrollToPosition(position);
//                            }
//                        });
                        //dataList.add(position, QR);
                    }
                }).show();
            }

            @Override
            public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder){
                return 1f;
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                                    @NonNull RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY, int actionState, boolean isCurrentlyActive){
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

        if (isCancelled){
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

    private void initWidgets(){
        backButton = findViewById(R.id.back_button);
        backButton.setBackgroundResource(R.drawable.back_arrow);
        toggleFilterButton = findViewById(R.id.toggle_filterbar_button);
        toggleRecyclerViewButton = findViewById(R.id.toggle_recyclerView_button);
        filterBar = findViewById(R.id.filterbar);
        sortListSpinner = findViewById(R.id.sort_list_spinner);
        recyclerView = findViewById(R.id.recyclerView);

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
                startActivity(dashboardIntent);
            }
        });
        toggleFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Hide and show the filter bar
                if (filterBar.getVisibility() == View.VISIBLE){
                    filterBar.setVisibility(View.GONE);
                } else {
                    filterBar.setVisibility(View.VISIBLE);
                }            }
        });
        toggleRecyclerViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide and show the listview
                // (Toggle between sliding view and listview)
                if (recyclerView.getVisibility() == View.VISIBLE){
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
                if (selected.equals("SCORE (ASCENDING)")){
                    System.out.println("ASCENDING");
                    dataList.sort(new ProfileCreatureScoreComparator());
                } else if (selected.equals("SCORE (DESCENDING)")){
                    System.out.println("DESCENDING");
                    dataList.sort(new ProfileCreatureScoreComparator());
                    Collections.reverse(dataList);
                }
                codeArrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }

}
