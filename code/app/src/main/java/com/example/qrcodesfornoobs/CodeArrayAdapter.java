package com.example.qrcodesfornoobs;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;

public class CodeArrayAdapter extends ArrayAdapter<String> {
    ImageButton deleteItemButton;
    private FragmentManager mFragmentManager;
    public CodeArrayAdapter(@NonNull Context context, ArrayList<String> codes, FragmentManager fragmentManager) {
        super(context, 0, codes);
        mFragmentManager = fragmentManager;
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


        deleteItemButton = view.findViewById(R.id.listview_delete_button);
        deleteItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeleteCodeFragment dialog = DeleteCodeFragment.newInstance(position);
                dialog.show(mFragmentManager, "Delete Code");
            }
        });
        return view;
    }
}
