package com.example.qrcodesfornoobs.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.qrcodesfornoobs.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class CommentFragment extends BottomSheetDialogFragment {
    private ImageView creatureImage;
    private DocumentReference creatureRef;

    private FirebaseFirestore db;
    final CollectionReference creatureCollectionReference = db.collection("Creatures");


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
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        String creatureHash = getArguments().getString("creatureHash");
        System.out.println(creatureHash);

        db = FirebaseFirestore.getInstance();
        creatureRef = creatureCollectionReference.document(creatureHash);

        super.onCreate(savedInstanceState);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        creatureImage = view.findViewById(R.id.creature_img);



    }
}
