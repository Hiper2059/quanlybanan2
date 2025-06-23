package com.example.quanlybanan.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Table implements Serializable {
    private int id;
    private int seats;
    private String status;
    private ArrayList<Item> items;  // Danh sách các món đã chọn

    // Constructor
    public Table(int id, int seats, String status) {
        this.id = id;
        this.seats = seats;
        this.status = status;
        this.items = new ArrayList<>();  // Khởi tạo danh sách món ăn
    }

    // Getter và Setter
    public int getId() { return id; }
    public int getSeats() { return seats; }
    public String getStatus() { return status; }

    public void setSeats(int seats) { this.seats = seats; }
    public void setStatus(String status) { this.status = status; }

    // Thêm món vào danh sách
    public Item addItem(Item existingItem) {
        // Loop through the existing items in the table
        for (Item item : items) {
            // If the item already exists (based on its name or id)
            if (item.getName().equals(existingItem.getName())) {
                // Increment the quantity of the existing item by 1
                item.setQuantity(item.getQuantity());
                return item; // Return the updated item
            }
        }
        // If the item does not exist in the list, add it to the list
        items.add(existingItem);  // Add the new item if it doesn't exist
        return existingItem; // Return the newly added item
    }


    // Lấy danh sách các món đã chọn
    public ArrayList<Item> getItems() {
        return items;
    }

    // Tính tổng tiền theo các món đã chọn
    public int calculateTotal() {
        int total = 0;
        for (Item item : items) {
            total += item.getPrice() * item.getQuantity();
        }
        return total;
    }
    public void setItems(ArrayList<Item> items) {
        this.items = items;
    }
    public void increaseItemQuantity(Item menuItem) {
        // Tìm xem món đã có trong đơn hàng chưa
        for (Item itemInOrder : items) {
            if (itemInOrder.getName().equals(menuItem.getName())) {
                // Nếu có, chỉ cần tăng số lượng lên 1
                itemInOrder.setQuantity(itemInOrder.getQuantity() + 1);
                return; // Kết thúc hàm
            }
        }
        // Nếu vòng lặp kết thúc mà không tìm thấy, nghĩa là món này chưa có trong đơn
        // Tạo một bản sao của món từ menu, đặt số lượng là 1 và thêm vào đơn hàng
        Item newItem = new Item(menuItem.getName(), menuItem.getPrice(), menuItem.getImagePath());
        newItem.setQuantity(1);
        items.add(newItem);
    }

    // << HÀM MỚI: Giảm số lượng của một món >>
    public void decreaseItemQuantity(Item menuItem) {
        Item itemToRemove = null;
        for (Item itemInOrder : items) {
            if (itemInOrder.getName().equals(menuItem.getName())) {
                // Giảm số lượng đi 1
                itemInOrder.setQuantity(itemInOrder.getQuantity() - 1);
                // Nếu số lượng về 0 hoặc ít hơn, đánh dấu để xóa
                if (itemInOrder.getQuantity() <= 0) {
                    itemToRemove = itemInOrder;
                }
                break; // Tìm thấy rồi thì thoát vòng lặp
            }
        }
        // Nếu có món cần xóa, thực hiện xóa khỏi danh sách
        if (itemToRemove != null) {
            items.remove(itemToRemove);
        }
    }

    // << HÀM MỚI: Lấy số lượng của một món trong đơn hàng >>
    public int getQuantityOfItem(String itemName) {
        for (Item itemInOrder : items) {
            if (itemInOrder.getName().equals(itemName)) {
                return itemInOrder.getQuantity();
            }
        }
        // Nếu không tìm thấy, trả về 0
        return 0;
    }

}
