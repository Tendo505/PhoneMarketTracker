package com.example.phonemarkettracker;

//stores a selected phone and its requested quantity.
public class CartItem {

    private final Phone phone;
    private int quantity;

    //1.store the selected phone
    public CartItem(Phone phone) {
        this.phone = phone;
        quantity = 1;
    }

    //2.read item data
    public Phone getPhone() {
        return phone;
    }

    public int getQuantity() {
        return quantity;
    }

    //3.change quantity within stock limits
    public boolean increaseQuantity() {
        if (quantity >= phone.getStockQuantity()) {
            return false;
        }

        quantity++;
        return true;
    }

    public boolean decreaseQuantity() {
        if (quantity <= 1) {
            return false;
        }

        quantity--;
        return true;
    }

    //4.calculate this item's totals
    public double calculateCostTotal() {
        return phone.getCostPrice() * quantity;
    }

    public double calculateRevenueTotal() {
        return phone.getSellingPrice() * quantity;
    }

    public double calculateProfitLoss() {
        return calculateRevenueTotal() - calculateCostTotal();
    }
}
