package com.caffeine.e_commercedemo.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.widget.Toast;

import com.caffeine.e_commercedemo.R;
import com.caffeine.e_commercedemo.adapter.DetailedImageAdapter;
import com.caffeine.e_commercedemo.databinding.ActivityDetailsBinding;
import com.caffeine.e_commercedemo.model.ProductDetails;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class DetailsActivity extends AppCompatActivity {

    private ActivityDetailsBinding binding;
    private int pos = 2;
    private int quantity = 1;
    private String size = "s", color = "w";
    private String variant = "ws";
    private int cPrice = 0;
    private ProductDetails product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        binding.recyclerView.setLayoutManager(layoutManager);
        cPrice = Integer.parseInt(binding.price.getText().toString());
        getProductDetails(id);

        binding.right.setOnClickListener(v -> {
            if (pos < product.getImages().size()-1){
                pos++;
                binding.recyclerView.smoothScrollToPosition(pos);
            }
        });

        binding.left.setOnClickListener(v -> {
            if (pos > 0){
                pos--;
                binding.recyclerView.smoothScrollToPosition(pos);
            }
        });
    }

    private void getProductDetails(String id){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("ECommerce").child("Products").child(id);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                product = snapshot.getValue(ProductDetails.class);
                
                showDetailsToActivity(product);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showDetailsToActivity(ProductDetails product) {
        Picasso.get().load(product.getImages().get(0).getImage()).into(binding.headerImage);

        DetailedImageAdapter adapter = new DetailedImageAdapter(this, binding.headerImage, product.getImages(), binding.left, binding.right);
        binding.recyclerView.setAdapter(adapter);



        binding.productName.setText(product.getName());
        binding.productDesc.setText(product.getDescription());
        binding.price.setText(product.getVariants().get(0).getPrice());
        binding.regularPrice.setText(product.getRprice());
        binding.regularPrice.setPaintFlags(binding.regularPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        binding.small.setOnClickListener(v -> {
            binding.small.setBackgroundResource(R.drawable.stroke_grey_filled_orange);
            binding.large.setBackgroundResource(R.drawable.bg_grey_square);
            size = "s";
            variant = color + size;
            changePrice(product);
        });

        binding.large.setOnClickListener(v -> {
            binding.large.setBackgroundResource(R.drawable.stroke_grey_filled_orange);
            binding.small.setBackgroundResource(R.drawable.bg_grey_square);
            size = "l";
            variant = color + size;
            changePrice(product);
        });

        binding.white.setOnClickListener(v -> {
            binding.white.setBackgroundResource(R.drawable.stroke_grey_filled_orange);
            binding.black.setBackgroundResource(R.drawable.bg_grey_square);
            color = "w";
            variant = color + size;
            changePrice(product);
        });

        binding.black.setOnClickListener(v -> {
            binding.black.setBackgroundResource(R.drawable.stroke_grey_filled_orange);
            binding.white.setBackgroundResource(R.drawable.bg_grey_square);
            color = "b";
            variant = color + size;
            changePrice(product);
        });

        binding.plus.setOnClickListener(v -> {
            quantity++;
            int p = quantity * cPrice;
            binding.price.setText(String.valueOf(p));
            binding.quantity.setText(String.valueOf(quantity));
        });

        binding.minus.setOnClickListener(v -> {
            if (quantity != 1){
                int p = Integer.parseInt(binding.price.getText().toString()) - cPrice;
                quantity--;
                binding.price.setText(String.valueOf(p));
                binding.quantity.setText(String.valueOf(quantity));
            }
        });
    }

    private void changePrice(ProductDetails product){
        switch (variant){
            case "ws":
                int p1 = quantity * Integer.parseInt(product.getVariants().get(0).getPrice());
                binding.price.setText(String.valueOf(p1));
                cPrice = Integer.parseInt(product.getVariants().get(0).getPrice());
                break;
            case "wl":int p2 = quantity * Integer.parseInt(product.getVariants().get(1).getPrice());
                binding.price.setText(String.valueOf(p2));
                cPrice = Integer.parseInt(product.getVariants().get(1).getPrice());
                break;
            case "bs":int p3 = quantity * Integer.parseInt(product.getVariants().get(2).getPrice());
                binding.price.setText(String.valueOf(p3));
                cPrice = Integer.parseInt(product.getVariants().get(2).getPrice());
                break;
            case "bl":int p4 = quantity * Integer.parseInt(product.getVariants().get(3).getPrice());
                binding.price.setText(String.valueOf(p4));
                cPrice = Integer.parseInt(product.getVariants().get(3).getPrice());
                break;
        }
    }
}