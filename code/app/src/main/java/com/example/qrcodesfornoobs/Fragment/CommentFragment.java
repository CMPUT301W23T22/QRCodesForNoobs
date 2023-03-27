package com.example.qrcodesfornoobs.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.qrcodesfornoobs.Adapter.CommentAdapter;
import com.example.qrcodesfornoobs.Models.Creature;
import com.example.qrcodesfornoobs.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import org.w3c.dom.Document;
import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CommentFragment extends BottomSheetDialogFragment {
    private DocumentReference creatureRef;
    private DocumentReference playerRef;

    private String creatureHash;
    private String userName;
    private boolean canComment;
    CommentAdapter commentAdapter;
    RecyclerView recyclerView;
    EditText addCommentEditText;
    Button submitButton;
    private ArrayList<String> commentsList;
    private ArrayList<String> imageList;
    private FirebaseFirestore db;
    CollectionReference creatureCollectionReference;

    CollectionReference playerCollectionReference;

    public static CommentFragment newInstance(String creatureHash, String userName) {
        CommentFragment fragment = new CommentFragment();
        Bundle args = new Bundle();
        args.putString("creatureHash", creatureHash);
        args.putString("User", userName);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.comment_dialog,container,false);
        recyclerView = view.findViewById(R.id.comment_recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        commentAdapter = new CommentAdapter(getContext(),commentsList);
        recyclerView.setAdapter(commentAdapter);

        addCommentEditText = view.findViewById(R.id.comment_input_edittext);
        submitButton = view.findViewById(R.id.submit_comment_button);

        checkCommentPerms();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = addCommentEditText.getText().toString().trim();
                if(!text.isEmpty()){
                    addComment(text);
                    addCommentToFirebase(text);
                    addCommentEditText.setText("");
                }
            }
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        creatureHash = getArguments().getString("creatureHash");
        userName = getArguments().getString("User");
        canComment = false;
        db = FirebaseFirestore.getInstance();
        creatureCollectionReference = db.collection("Creatures");
        playerCollectionReference = db.collection("Players");
        creatureRef = creatureCollectionReference.document(creatureHash);
        playerRef = playerCollectionReference.document(userName);

        commentsList = new ArrayList<>();
        imageList = new ArrayList<>();

        super.onCreate(savedInstanceState);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView creatureImage = view.findViewById(R.id.creature_img);
        ImageView locationImage = view.findViewById(R.id.location_img);
        TextView creatureName = view.findViewById(R.id.creature_name_txt);
        TextView creatureNumScan = view.findViewById(R.id.creature_num_scanned);
        TextView creaturePoints = view.findViewById(R.id.creature_points_txt);
        RequestOptions options = new RequestOptions().circleCrop();
        creatureRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                creatureRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()){
                                Creature creature = document.toObject(Creature.class);
                                ArrayList<String> commentArray = creature.getComments();

                                if(isAdded()){
                                    Glide.with(getContext()).load(creature.getPhotoCreatureUrl())
                                            .apply(options)
                                            .into(creatureImage);
                                    if (creature.getPhotoCreatureUrl() != null){
                                        Glide.with(getContext()).load(creature.getPhotoLocationUrl())
                                                .apply(options)
                                                .into(locationImage);
                                    }
                                }

                                creatureName.setText(creature.getName());
                                creatureNumScan.setText("Scanned by " + creature.getNumOfScans() + " other players!");
                                creaturePoints.setText(creature.getScore() + " points");

                                commentsList.clear();
                                if (!commentArray.isEmpty()){
                                    commentsList.addAll(commentArray);

                                }

                                commentAdapter.notifyDataSetChanged();


                            } else {
                                Log.d("TAG", "No such document");
                            }
                        } else {
                            Log.d("TAG", "get failed with ", task.getException());
                        }
                    }
                });
            }

        });


    }

    private void checkCommentPerms(){
        playerRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()){
                    ArrayList<String> playerCreatures = (ArrayList<String>) documentSnapshot.get("creatures");
                    if (playerCreatures.contains(creatureHash)){
                        canComment = true;
                    }
                } else {
                    Log.d("TAG","Player document  does not exist!");
                }

                setEditTextVisibility();

            }
        });
    }

    private void setEditTextVisibility(){
        if (canComment){
            addCommentEditText.setVisibility(View.VISIBLE);
            submitButton.setVisibility(View.VISIBLE);
        } else {
            addCommentEditText.setVisibility(View.GONE);
            submitButton.setVisibility(View.GONE);
        }

    }
    private void addCommentToFirebase(String text){
        creatureRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                List<String> stringArray = (List<String>) documentSnapshot.get("comments");
                stringArray.add(userName + " : " + text);
                creatureRef.update("comments", stringArray)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("TAG","Comment successfully added to array");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("TAG","Comment failed to add to array", e);
                            }
                        });

            }
        });
    }
    private void addComment(String text){
        commentsList.add(userName + " : " + text);
        commentAdapter.notifyDataSetChanged();
    }
}
