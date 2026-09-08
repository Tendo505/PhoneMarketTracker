package com.example.phonemarkettracker;

import java.util.ArrayList;
import java.util.List;

/** Keeps the current cart in memory until checkout, sign out, or app closure. */
public final class CartManager {

    private static final List<CartItem> CART_ITEMS = new ArrayList<>();

    private CartManager() {
    }

    // create
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

    // read
    public static List<CartItem> getItems() {
        return new ArrayList<>(CART_ITEMS);
    }

    // read
    public static int getTotalQuantity() {
        int totalQuantity = 0;

        for (CartItem cartItem : CART_ITEMS) {
            totalQuantity += cartItem.getQuantity();
        }

        return totalQuantity;
    }

    // update
    public static boolean increaseQuantity(int phoneId) {
        CartItem cartItem = findItem(phoneId);
        return cartItem != null && cartItem.increaseQuantity();
    }

    // update
    public static void decreaseQuantity(int phoneId) {
        CartItem cartItem = findItem(phoneId);

        if (cartItem == null) {
            return;
        }

        if (!cartItem.decreaseQuantity()) {
            CART_ITEMS.remove(cartItem);
        }
    }

    // delete
    public static void removeItem(int phoneId) {
        CartItem cartItem = findItem(phoneId);

        if (cartItem != null) {
            CART_ITEMS.remove(cartItem);
        }
    }

    // delete
    public static void clear() {
        CART_ITEMS.clear();
    }

    // read
    private static CartItem findItem(int phoneId) {
        for (CartItem cartItem : CART_ITEMS) {
            if (cartItem.getPhone().getPhoneId() == phoneId) {
                return cartItem;
            }
        }

        return null;
    }
}
