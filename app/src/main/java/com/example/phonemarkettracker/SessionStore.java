package com.example.phonemarkettracker;

/**
 * Temporary runtime storage required by the assignment.
 * These multidimensional arrays reset when the app process is closed.
 */
public final class SessionStore {

    public static final int MAX_CART_ITEMS = 50;
    public static final int MAX_SESSION_ITEMS = 100;

    // Columns: ID, brand, model, cost, selling, quantity, profit/loss.
    public static final Object[][] cartData = new Object[MAX_CART_ITEMS][7];
    public static final Object[][] sessionSales = new Object[MAX_SESSION_ITEMS][7];

    public static int cartCount = 0;
    public static int sessionCount = 0;

    private SessionStore() {
    }

    public static boolean addToCart(Object[] phone, int quantity) {
        int phoneId = (int) phone[0];

        for (int i = 0; i < cartCount; i++) {
            if ((int) cartData[i][0] == phoneId) {
                int newQuantity = (int) cartData[i][5] + quantity;
                cartData[i][5] = newQuantity;
                cartData[i][6] = ((double) cartData[i][4] - (double) cartData[i][3])
                        * newQuantity;
                return true;
            }
        }

        if (cartCount >= MAX_CART_ITEMS) {
            return false;
        }

        cartData[cartCount][0] = phone[0];
        cartData[cartCount][1] = phone[1];
        cartData[cartCount][2] = phone[2];
        cartData[cartCount][3] = phone[3];
        cartData[cartCount][4] = phone[4];
        cartData[cartCount][5] = quantity;
        cartData[cartCount][6] = ((double) phone[4] - (double) phone[3]) * quantity;
        cartCount++;
        return true;
    }

    public static int getCartQuantity(int phoneId) {
        for (int i = 0; i < cartCount; i++) {
            if ((int) cartData[i][0] == phoneId) {
                return (int) cartData[i][5];
            }
        }
        return 0;
    }

    public static void removeCartItem(int position) {
        if (position < 0 || position >= cartCount) {
            return;
        }
        for (int i = position; i < cartCount - 1; i++) {
            System.arraycopy(cartData[i + 1], 0, cartData[i], 0, 7);
        }
        for (int column = 0; column < 7; column++) {
            cartData[cartCount - 1][column] = null;
        }
        cartCount--;
    }

    public static double getCartRevenue() {
        double total = 0;
        for (int i = 0; i < cartCount; i++) {
            total += (double) cartData[i][4] * (int) cartData[i][5];
        }
        return total;
    }

    public static double getCartCost() {
        double total = 0;
        for (int i = 0; i < cartCount; i++) {
            total += (double) cartData[i][3] * (int) cartData[i][5];
        }
        return total;
    }

    public static void updateCartQuantity(int position, int quantity) {
        if (position < 0 || position >= cartCount || quantity < 1) {
            return;
        }
        cartData[position][5] = quantity;
        cartData[position][6] = ((double) cartData[position][4]
                - (double) cartData[position][3]) * quantity;
    }

    public static double getCartProfitLoss() {
        double total = 0;
        for (int i = 0; i < cartCount; i++) {
            total += (double) cartData[i][6];
        }
        return total;
    }

    public static void recordCompletedCart() {
        for (int i = 0; i < cartCount; i++) {
            recordSale(cartData[i]);
        }
    }

    private static void recordSale(Object[] cartRow) {
        int phoneId = (int) cartRow[0];
        for (int i = 0; i < sessionCount; i++) {
            if ((int) sessionSales[i][0] == phoneId) {
                int newQuantity = (int) sessionSales[i][5] + (int) cartRow[5];
                sessionSales[i][5] = newQuantity;
                sessionSales[i][6] = ((double) sessionSales[i][4]
                        - (double) sessionSales[i][3]) * newQuantity;
                return;
            }
        }

        if (sessionCount >= MAX_SESSION_ITEMS) {
            return;
        }
        System.arraycopy(cartRow, 0, sessionSales[sessionCount], 0, 7);
        sessionCount++;
    }

    public static String getMostSoldPhone() {
        if (sessionCount == 0) {
            return "No sales yet";
        }
        int bestIndex = 0;
        for (int i = 1; i < sessionCount; i++) {
            if ((int) sessionSales[i][5] > (int) sessionSales[bestIndex][5]) {
                bestIndex = i;
            }
        }
        return sessionSales[bestIndex][1] + " " + sessionSales[bestIndex][2]
                + " (" + sessionSales[bestIndex][5] + " sold)";
    }

    public static double getSessionProfitLoss() {
        double total = 0;
        for (int i = 0; i < sessionCount; i++) {
            total += (double) sessionSales[i][6];
        }
        return total;
    }

    public static void clearCart() {
        for (int row = 0; row < cartCount; row++) {
            for (int column = 0; column < 7; column++) {
                cartData[row][column] = null;
            }
        }
        cartCount = 0;
    }

    public static void resetSession() {
        clearCart();
        for (int row = 0; row < sessionCount; row++) {
            for (int column = 0; column < 7; column++) {
                sessionSales[row][column] = null;
            }
        }
        sessionCount = 0;
    }
}
