package com.example.qrcodesfornoobs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ProfileDeleteCodeFragment extends DialogFragment {
    Button confirmButton;
    Button cancelButton;
    AlertDialog dialog;
    private int position; // The position of the listview item
    public static ProfileDeleteCodeFragment newInstance(int pos) {
        ProfileDeleteCodeFragment fragment = new ProfileDeleteCodeFragment();
        Bundle args = new Bundle();
        args.putInt("position", pos);
        fragment.setArguments(args);
        return fragment;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.delete_code_fragment,null);

        Bundle args = getArguments();
        if (args != null){
            position = args.getInt("position");
        }

        // Builder for the delete item dialog popup
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(view);
        dialog = builder.create();

        // Initialize buttons and their click listeners
        confirmButton = view.findViewById(R.id.confirm_delete_button);
        cancelButton = view.findViewById(R.id.cancel_delete_button);
        addListenerOnButtons();

        // Show the dialog
        dialog.show();
        return dialog;
    }

    private void addListenerOnButtons(){
        // Buttons do nothing right now
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Delete item at position" + position, Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }

        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Delete cancelled", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }
}
