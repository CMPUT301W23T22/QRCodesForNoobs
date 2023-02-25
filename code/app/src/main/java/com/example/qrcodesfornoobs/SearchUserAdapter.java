package com.example.qrcodesfornoobs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class SearchUserAdapter extends RecyclerView.Adapter<SearchUserAdapter.MyHolder>{

    Context context;
    ArrayList<SearchUser> arrayList;
    LayoutInflater layoutInflater;

    public SearchUserAdapter(Context context, ArrayList<SearchUser> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        layoutInflater = layoutInflater.from(context);
    }

    @NonNull
    @Override
    public SearchUserAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.search_item, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchUserAdapter.MyHolder holder, int position) {
        holder.userName.setText(arrayList.get(position).getUsername());


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        TextView userName;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.txt);

        }
    }
}