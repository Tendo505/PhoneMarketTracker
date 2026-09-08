package com.example.phonemarkettracker;

import java.util.List;

/** Calculates cart totals without depending on Android UI classes. */
public class SalesCalculator {

    private SalesCalculator() {
    }

    // calculate result
    public static double calculateTotalCost(List<CartItem> cartItems) {
        double totalCost = 0.0;

        for (CartItem cartItem : cartItems) {
            totalCost += cartItem.calculateCostTotal();
        }

        return totalCost;
    }

    // calculate result
    public static double calculateTotalRevenue(List<CartItem> cartItems) {
        double totalRevenue = 0.0;

        for (CartItem cartItem : cartItems) {
            totalRevenue += cartItem.calculateRevenueTotal();
        }

        return totalRevenue;
    }

    // calculate result
    public static double calculateProfitLoss(List<CartItem> cartItems) {
        return calculateTotalRevenue(cartItems) - calculateTotalCost(cartItems);
    }
}
