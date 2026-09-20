package com.example.phonemarkettracker;

import java.util.List;

//calculates cart totals without depending on android ui classes.
public class SalesCalculator {

    //1.utility class setup
    private SalesCalculator() {
    }

    //2.item formulas
    public static double calculateItemCost(CartItem item) {
        //kos seunit x kuantiti.
        return item.getPhone().getCostPrice() * item.getQuantity();
    }

    public static double calculateItemRevenue(CartItem item) {
        //harga jual x kuantiti.
        return item.getPhone().getSellingPrice() * item.getQuantity();
    }

    public static double calculateItemProfitLoss(CartItem item) {
        //hasil - kos.
        return calculateItemRevenue(item) - calculateItemCost(item);
    }

    //3.calculate whole-cart totals
    public static double calculateTotalCost(List<CartItem> cartItems) {
        double totalCost = 0.0;

        for (CartItem cartItem : cartItems) {
            totalCost += calculateItemCost(cartItem);
        }

        return totalCost;
    }

    public static double calculateTotalRevenue(List<CartItem> cartItems) {
        double totalRevenue = 0.0;

        for (CartItem cartItem : cartItems) {
            totalRevenue += calculateItemRevenue(cartItem);
        }

        return totalRevenue;
    }

    public static double calculateProfitLoss(List<CartItem> cartItems) {
        return calculateTotalRevenue(cartItems) - calculateTotalCost(cartItems);
    }
    //4.calculated values for storage
    public static class Totals {
        private final double totalCost;
        private final double totalRevenue;
        private final double profitLoss;

        public Totals(double totalCost, double totalRevenue, double profitLoss) {
            this.totalCost = totalCost;
            this.totalRevenue = totalRevenue;
            this.profitLoss = profitLoss;
        }

        public double getTotalCost() { return totalCost; }
        public double getTotalRevenue() { return totalRevenue; }
        public double getProfitLoss() { return profitLoss; }
    }
}
