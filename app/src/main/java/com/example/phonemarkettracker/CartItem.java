package com.example.phonemarkettracker;

/** Stores a selected phone and its requested quantity. */
public class CartItem {

    private final Phone phone;
    private int quantity;

    public CartItem(Phone phone) {
        this.phone = phone;
        quantity = 1;
    }

    public Phone getPhone() {
        return phone;
    }

    public int getQuantity() {
        return quantity;
    }

    // update
    public boolean increaseQuantity() {
        if (quantity >= phone.getStockQuantity()) {
            return false;
        }

        quantity++;
        return true;
    }

    // update
    public boolean decreaseQuantity() {
        if (quantity <= 1) {
            return false;
        }

        quantity--;
        return true;
    }

    // calculate result
    public double calculateCostTotal() {
        return phone.getCostPrice() * quantity;
    }

    // calculate result
    public double calculateRevenueTotal() {
        return phone.getSellingPrice() * quantity;
    }

    // calculate result
    public double calculateProfitLoss() {
        return calculateRevenueTotal() - calculateCostTotal();
    }
}
