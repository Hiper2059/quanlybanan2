// --- SỬA FILE Item.java ---
package com.example.quanlybanan.model;

import java.io.Serializable;

public class Item implements Serializable {
    private String name;
    private int price;
    private int quantity;
    private String imagePath; // <-- THÊM DÒNG NÀY

    // Sửa lại Constructor
    public Item(String name, int price, String imagePath) { // <-- THÊM imagePath VÀO ĐÂY
        this.name = name;
        this.price = price;
        this.imagePath = imagePath; // <-- THÊM DÒNG NÀY
        this.quantity = 0;
    }

    // Constructor cũ để tương thích (nếu cần)
    public Item(String name, int price) {
        this(name, price, null); // Gọi constructor chính với imagePath là null
    }

    // Getter và Setter
    public String getName() { return name; }
    public int getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public String getImagePath() { return imagePath; } // <-- THÊM GETTER NÀY

    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getTotalPrice() { return price * quantity; }
}