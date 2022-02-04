package com.caffeine.e_commercedemo.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Toast;

import com.caffeine.e_commercedemo.R;
import com.caffeine.e_commercedemo.adapter.ImageAdapter;
import com.caffeine.e_commercedemo.databinding.ActivityAddBinding;
import com.caffeine.e_commercedemo.model.ImageDetails;
import com.caffeine.e_commercedemo.model.ProductDetails;
import com.caffeine.e_commercedemo.model.VariantDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Random;

public class AddActivity extends AppCompatActivity {

    private ActivityAddBinding binding;
    private String name, desc, rprice, ws, wl, bs, bl;
    private ArrayList<Uri> images;
    private ArrayList<ImageDetails> url;
    private ArrayList<VariantDetails> variants;
    private final int IMAGE_CODE = 1;
    private int temp = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        binding.recyclerView.setLayoutManager(layoutManager);

        images = new ArrayList<>();
        variants = new ArrayList<>();
        url = new ArrayList<>();

        binding.uploadBtn.setOnClickListener(v -> {
            initialize();
            if (validate()){
                variants.add(new VariantDetails("white", "small", ws));
                variants.add(new VariantDetails("white", "large", wl));
                variants.add(new VariantDetails("black", "small", bs));
                variants.add(new VariantDetails("black", "large", bl));
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.uploadTxt.setVisibility(View.GONE);
                uploadImagesToFirebase();
            }
        });

        binding.addImageBtn.setOnClickListener(v -> {
            addImages();
        });
    }

    private void addImages(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(
                Intent.createChooser(intent, "Choose product images"),
                IMAGE_CODE
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null && requestCode == IMAGE_CODE && resultCode == Activity.RESULT_OK && data.getClipData() != null) {
            images.clear();

            for (int i=0; i<data.getClipData().getItemCount(); i++){
                images.add(data.getClipData().getItemAt(i).getUri());
            }

            ImageAdapter adapter = new ImageAdapter(images);
            binding.recyclerView.setAdapter(adapter);
        }
    }

    private void initialize(){
        name = binding.productName.getText().toString();
        desc = binding.productDesc.getText().toString();
        rprice = binding.regularPrice.getText().toString();
        ws = binding.wsPrice.getText().toString();
        wl = binding.wlPrice.getText().toString();
        bs = binding.bsPrice.getText().toString();
        bl = binding.blPrice.getText().toString();
    }

    private boolean validate(){
        boolean v = true;

        if (name.isEmpty()){
            binding.productName.setError("Filed cannot be empty");
            v=false;
        }
        else if (desc.isEmpty()){
            binding.productDesc.setError("Filed cannot be empty");
            v=false;
        }

        else if (rprice.isEmpty()){
            binding.regularPrice.setError("Filed cannot be empty");
            v=false;
        }

        else if (ws.isEmpty()){
            binding.wsPrice.setError("Filed cannot be empty");
            v=false;
        }

        else if (wl.isEmpty()){
            binding.wlPrice.setError("Filed cannot be empty");
            v=false;
        }

        else if (bs.isEmpty()){
            binding.bsPrice.setError("Filed cannot be empty");
            v=false;
        }

        else if (bl.isEmpty()){
            binding.blPrice.setError("Filed cannot be empty");
            v=false;
        }

        else if (images.isEmpty()){
            Toast.makeText(this, "Please select at least 2 images", Toast.LENGTH_SHORT).show();
            v=false;
        }

        return v;
    }

    private void uploadImagesToFirebase(){
        for (int i=0; i<images.size(); i++){

            String fileName = Long.toString(System.currentTimeMillis()) + new Random().nextInt() + getFileName(images.get(i));
            StorageReference strRef = FirebaseStorage.getInstance().getReference().child("product_images/" + fileName);

            strRef.putFile(images.get(i)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    strRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            temp++;
                            url.add(new ImageDetails(uri.toString()));
                            if (temp==images.size()){
                                uploadToFirebase();
                            }
                        }
                    });
                }
            });
        }
    }

    private void uploadToFirebase(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("ECommerce").child("Products");
        String id = Long.toString(System.currentTimeMillis());

        ProductDetails products = new ProductDetails(id, name, desc, rprice, url, variants);
        ref.child(id).setValue(products).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(AddActivity.this, "Product Uploaded Successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AddActivity.this, DashboardActivity.class));
                    finish();
                }
                else {
                    Toast.makeText(AddActivity.this, "Upload failed", Toast.LENGTH_SHORT).show();
                    binding.progressBar.setVisibility(View.GONE);
                    binding.uploadTxt.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @SuppressLint("Range")
    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(AddActivity.this, DashboardActivity.class));
        finish();
    }
}