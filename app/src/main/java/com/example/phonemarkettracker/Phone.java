package com.example.phonemarkettracker;

/** Stores one phone record read from SQLite. */
public class Phone {

    private final int phoneId;
    private final String brand;
    private final String model;
    private final double costPrice;
    private final double sellingPrice;
    private final int stockQuantity;

    public Phone(
            int phoneId,
            String brand,
            String model,
            double costPrice,
            double sellingPrice,
            int stockQuantity
    ) {
        this.phoneId = phoneId;
        this.brand = brand;
        this.model = model;
        this.costPrice = costPrice;
        this.sellingPrice = sellingPrice;
        this.stockQuantity = stockQuantity;
    }

    public int getPhoneId() {
        return phoneId;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public double getCostPrice() {
        return costPrice;
    }

    public double getSellingPrice() {
        return sellingPrice;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public String getDisplayName() {
        return brand + " " + model;
    }
}
