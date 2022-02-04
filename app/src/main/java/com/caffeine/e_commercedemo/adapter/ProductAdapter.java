package com.caffeine.e_commercedemo.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.caffeine.e_commercedemo.R;
import com.caffeine.e_commercedemo.databinding.ItemLayoutBinding;
import com.caffeine.e_commercedemo.model.ProductDetails;
import com.caffeine.e_commercedemo.view.DetailsActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder>{

    private Context context;
    private ArrayList<ProductDetails> list;

    public ProductAdapter(Context context, ArrayList<ProductDetails> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemLayoutBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductDetails products = list.get(position);

        if (position%2==0){
            holder.binding.availabilty.setText("Sold out");
            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(
                    RecyclerView.LayoutParams.MATCH_PARENT,
                    RecyclerView.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(20, 20, 10, 20);
            holder.binding.getRoot().setLayoutParams(params);

            holder.binding.productName.setText(products.getName());
        }else {
            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(
                    RecyclerView.LayoutParams.MATCH_PARENT,
                    RecyclerView.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(10, 20, 20, 20);
            holder.binding.getRoot().setLayoutParams(params);

            holder.binding.productName.setText(products.getName());
            holder.binding.availabilty.setText("Available");
        }

        Picasso.get().load(products.getImages().get(0).getImage()).into(holder.binding.image);

        ArrayList<Integer> prices = new ArrayList<>();
        prices.add(Integer.parseInt(products.getVariants().get(0).getPrice()));
        prices.add(Integer.parseInt(products.getVariants().get(1).getPrice()));
        prices.add(Integer.parseInt(products.getVariants().get(2).getPrice()));
        prices.add(Integer.parseInt(products.getVariants().get(3).getPrice()));

        int minPrice=0, maxPrice=0;
        for (int price : prices){
            if (minPrice == 0) minPrice = price;
            else if (price < minPrice) minPrice = price;
            if (price>maxPrice) maxPrice = price;
        }

        holder.binding.currentPrice.setText("৳" + minPrice + " - ৳" + maxPrice);
        holder.binding.regularPrice.setText(products.getRprice());
        holder.binding.regularPrice.setPaintFlags(holder.binding.regularPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        holder.binding.getRoot().setOnClickListener(v -> {
            if (products.getId() != null){
                Intent intent = new Intent(context, DetailsActivity.class);
                intent.putExtra("id", products.getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        ItemLayoutBinding binding;

        public ViewHolder(ItemLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
