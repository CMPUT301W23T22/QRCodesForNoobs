package com.example.qrcodesfornoobs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class CodeArrayAdapter extends RecyclerView.Adapter<CodeArrayAdapter.MyHolder>{

    Context context;
    ArrayList<String> codes;
    LayoutInflater layoutInflater;

    public CodeArrayAdapter(Context context, ArrayList<String> codes) {
        this.context = context;
        this.codes = codes;
        layoutInflater = layoutInflater.from(context);
    }

    @NonNull
    @Override
    public CodeArrayAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.profile_code_content, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CodeArrayAdapter.MyHolder holder, int position) {
        holder.userName.setText(codes.get(position));

    }

    @Override
    public int getItemCount() {
        return codes.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        TextView userName;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.profile_code_txt);

        }
    }
}