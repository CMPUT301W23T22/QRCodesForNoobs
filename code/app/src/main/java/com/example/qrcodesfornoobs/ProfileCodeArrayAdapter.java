package com.example.qrcodesfornoobs;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class ProfileCodeArrayAdapter extends RecyclerView.Adapter<ProfileCodeArrayAdapter.MyHolder>{

    Context context;
    ArrayList<Creature> codes;
    LayoutInflater layoutInflater;

    public ProfileCodeArrayAdapter(Context context, ArrayList<Creature> codes) {
        this.context = context;
        this.codes = codes;
        layoutInflater = layoutInflater.from(context);
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.profile_code_content, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        // Set list item info
        Creature creature = codes.get(position);
        holder.creatureName.setText(creature.getName());
        holder.creatureScore.setText(creature.getScore() + " points");
        holder.creatureNumOfScans.setText("Scanned by " + creature.getNumOfScans() + " Players");
    }

    @Override
    public int getItemCount() {
        return codes.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        TextView creatureName;
        TextView creatureScore;
        TextView creatureNumOfScans;
        public MyHolder(@NonNull View itemView) {
            // Obtain list item textviews
            super(itemView);
            creatureName = itemView.findViewById(R.id.profile_code_txt);
            creatureScore = itemView.findViewById(R.id.profile_code_points);
            creatureNumOfScans = itemView.findViewById(R.id.profile_num_of_scans);
        }
    }
}