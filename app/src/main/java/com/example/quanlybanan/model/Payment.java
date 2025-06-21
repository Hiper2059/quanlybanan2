package com.example.quanlybanan.model;

public class Payment {
    private String tableId;
    private double amount;
    private long timestamp;

    public Payment(String tableId, double amount, long timestamp) {
        this.tableId = tableId;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public String getTableId() {
        return tableId;
    }

    public double getAmount() {
        return amount;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
