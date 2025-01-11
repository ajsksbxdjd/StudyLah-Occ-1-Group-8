package com.example.studylah;

import android.graphics.Bitmap;

public class Market_BookItem {
    private Bitmap image;
    private String title;
    private String price;
    private String id;

    public Market_BookItem(Bitmap image, String title, String price, String id) {
        this.image = image;
        this.title = title;
        this.price = price;
        this.id = id;
    }

    public Bitmap getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public String getPrice() {
        return "RM" + price;
    }

    public String getId() {
        return id;
    }
}