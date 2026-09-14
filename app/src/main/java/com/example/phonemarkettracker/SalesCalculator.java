package com.example.phonemarkettracker;

import java.util.List;

//calculates cart totals without depending on android ui classes.
public class SalesCalculator {

    //1.utility class setup
    private SalesCalculator() {
    }

    //2.calculate whole-cart totals
    public static double calculateTotalCost(List<CartItem> cartItems) {
        double totalCost = 0.0;

        for (CartItem cartItem : cartItems) {
            totalCost += cartItem.calculateCostTotal();
        }

        return totalCost;
    }

    public static double calculateTotalRevenue(List<CartItem> cartItems) {
        double totalRevenue = 0.0;

        for (CartItem cartItem : cartItems) {
            totalRevenue += cartItem.calculateRevenueTotal();
        }

        return totalRevenue;
    }

    public static double calculateProfitLoss(List<CartItem> cartItems) {
        return calculateTotalRevenue(cartItems) - calculateTotalCost(cartItems);
    }
}
