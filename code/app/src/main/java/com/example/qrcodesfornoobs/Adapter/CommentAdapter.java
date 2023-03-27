package com.example.qrcodesfornoobs.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qrcodesfornoobs.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyHolder>{
    Context context;
    ArrayList<String> comments;
    LayoutInflater layoutInflater;


    public CommentAdapter(Context context, ArrayList<String> comments) {
        this.context = context;
        this.comments = comments;
        layoutInflater = layoutInflater.from(context);
    }
    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.qrcode_comment_content, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.MyHolder holder, int position) {
        holder.commentText.setText(comments.get(position));

    }

    @Override
    public int getItemCount() {
        return comments.size();
    }



    public class MyHolder extends RecyclerView.ViewHolder {
        TextView commentText;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            commentText = itemView.findViewById(R.id.comment_txt);
        }
    }
}
