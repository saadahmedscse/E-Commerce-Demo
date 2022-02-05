package com.caffeine.e_commercedemo.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.caffeine.e_commercedemo.adapter.ProductAdapter;
import com.caffeine.e_commercedemo.databinding.ActivityDashboardBinding;
import com.caffeine.e_commercedemo.model.ProductDetails;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DashboardActivity extends AppCompatActivity {

    private ActivityDashboardBinding binding;
    private DatabaseReference ref;
    private ArrayList<ProductDetails> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ref = FirebaseDatabase.getInstance().getReference().child("ECommerce").child("Users").child(FirebaseAuth.getInstance().getUid());
        RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
        binding.recyclerView.setLayoutManager(layoutManager);
        list = new ArrayList<>();

        getUserData();
        getProducts();

        binding.addProduct.setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, AddActivity.class));
            finish();
        });

        binding.logout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void getUserData(){
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String accType = snapshot.child("acc").getValue(String.class);
                if (accType.equals("Individual")) binding.addProduct.setVisibility(View.GONE);
                else binding.addProduct.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getProducts(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("ECommerce").child("Products");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list.clear();
                for (DataSnapshot ds : snapshot.getChildren()){
                    list.add(ds.getValue(ProductDetails.class));
                }

                ProductAdapter adapter = new ProductAdapter(DashboardActivity.this, list);

                if (adapter.getItemCount() > 0){
                    binding.progressBar.setVisibility(View.GONE);
                }

                binding.recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}