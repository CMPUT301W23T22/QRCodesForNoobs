package com.example.qrcodesfornoobs.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.qrcodesfornoobs.R;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.ArrayList;

public class CodeSliderAdapter extends SliderViewAdapter<CodeSliderAdapter.SliderAdapterViewHolder> {

    // URLs to fetch code images from
    private ArrayList<String> sliderItems;

    public CodeSliderAdapter(Context context, ArrayList<String> sliderDataArrayList) {
        this.sliderItems = sliderDataArrayList;
    }

    @Override
    public SliderAdapterViewHolder onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.slider_item, parent, false);
        return new SliderAdapterViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterViewHolder viewHolder, int position) {
        String sliderItem = sliderItems.get(position); // URL of display item

        Glide.with(viewHolder.itemView)
                .load(sliderItem)
                .fitCenter()
                .into(viewHolder.imageViewBackground);
    }

    @Override
    public int getCount() {
        return sliderItems.size();
    }


    static class SliderAdapterViewHolder extends SliderViewAdapter.ViewHolder {
        // Adapter class for initializing
        // the views of our slider view.
        View itemView;
        ImageView imageViewBackground;

        public SliderAdapterViewHolder(View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.slider_item_imageView);
            this.itemView = itemView;
        }
    }
}
