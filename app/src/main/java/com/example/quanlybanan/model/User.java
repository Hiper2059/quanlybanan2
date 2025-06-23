// --- TẠO FILE MỚI: User.java ---
package com.example.quanlybanan.model;

public class User {
    private int id;
    String username;

    // Thêm một constructor rỗng để linh hoạt hơn nếu cần
    public User() {
    }

    public User(int id, String username) {
        this.id = id;
        this.username = username;
    }

    // Thêm các getter và setter để tuân thủ quy tắc đóng gói
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}