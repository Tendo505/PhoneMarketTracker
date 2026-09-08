package com.example.phonemarkettracker;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class SalesCalculatorTest {

    private static final double DELTA = 0.001;

    // calculate result
    @Test
    public void calculatesCostRevenueAndProfitForMultipleItems() {
        Phone firstPhone = new Phone(
                1,
                "Apple",
                "iPhone 15 128GB",
                2850.00,
                3299.00,
                5
        );
        Phone secondPhone = new Phone(
                2,
                "Xiaomi",
                "Redmi Note 13",
                650.00,
                799.00,
                10
        );

        CartItem firstItem = new CartItem(firstPhone);
        firstItem.increaseQuantity();
        CartItem secondItem = new CartItem(secondPhone);

        List<CartItem> cartItems = new ArrayList<>();
        cartItems.add(firstItem);
        cartItems.add(secondItem);

        assertEquals(6350.00, SalesCalculator.calculateTotalCost(cartItems), DELTA);
        assertEquals(7397.00, SalesCalculator.calculateTotalRevenue(cartItems), DELTA);
        assertEquals(1047.00, SalesCalculator.calculateProfitLoss(cartItems), DELTA);
    }

    // validate input
    @Test
    public void preventsQuantityFromExceedingAvailableStock() {
        Phone phone = new Phone(
                1,
                "Apple",
                "iPhone 15 128GB",
                2850.00,
                3299.00,
                2
        );
        CartItem cartItem = new CartItem(phone);

        assertEquals(true, cartItem.increaseQuantity());
        assertEquals(false, cartItem.increaseQuantity());
        assertEquals(2, cartItem.getQuantity());
    }
}
