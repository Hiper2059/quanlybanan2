package com.example.quanlybanan.model;

public class RevenueReportItem {
    private String itemName;
    private int totalQuantity;
    private int totalRevenue;

    public RevenueReportItem(String itemName, int totalQuantity, int totalRevenue) {
        this.itemName = itemName;
        this.totalQuantity = totalQuantity;
        this.totalRevenue = totalRevenue;
    }
    public String getItemName() {
        return itemName;
    }
    public int getTotalQuantity() {
        return totalQuantity;
    }
    public int getTotalRevenue() {
        return totalRevenue;
    }
}