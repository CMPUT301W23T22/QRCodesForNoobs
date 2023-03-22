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

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class CommentFragment extends BottomSheetDialogFragment {
    private DocumentReference creatureRef;

    CommentAdapter commentAdapter;
    RecyclerView recyclerView;
    EditText addCommentEditText;
    Button submitButton;
    private ArrayList<String> commentsList;
    private FirebaseFirestore db;
    CollectionReference creatureCollectionReference;


    public static CommentFragment newInstance(String creatureHash) {
        CommentFragment fragment = new CommentFragment();
        Bundle args = new Bundle();
        args.putString("creatureHash", creatureHash);
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
        recyclerView.setAdapter(new CommentAdapter(getContext(),commentsList));


        addCommentEditText = view.findViewById(R.id.comment_input_edittext);
        submitButton = view.findViewById(R.id.submit_comment_button);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = addCommentEditText.getText().toString().trim();
                if(!text.isEmpty()){
                    addComment(text);
                    addCommentEditText.setText("");
                }
            }
        });


        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        String creatureHash = getArguments().getString("creatureHash");


        db = FirebaseFirestore.getInstance();
        creatureCollectionReference = db.collection("Creatures");
        creatureRef = creatureCollectionReference.document(creatureHash);
        commentsList = new ArrayList<>();

        super.onCreate(savedInstanceState);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ImageView creatureImage = view.findViewById(R.id.creature_img);
        TextView creatureName = view.findViewById(R.id.creature_name_txt);
        TextView creatureNumScan = view.findViewById(R.id.creature_num_scanned);


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

                                Glide.with(getContext()).load(creature.getPhotoCreatureUrl()).into(creatureImage);
                                creatureName.setText(creature.getName());
                                creatureNumScan.setText("Scanned by " + creature.getNumOfScans() + " other players!");

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

    private void addComment(String text){
        commentsList.add(text);
        commentAdapter.notifyDataSetChanged();
    }
}
