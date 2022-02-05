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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.caffeine.e_commercedemo.R;
import com.caffeine.e_commercedemo.adapter.ImageAdapter;
import com.caffeine.e_commercedemo.databinding.ActivityAddBinding;
import com.caffeine.e_commercedemo.databinding.VariantLayoutBinding;
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
    private String name, desc, rprice;
    private ArrayList<Uri> images;
    private ArrayList<ImageDetails> url;
    private ArrayList<VariantDetails> variants;
    private final int IMAGE_CODE = 1;
    private int temp = 0;

    private ArrayList<View> variantView;

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
        variantView = new ArrayList<>();

        addVariant();

        binding.uploadBtn.setOnClickListener(v -> {
            initialize();
            if (validate() && assignToList()){
                binding.progressBar.setVisibility(View.VISIBLE);
                binding.uploadTxt.setVisibility(View.GONE);
                uploadImagesToFirebase();
            }
        });

        binding.addImageBtn.setOnClickListener(v -> {
            addImages();
        });

        binding.addVariantBtn.setOnClickListener(v -> {
            if (lastVariantValidate()){
                addVariant();
            }
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

    private void addVariant(){
        View view = LayoutInflater.from(this).inflate(R.layout.variant_layout, null, false);

        view.findViewById(R.id.delete).setOnClickListener(v -> {
            if (variantView.size() > 1){
                variantView.remove(view);
                binding.variantLayout.removeView(view);
            }
            else {
                Toast.makeText(this, "Minimum one variant is required", Toast.LENGTH_SHORT).show();
            }
        });

        binding.variantLayout.addView(view);
        variantView.add(view);
    }

    private boolean lastVariantValidate(){
        if (variantView.size() != 0){
            View view = variantView.get(variantView.size() - 1);
            EditText size, color, price;
            size = view.findViewById(R.id.size);
            price = view.findViewById(R.id.price);
            color = view.findViewById(R.id.color);

            if (size.getText().toString().isEmpty() || color.getText().toString().isEmpty() || price.getText().toString().isEmpty()){
                Toast.makeText(this, "Fill the previous variant filed first", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    private boolean assignToList(){
        variants.clear();
        for (View v : variantView){
            EditText size, color, price;
            size = v.findViewById(R.id.size);
            price = v.findViewById(R.id.price);
            color = v.findViewById(R.id.color);

            if (size.getText().toString().isEmpty() || color.getText().toString().isEmpty() || price.getText().toString().isEmpty()){
                Toast.makeText(this, "Fill the variant filled with valid details", Toast.LENGTH_SHORT).show();
                return false;
            }
            VariantDetails vnt = new VariantDetails(color.getText().toString(), size.getText().toString(), price.getText().toString());
            variants.add(vnt);
        }
        return true;
    }

    private void initialize(){
        name = binding.productName.getText().toString();
        desc = binding.productDesc.getText().toString();
        rprice = binding.regularPrice.getText().toString();
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