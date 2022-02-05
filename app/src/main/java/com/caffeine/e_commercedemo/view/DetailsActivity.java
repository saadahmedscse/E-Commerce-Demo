package com.caffeine.e_commercedemo.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.widget.Toast;

import com.caffeine.e_commercedemo.R;
import com.caffeine.e_commercedemo.adapter.ColorAdapter;
import com.caffeine.e_commercedemo.adapter.DetailedImageAdapter;
import com.caffeine.e_commercedemo.adapter.SizeAdapter;
import com.caffeine.e_commercedemo.databinding.ActivityDetailsBinding;
import com.caffeine.e_commercedemo.model.ProductDetails;
import com.caffeine.e_commercedemo.model.VariantDetails;
import com.caffeine.e_commercedemo.util.ColorInterface;
import com.caffeine.e_commercedemo.util.SizeInterface;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class DetailsActivity extends AppCompatActivity implements SizeInterface, ColorInterface {

    private ActivityDetailsBinding binding;
    private int pos = 2;
    private int quantity = 1;
    private String variant = "ws";
    private int cPrice = 0;
    private ProductDetails product;
    private ArrayList<String> size;
    private ArrayList<String> color;
    private HashMap<String, ArrayList<String>> sizeMap;
    private HashMap<String, String> priceMap;
    SizeAdapter sizeAdapter;
    ColorAdapter colorAdapter;

    private String selectedColor = "", selectedSize = "";
    private int selectedPrice=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager layoutManager3 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        binding.recyclerView.setLayoutManager(layoutManager);
        binding.sizeRecyclerView.setLayoutManager(layoutManager2);
        binding.colorRecyclerView.setLayoutManager(layoutManager3);
        cPrice = Integer.parseInt(binding.price.getText().toString());
        getProductDetails(id);

        size = new ArrayList<>();
        color = new ArrayList<>();
        sizeMap = new HashMap<>();
        priceMap = new HashMap<>();

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


        for (VariantDetails obj : product.getVariants()){
            if (sizeMap.containsKey(obj.getSize())){
                sizeMap.get(obj.getSize()).add(obj.getColor());
            }
            else {
                ArrayList<String> temp = new ArrayList<>();
                temp.add(obj.getColor());
                sizeMap.put(obj.getSize(), temp);
            }

            priceMap.put(obj.getColor() + obj.getSize(), obj.getPrice());
        }

        for (VariantDetails obj : product.getVariants()){
            color.add(obj.getColor());
        }

        size.addAll(sizeMap.keySet());

        sizeAdapter = new SizeAdapter(this, size, this);
        binding.sizeRecyclerView.setAdapter(sizeAdapter);

        colorAdapter = new ColorAdapter(this, color, binding.color, this);
        binding.colorRecyclerView.setAdapter(colorAdapter);

        cPrice = Integer.parseInt(product.getVariants().get(0).getPrice());

        binding.plus.setOnClickListener(v -> {
            if (selectedColor != ""){
                quantity++;
                int p = quantity * selectedPrice;
                binding.price.setText(String.valueOf(p));
                binding.quantity.setText(String.valueOf(quantity));
            }
            else {
                Toast.makeText(this, "Please select color first", Toast.LENGTH_SHORT).show();
            }
        });

        binding.minus.setOnClickListener(v -> {
            if (quantity != 1){
                int p = Integer.parseInt(binding.price.getText().toString()) - selectedPrice;
                quantity--;
                binding.price.setText(String.valueOf(p));
                binding.quantity.setText(String.valueOf(quantity));
            }
        });
    }

    @Override
    public void onColorClicked(String color, int position) {
        if (selectedSize != ""){
            selectedColor = color;
            binding.price.setText(priceMap.get(selectedColor + selectedSize));
            selectedPrice = Integer.parseInt(priceMap.get(selectedColor + selectedSize));
            colorAdapter.upadteBg(position);
        }
        else {
            Toast.makeText(this, "Please select size first", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSizeClicked(String size, int position) {
        if (selectedSize != size){
            selectedColor ="";
            selectedSize = size;
            colorAdapter.upadteBg(-1);
            colorAdapter.updateList(sizeMap.get(size));
            sizeAdapter.upadteBg(position);
        }
    }

//    private void changePrice(ProductDetails product){
//        switch (variant){
//            case "ws":
//                int p1 = quantity * Integer.parseInt(product.getVariants().get(0).getPrice());
//                binding.price.setText(String.valueOf(p1));
//                cPrice = Integer.parseInt(product.getVariants().get(0).getPrice());
//                break;
//            case "wl":int p2 = quantity * Integer.parseInt(product.getVariants().get(1).getPrice());
//                binding.price.setText(String.valueOf(p2));
//                cPrice = Integer.parseInt(product.getVariants().get(1).getPrice());
//                break;
//            case "bs":int p3 = quantity * Integer.parseInt(product.getVariants().get(2).getPrice());
//                binding.price.setText(String.valueOf(p3));
//                cPrice = Integer.parseInt(product.getVariants().get(2).getPrice());
//                break;
//            case "bl":int p4 = quantity * Integer.parseInt(product.getVariants().get(3).getPrice());
//                binding.price.setText(String.valueOf(p4));
//                cPrice = Integer.parseInt(product.getVariants().get(3).getPrice());
//                break;
//        }
//    }
}