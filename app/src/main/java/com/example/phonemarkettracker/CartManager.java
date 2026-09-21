package com.example.phonemarkettracker;

import java.util.ArrayList;
import java.util.List;

//shared in-memory cart.
public class CartManager {
    private static final List<CartItem> CART_ITEMS = new ArrayList<>();

    private CartManager() { }

    public static List<CartItem> getItems() {
        return new ArrayList<>(CART_ITEMS);
    }

    public static int getTotalQuantity() {
        //jumlah unit dalam troli.
        int totalQuantity = 0;
        for (CartItem item : CART_ITEMS) {
            totalQuantity += item.getQuantity();
        }
        return totalQuantity;
    }

    public static boolean addPhone(Phone phone) {
        CartItem existingItem = findItem(phone.getPhoneId());
        if (existingItem != null) {
            return existingItem.increaseQuantity();
        }
        if (phone.getStockQuantity() <= 0) {
            return false;
        }
        CART_ITEMS.add(new CartItem(phone));
        return true;
    }

    public static boolean increaseQuantity(int phoneId) {
        CartItem item = findItem(phoneId);
        return item != null && item.increaseQuantity();
    }

    public static void decreaseQuantity(int phoneId) {
        CartItem item = findItem(phoneId);
        if (item == null) {
            return;
        }
        if (!item.decreaseQuantity()) {
            CART_ITEMS.remove(item);
        }
    }

    public static void clearCart() {
        CART_ITEMS.clear();
    }

    private static CartItem findItem(int phoneId) {
        for (CartItem item : CART_ITEMS) {
            if (item.getPhone().getPhoneId() == phoneId) {
                return item;
            }
        }
        return null;
    }
}
