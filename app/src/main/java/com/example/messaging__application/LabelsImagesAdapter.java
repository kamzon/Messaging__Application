package com.example.messaging__application;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class LabelsImagesAdapter extends RecyclerView.Adapter<LabelsImagesAdapter.LabelsImagesHolder> {


    private Context mContext;
    private List<LabelsImages> mData;

    public LabelsImagesAdapter(Context mContext, List<LabelsImages> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public LabelsImagesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.cardview_images_in_the_classe,parent,false);


        return new LabelsImagesHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LabelsImagesHolder holder, int position) {

        Picasso.get().load(mData.get(position).getImagePath()).into(holder.image);


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class LabelsImagesHolder extends RecyclerView.ViewHolder{

        ImageView image;
        //ImageView Folder_mabel_image;


        @SuppressLint("WrongViewCast")
        public LabelsImagesHolder(@NonNull View itemView) {
            super(itemView);

            image = (ImageView) itemView.findViewById(R.id.image_in_the_classe);
            //Folder_mabel_image = (ImageView) itemView.findViewById(R.id.folder_label_image);


        }
    }

}
