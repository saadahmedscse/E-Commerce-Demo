package com.caffeine.e_commercedemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.caffeine.e_commercedemo.R;
import com.caffeine.e_commercedemo.util.ColorInterface;

import java.util.ArrayList;

public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ViewHolder>{

    Context context;
    ArrayList<String> list;
    TextView color;
    private int selected_position = -1, count = 0;
    private ColorInterface colorInterface;

    public ColorAdapter(Context context, ArrayList<String> list, TextView color, ColorInterface colorInterface) {
        this.context = context;
        this.list = list;
        this.color = color;
        this.colorInterface = colorInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.color_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.size.setText(list.get(position));

        if (selected_position == position){
            holder.size.setBackgroundResource(R.drawable.stroke_grey_filled_orange);
        }

        else {
            holder.size.setBackgroundResource(R.drawable.bg_grey_square);
        }

        holder.size.setOnClickListener(v -> {
            colorInterface.onColorClicked(list.get(position), position);
        });
    }

    public void updateList(ArrayList<String> newList){
        this.list = newList;
        notifyDataSetChanged();
    }

    public void upadteBg(int position){
        this.selected_position = position;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView size;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            size = itemView.findViewById(R.id.color);
        }
    }
}
