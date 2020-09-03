package com.example.messaging__application;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FolderLabelsAdapter extends RecyclerView.Adapter<FolderLabelsAdapter.LabelsFolderHolder> {


    private Context mContext;
    private List<Folder_label> mData;

    public FolderLabelsAdapter(Context mContext, List<Folder_label> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public LabelsFolderHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.cardview_images_classes,parent,false);


        return new LabelsFolderHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull LabelsFolderHolder holder, final int position) {


        holder.Folder_label_name.setText(mData.get(position).getLabelName());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(mContext,LabelsActivity.class);
                intent.putExtra("label",mData.get(position).getLabelName());
                intent.putExtra("label1","x" );
                intent.putExtra("label2","x" );
                intent.putExtra("label3","x" );
                intent.putExtra("label4","x" );
                intent.putExtra("senderID",mData.get(position).getSenderID());
                intent.putExtra("recieverID",mData.get(position).getRecieverId());
                mContext.startActivity(intent);


            }
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class LabelsFolderHolder extends RecyclerView.ViewHolder{

        TextView Folder_label_name;
        ImageView Folder_mabel_image;
        CardView cardView;

        @SuppressLint("WrongViewCast")
        public LabelsFolderHolder(@NonNull View itemView) {
            super(itemView);

            Folder_label_name = (TextView) itemView.findViewById(R.id.folder_label_name);
            Folder_mabel_image = (ImageView) itemView.findViewById(R.id.folder_label_image);
            cardView =(CardView) itemView.findViewById(R.id.card_view_image_calsses);

        }
    }



}