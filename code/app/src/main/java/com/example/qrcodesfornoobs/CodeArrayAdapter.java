package com.example.qrcodesfornoobs;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;

public class CodeArrayAdapter extends ArrayAdapter<Creature> {
    ImageButton deleteItemButton;
    private FragmentManager deleteCodeFragmentManager;
    public CodeArrayAdapter(@NonNull Context context, ArrayList<Creature> codes, FragmentManager fragmentManager) {
        super(context, 0, codes);
        deleteCodeFragmentManager = fragmentManager;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.content,
                    parent, false);
        } else {
            view = convertView;
        }

        Creature creature = getItem(position);

        TextView creatureName = view.findViewById(R.id.listview_code_name);
        TextView creatureScore = view.findViewById(R.id.listview_code_points);
        //ImageView creatureImage = view.findViewById(R.id.listview_code_image);

        creatureName.setText(creature.getName());
        creatureScore.setText(String.valueOf(creature.getScore()) + " points");
        //creatureImage.setImageResource(creature.getPhoto());

        deleteItemButton = view.findViewById(R.id.listview_delete_button);
        deleteItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeleteCodeFragment dialog = DeleteCodeFragment.newInstance(position);
                dialog.show(deleteCodeFragmentManager, "Delete Code");
            }
        });
        return view;
    }
}
