package com.example.phonemarkettracker;

public class CartItem {
    private final Phone phone;
    private int quantity;

    public CartItem(Phone phone, int quantity) {
        this.phone = phone;
        this.quantity = quantity;
    }

    public Phone getPhone() { return phone; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getLineTotal() {
        return phone.getSellingPrice() * quantity;
    }
    public double getLineCost() {
        return phone.getCostPrice() * quantity;
    }
    public double getLineProfit() {
        return (phone.getSellingPrice() - phone.getCostPrice()) * quantity;
    }
}
