package com.caffeine.e_commercedemo.model;

import java.util.ArrayList;

public class ProductDetails {

    String id, name, description, rprice;
    ArrayList<ImageDetails> images;
    ArrayList<VariantDetails> variants;

    public ProductDetails() {}

    public ProductDetails(String id, String name, String description, String rprice, ArrayList<ImageDetails> images, ArrayList<VariantDetails> variants) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.rprice = rprice;
        this.images = images;
        this.variants = variants;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getRprice() {
        return rprice;
    }

    public ArrayList<ImageDetails> getImages() {
        return images;
    }

    public ArrayList<VariantDetails> getVariants() {
        return variants;
    }
}
