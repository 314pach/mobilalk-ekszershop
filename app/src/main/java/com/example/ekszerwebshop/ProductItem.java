package com.example.ekszerwebshop;

public class ProductItem {
    private String name;
    private String price;
    private int imageRes;

    public ProductItem() {
    }

    public ProductItem(String name, String price, int imageRes) {
        this.name = name;
        this.price = price;
        this.imageRes = imageRes;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public int getImageRes() {
        return imageRes;
    }
}
