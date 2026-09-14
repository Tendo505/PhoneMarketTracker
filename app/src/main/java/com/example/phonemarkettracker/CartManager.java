package com.example.phonemarkettracker;

import java.util.ArrayList;
import java.util.List;

//keeps the current cart in memory until checkout, sign out, or app closure.
public class CartManager {

    private static final List<CartItem> CART_ITEMS = new ArrayList<>();

    //1.shared cart setup
    private CartManager() {
    }

    //2.read cart and count units
    public static List<CartItem> getItems() {
        return new ArrayList<>(CART_ITEMS);
    }

    public static int getTotalQuantity() {
        //jumlah unit dalam troli.
        int totalQuantity = 0;

        for (CartItem cartItem : CART_ITEMS) {
            totalQuantity += cartItem.getQuantity();
        }

        return totalQuantity;
    }

    //3.add, change or remove selections
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
        CartItem cartItem = findItem(phoneId);
        return cartItem != null && cartItem.increaseQuantity();
    }

    public static void decreaseQuantity(int phoneId) {
        CartItem cartItem = findItem(phoneId);

        if (cartItem == null) {
            return;
        }

        if (!cartItem.decreaseQuantity()) {
            CART_ITEMS.remove(cartItem);
        }
    }

    public static void removeItem(int phoneId) {
        CartItem cartItem = findItem(phoneId);

        if (cartItem != null) {
            CART_ITEMS.remove(cartItem);
        }
    }

    public static void clear() {
        CART_ITEMS.clear();
    }

    //4.search for a selected phone
    private static CartItem findItem(int phoneId) {
        for (CartItem cartItem : CART_ITEMS) {
            if (cartItem.getPhone().getPhoneId() == phoneId) {
                return cartItem;
            }
        }

        return null;
    }
}
