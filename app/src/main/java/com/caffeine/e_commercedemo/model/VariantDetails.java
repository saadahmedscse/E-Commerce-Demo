package com.caffeine.e_commercedemo.model;

public class VariantDetails {

    String color, size, price;

    public VariantDetails() {}

    public VariantDetails(String color, String size, String price) {
        this.color = color;
        this.size = size;
        this.price = price;
    }

    public String getColor() {
        return color;
    }

    public String getSize() {
        return size;
    }

    public String getPrice() {
        return price;
    }
}
