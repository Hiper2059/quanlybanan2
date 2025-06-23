package com.example.quanlybanan.model;

import java.io.Serializable;

public class MenuItemObject implements Serializable {
    private int id;
    private String name;
    private int price;
    private String imagePath;

    public MenuItemObject(int id, String name, int price, String imagePath) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imagePath = imagePath;
    }

    // Getters and Setters
    public int getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
}