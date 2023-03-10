package com.example.qrcodesfornoobs;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


public class ProfileCodeArrayAdapter extends RecyclerView.Adapter<ProfileCodeArrayAdapter.MyHolder>{

    Context context;
    ArrayList<Creature> codes;
    LayoutInflater layoutInflater;
    ImageView creatureImage;

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
        URL creatureImageUrl;
        try {
            creatureImageUrl = new URL(creature.getPhotoCreatureUrl());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        RequestOptions options = new RequestOptions().circleCrop().placeholder(R.drawable.face_icon);


        holder.creatureName.setText(creature.getName());
        holder.creatureScore.setText(creature.getScore() + " points");
        Glide.with(context).load(creatureImageUrl).apply(options).into(creatureImage);

    }

    @Override
    public int getItemCount() {
        return codes.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        TextView creatureName;
        TextView creatureScore;

        public MyHolder(@NonNull View itemView) {
            // Obtain list item textviews
            super(itemView);
            creatureName = itemView.findViewById(R.id.profile_code_txt);
            creatureScore = itemView.findViewById(R.id.profile_code_points);
            creatureImage = itemView.findViewById(R.id.profile_creature_img);


        }
    }
}