package com.example.phonemarkettracker;

import android.util.Patterns;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

//account checks, checkout and daily tracking.
public class AppProcesses {
    private final DatabasePMT database;
    private static final List<CartItem> CART_ITEMS = new ArrayList<>();
    private static int userId = -1;

    public AppProcesses(DatabasePMT database) {
        this.database = database;
    }

    public enum Field { NAME, EMAIL, PASSWORD, CONFIRM_PASSWORD }

    public static class Error {
        public final Field field;
        public final String message;

        private Error(Field field, String message) {
            this.field = field;
            this.message = message;
        }
    }

    //1.sign-in rules
    public static Error validateLogin(String email, String password) {
        if (email.isEmpty()) return new Error(Field.EMAIL, "Enter your email address");
        if (password.isEmpty()) return new Error(Field.PASSWORD, "Enter your password");
        return null;
    }

    //2.registration rules
    public static Error validateRegistration(String name, String email,
                                              String password, String confirmation) {
        if (name.isEmpty()) return new Error(Field.NAME, "Enter your full name");
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return new Error(Field.EMAIL, "Enter a valid email address");
        }
        if (password.length() < 6) return new Error(Field.PASSWORD, "Use at least 6 characters");
        if (!password.equals(confirmation)) {
            return new Error(Field.CONFIRM_PASSWORD, "Passwords do not match");
        }
        return null;
    }

    //3.cart selections and quantity
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

    public static void clearCart() {
        CART_ITEMS.clear();
    }

    private static CartItem findItem(int phoneId) {
        for (CartItem cartItem : CART_ITEMS) {
            if (cartItem.getPhone().getPhoneId() == phoneId) {
                return cartItem;
            }
        }

        return null;
    }

    //4.signed-in user
    public static void signIn(int id) { userId = id; }
    public static int getUserId() { return userId; }
    public static void signOut() { userId = -1; }

    //5.checkout
    public boolean completeSale(int userId, List<CartItem> cartItems) {
        if (userId <= 0 || cartItems == null || cartItems.isEmpty()) {
            return false;
        }

        SalesCalculator.Totals totals = new SalesCalculator.Totals(
                SalesCalculator.calculateTotalCost(cartItems),
                SalesCalculator.calculateTotalRevenue(cartItems),
                SalesCalculator.calculateProfitLoss(cartItems)
        );

        //clear only after saving succeeds
        boolean saved = database.saveSale(userId, cartItems, LocalDate.now().toString(), totals);
        if (saved) {
            AppProcesses.clearCart();
        }
        return saved;
    }

    //6.daily results
    public DailySalesSummary getTodaySalesSummary() {
        String date = LocalDate.now().toString();
        return summarize(database.getSalesTotals(date), database.getPhoneSales(date));
    }

    public List<PhoneSalesRecord> getTodayPhoneSales() {
        return database.getPhoneSales(LocalDate.now().toString());
    }

    //count units and select the top phone
    public static DailySalesSummary summarize(SalesCalculator.Totals totals,
                                               List<PhoneSalesRecord> phoneSalesRecords) {
        int totalQuantitySold = 0;
        String mostSoldPhone = "No sales yet";
        int mostSoldQuantity = 0;

        for (PhoneSalesRecord record : phoneSalesRecords) {
            //jumlah unit terjual.
            totalQuantitySold += record.getQuantitySold();
            //seri kekalkan yang pertama.
            if (record.getQuantitySold() > mostSoldQuantity) {
                mostSoldQuantity = record.getQuantitySold();
                mostSoldPhone = record.getPhoneName();
            }
        }

        return new DailySalesSummary(totalQuantitySold, totals.getTotalCost(),
                totals.getTotalRevenue(), totals.getProfitLoss(), mostSoldPhone, mostSoldQuantity);
    }

    //7.close day
    public void closeDay() {
        database.deleteSales(LocalDate.now().toString());
        AppProcesses.clearCart();
    }

    //8.daily result data
    public static class DailySalesSummary {

        private final int totalQuantitySold;
        private final double totalCost;
        private final double totalRevenue;
        private final double profitLoss;
        private final String mostSoldPhone;
        private final int mostSoldQuantity;

        public DailySalesSummary(
                int totalQuantitySold,
                double totalCost,
                double totalRevenue,
                double profitLoss,
                String mostSoldPhone,
                int mostSoldQuantity
        ) {
            this.totalQuantitySold = totalQuantitySold;
            this.totalCost = totalCost;
            this.totalRevenue = totalRevenue;
            this.profitLoss = profitLoss;
            this.mostSoldPhone = mostSoldPhone;
            this.mostSoldQuantity = mostSoldQuantity;
        }

        public int getTotalQuantitySold() { return totalQuantitySold; }

        public double getTotalCost() { return totalCost; }

        public double getTotalRevenue() { return totalRevenue; }

        public double getProfitLoss() { return profitLoss; }

        public String getMostSoldPhone() { return mostSoldPhone; }

        public int getMostSoldQuantity() { return mostSoldQuantity; }
    }
    //phone chart data
    public static class PhoneSalesRecord {

        private final String phoneName;
        private final int quantitySold;

        public PhoneSalesRecord(String phoneName, int quantitySold) {
            this.phoneName = phoneName;
            this.quantitySold = quantitySold;
        }

        public String getPhoneName() { return phoneName; }

        public int getQuantitySold() { return quantitySold; }
    }
}
