package com.example.qrcodesfornoobs;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.MyHolder>{

    Context context;
    ArrayList<String> codes;
    LayoutInflater layoutInflater;
    private RecyclerViewInterface rvListener;
    private String searchName;

    public interface RecyclerViewInterface {
        void onItemClick(int pos);
    }

    public SearchAdapter(Context context, ArrayList<String> codes, RecyclerViewInterface rvListener) {
        this.context = context;
        this.codes = codes;
        this.rvListener = rvListener;
        layoutInflater = layoutInflater.from(context);
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.search_item, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        holder.userName.setText(codes.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = holder.getAdapterPosition();
                searchName = codes.get(pos);
                rvListener.onItemClick(pos);
            }
        });

    }

    @Override
    public int getItemCount() {
        return codes.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        TextView userName;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.txt);

//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if (rvInterface != null){
//                        int pos = getAdapterPosition();
//                        rvInterface.onItemClick(pos);
//                        searchName = codes.get(pos);
//                        Toast.makeText(view.getContext(), searchName,Toast.LENGTH_SHORT).show();
//
//                    }
//                }
//            });


        }
    }
}