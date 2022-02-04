package com.caffeine.e_commercedemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.caffeine.e_commercedemo.R;
import com.caffeine.e_commercedemo.model.ImageDetails;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DetailedImageAdapter extends RecyclerView.Adapter<DetailedImageAdapter.ViewHolder>{

    Context context;
    ImageView imageView;
    ArrayList<ImageDetails> list;

    public DetailedImageAdapter(Context context, ImageView imageView, ArrayList<ImageDetails> list) {
        this.context = context;
        this.imageView = imageView;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.detailed_image_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ImageDetails images = list.get(position);
        Picasso.get().load(images.getImage()).into(holder.image);

        holder.image.setOnClickListener(v -> {
            Picasso.get().load(images.getImage()).into(imageView);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
        }
    }
}
