package com.example.phonemarkettracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Locale;

// Manages the local SQLite PhoneMarketTracker db.
public class DatabasePMT extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "phonemarkettracker.db";
    private static final int DATABASE_VERSION = 4;

    private static final String DEFAULT_FULL_NAME =
            "Muhammad Amirul Harith Bin Zakaria";
    private static final String DEFAULT_EMAIL =
            "amirulharith505@gmail.com";
    private static final String DEFAULT_PASSWORD = "abcd1234";

    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_FULL_NAME = "full_name";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";

    private static final String TABLE_PHONES = "phones";
    private static final String COLUMN_PHONE_ID = "phone_id";
    private static final String COLUMN_BRAND = "brand";
    private static final String COLUMN_MODEL = "model";
    private static final String COLUMN_COST_PRICE = "cost_price";
    private static final String COLUMN_SELLING_PRICE = "selling_price";
    private static final String COLUMN_STOCK_QUANTITY = "stock_quantity";

    // ---- versions ----
    private static final int DATABASE_VERSION = 5;

    // ---- sales table ----
    private static final String TABLE_SALES = "sales";
    private static final String COLUMN_SALE_ID = "sale_id";
    private static final String COLUMN_SALE_USER_ID = "user_id";
    private static final String COLUMN_SALE_DATE = "sale_date";
    private static final String COLUMN_SALE_TOTAL_COST = "total_cost";
    private static final String COLUMN_SALE_TOTAL_REVENUE = "total_revenue";
    private static final String COLUMN_SALE_PROFIT_LOSS = "profit_loss";

    // ---- sale_items table ----
    private static final String TABLE_SALE_ITEMS = "sale_items";
    private static final String COLUMN_SALE_ITEM_ID = "sale_item_id";
    private static final String COLUMN_SALE_ITEM_SALE_ID = "sale_id";
    private static final String COLUMN_SALE_ITEM_PHONE_ID = "phone_id";
    private static final String COLUMN_SALE_ITEM_QUANTITY = "quantity";
    private static final String COLUMN_SALE_ITEM_UNIT_COST = "unit_cost";
    private static final String COLUMN_SALE_ITEM_UNIT_PRICE = "unit_price";
    // create

    public DatabasePMT(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // create
    @Override
    public void onCreate(SQLiteDatabase database) {
        createUsersTable(database);
        insertDefaultUser(database);
        createPhonesTable(database);
        insertCurrentPhoneDetails(database);
        createSalesTable(database);
        createSaleItemsTable(database);
    }

    // create
    private void createUsersTable(SQLiteDatabase database) {
        String createUsersTable =
                "CREATE TABLE " + TABLE_USERS + " (" +
                        COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_FULL_NAME + " TEXT NOT NULL, " +
                        COLUMN_EMAIL + " TEXT NOT NULL UNIQUE, " +
                        COLUMN_PASSWORD + " TEXT NOT NULL" +
                        ")";

        database.execSQL(createUsersTable);
    }

    // update
    @Override
    public void onUpgrade(
            SQLiteDatabase database,
            int oldVersion,
            int newVersion
    ) {
        if (oldVersion < 5) {
            createSalesTable(database);
            createSaleItemsTable(database);
        }
        if (oldVersion < 4) {
            database.execSQL("DROP TABLE IF EXISTS users_backup");
            database.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
            createUsersTable(database);
            insertDefaultUser(database);
        }

        if (oldVersion < 3) {
            createPhonesTable(database);
            insertCurrentPhoneDetails(database);
        }

    }

    // create
    private void insertDefaultUser(SQLiteDatabase database) {
        ContentValues userDetails = new ContentValues();
        userDetails.put(COLUMN_FULL_NAME, DEFAULT_FULL_NAME);
        userDetails.put(COLUMN_EMAIL, DEFAULT_EMAIL);
        userDetails.put(COLUMN_PASSWORD, DEFAULT_PASSWORD);

        database.insert(TABLE_USERS, null, userDetails);
    }

    // create
    private void createPhonesTable(SQLiteDatabase database) {
        String createPhonesTable =
                "CREATE TABLE IF NOT EXISTS " + TABLE_PHONES + " (" +
                        COLUMN_PHONE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_BRAND + " TEXT NOT NULL, " +
                        COLUMN_MODEL + " TEXT NOT NULL, " +
                        COLUMN_COST_PRICE + " REAL NOT NULL, " +
                        COLUMN_SELLING_PRICE + " REAL NOT NULL, " +
                        COLUMN_STOCK_QUANTITY + " INTEGER NOT NULL" +
                        ")";

        database.execSQL(createPhonesTable);
    }

    // create
    private void insertCurrentPhoneDetails(SQLiteDatabase database) {
        addPhoneIfMissing(database, "Apple", "iPhone 13 128GB", 1800.00, 2199.00, 8);
        addPhoneIfMissing(database, "Apple", "iPhone 15 128GB", 2850.00, 3299.00, 5);
        addPhoneIfMissing(database, "Samsung", "Galaxy S24 256GB", 2600.00, 3099.00, 6);
        addPhoneIfMissing(database, "Xiaomi", "Redmi Note 13", 650.00, 799.00, 10);
        addPhoneIfMissing(database, "OPPO", "Reno 11F 5G", 1050.00, 1299.00, 7);
        addPhoneIfMissing(database, "OPPO", "OPPO Reno 12", 1500.00, 1899.00, 8);
    }

    // create
    private void addPhoneIfMissing(
            SQLiteDatabase database,
            String brand,
            String model,
            double costPrice,
            double sellingPrice,
            int stockQuantity
    ) {
        Cursor cursor = database.rawQuery(
                "SELECT " + COLUMN_PHONE_ID +
                        " FROM " + TABLE_PHONES +
                        " WHERE " + COLUMN_BRAND + " = ?" +
                        " AND " + COLUMN_MODEL + " = ?",
                new String[]{brand, model}
        );

        boolean phoneExists = cursor.moveToFirst();
        cursor.close();

        if (phoneExists) {
            return;
        }

        ContentValues phoneDetails = new ContentValues();
        phoneDetails.put(COLUMN_BRAND, brand);
        phoneDetails.put(COLUMN_MODEL, model);
        phoneDetails.put(COLUMN_COST_PRICE, costPrice);
        phoneDetails.put(COLUMN_SELLING_PRICE, sellingPrice);
        phoneDetails.put(COLUMN_STOCK_QUANTITY, stockQuantity);

        database.insert(TABLE_PHONES, null, phoneDetails);
    }

    // read – get all phones
    public java.util.List<Phone> getAllPhones() {
        java.util.List<Phone> phoneList = new java.util.ArrayList<>();
        SQLiteDatabase database = getReadableDatabase();

        Cursor cursor = database.rawQuery(
                "SELECT " + COLUMN_PHONE_ID + ", " +
                        COLUMN_BRAND + ", " +
                        COLUMN_MODEL + ", " +
                        COLUMN_COST_PRICE + ", " +
                        COLUMN_SELLING_PRICE + ", " +
                        COLUMN_STOCK_QUANTITY +
                        " FROM " + TABLE_PHONES +
                        " ORDER BY " + COLUMN_BRAND + " ASC, " +
                        COLUMN_MODEL + " ASC",
                null
        );

        if (cursor.moveToFirst()) {
            do {
                phoneList.add(new Phone(
                        cursor.getInt(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getDouble(3),
                        cursor.getDouble(4),
                        cursor.getInt(5)
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return phoneList;
    }

    // read – get one phone by id
    public Phone getPhoneById(int phoneId) {
        SQLiteDatabase database = getReadableDatabase();

        Cursor cursor = database.rawQuery(
                "SELECT " + COLUMN_PHONE_ID + ", " +
                        COLUMN_BRAND + ", " +
                        COLUMN_MODEL + ", " +
                        COLUMN_COST_PRICE + ", " +
                        COLUMN_SELLING_PRICE + ", " +
                        COLUMN_STOCK_QUANTITY +
                        " FROM " + TABLE_PHONES +
                        " WHERE " + COLUMN_PHONE_ID + " = ?",
                new String[]{String.valueOf(phoneId)}
        );

        Phone phone = null;
        if (cursor.moveToFirst()) {
            phone = new Phone(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getDouble(3),
                    cursor.getDouble(4),
                    cursor.getInt(5)
            );
        }
        cursor.close();
        return phone;
    }

    // update – reduce stock after a sale
    public boolean reduceStock(int phoneId, int quantitySold) {
        SQLiteDatabase database = getWritableDatabase();

        Cursor cursor = database.rawQuery(
                "SELECT " + COLUMN_STOCK_QUANTITY +
                        " FROM " + TABLE_PHONES +
                        " WHERE " + COLUMN_PHONE_ID + " = ?",
                new String[]{String.valueOf(phoneId)}
        );

        int currentStock = 0;
        if (cursor.moveToFirst()) {
            currentStock = cursor.getInt(0);
        }
        cursor.close();

        if (quantitySold <= 0 || quantitySold > currentStock) {
            return false;
        }

        ContentValues values = new ContentValues();
        values.put(COLUMN_STOCK_QUANTITY, currentStock - quantitySold);

        int rows = database.update(
                TABLE_PHONES,
                values,
                COLUMN_PHONE_ID + " = ?",
                new String[]{String.valueOf(phoneId)}
        );

        return rows > 0;
    }
    // create
    public boolean addUser(
            String fullName,
            String emailAddress,
            String password
    ) {
        SQLiteDatabase database = getWritableDatabase();

        ContentValues userDetails = new ContentValues();
        userDetails.put(COLUMN_FULL_NAME, fullName.trim());
        userDetails.put(COLUMN_EMAIL, normalizeEmail(emailAddress));
        userDetails.put(COLUMN_PASSWORD, password);

        long newUserId = database.insert(
                TABLE_USERS,
                null,
                userDetails
        );

        return newUserId != -1;
    }

    // read
    public boolean emailExists(String emailAddress) {
        SQLiteDatabase database = getReadableDatabase();

        Cursor cursor = database.rawQuery(
                "SELECT " + COLUMN_USER_ID +
                        " FROM " + TABLE_USERS +
                        " WHERE " + COLUMN_EMAIL + " = ?",
                new String[]{normalizeEmail(emailAddress)}
        );

        boolean emailFound = cursor.moveToFirst();
        cursor.close();

        return emailFound;
    }

    // validate input
    public boolean checkUser(
            String emailAddress,
            String password
    ) {
        SQLiteDatabase database = getReadableDatabase();

        Cursor cursor = database.rawQuery(
                "SELECT " + COLUMN_USER_ID +
                        " FROM " + TABLE_USERS +
                        " WHERE " + COLUMN_EMAIL + " = ?" +
                        " AND " + COLUMN_PASSWORD + " = ?",
                new String[]{normalizeEmail(emailAddress), password}
        );

        boolean userFound = cursor.moveToFirst();
        cursor.close();

        return userFound;
    }

    // validate input
    private String normalizeEmail(String emailAddress) {
        return emailAddress.trim().toLowerCase(Locale.ROOT);
    }
}
        private void createSalesTable(SQLiteDatabase database) {
            database.execSQL(
                    "CREATE TABLE IF NOT EXISTS " + TABLE_SALES + " (" +
                            COLUMN_SALE_ID          + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            COLUMN_SALE_USER_ID     + " INTEGER NOT NULL, " +
                            COLUMN_SALE_DATE        + " TEXT NOT NULL, " +
                            COLUMN_SALE_TOTAL_COST  + " REAL NOT NULL, " +
                            COLUMN_SALE_TOTAL_REVENUE + " REAL NOT NULL, " +
                            COLUMN_SALE_PROFIT_LOSS + " REAL NOT NULL" +
                            ")");
        }

        private void createSaleItemsTable(SQLiteDatabase database) {
            database.execSQL(
                    "CREATE TABLE IF NOT EXISTS " + TABLE_SALE_ITEMS + " (" +
                            COLUMN_SALE_ITEM_ID       + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            COLUMN_SALE_ITEM_SALE_ID  + " INTEGER NOT NULL, " +
                            COLUMN_SALE_ITEM_PHONE_ID + " INTEGER NOT NULL, " +
                            COLUMN_SALE_ITEM_QUANTITY + " INTEGER NOT NULL, " +
                            COLUMN_SALE_ITEM_UNIT_COST  + " REAL NOT NULL, " +
                            COLUMN_SALE_ITEM_UNIT_PRICE + " REAL NOT NULL" +
                            ")");
        }
        /**
         * Inserts a sale, all sale_items, and reduces stock atomically.
         * Returns the new sale_id, or -1 on failure.
         */
        public long completeSale(java.util.List<CartItem> cartItems) {
            if (cartItems == null || cartItems.isEmpty()) return -1;

            SQLiteDatabase db = getWritableDatabase();
            db.beginTransaction();
            try {
                // totals
                double totalCost = 0, totalRevenue = 0;
                for (CartItem ci : cartItems) {
                    totalCost    += ci.getPhone().getCostPrice()    * ci.getQuantity();
                    totalRevenue += ci.getPhone().getSellingPrice() * ci.getQuantity();
                }
                double profitLoss = totalRevenue - totalCost;

                // 1) validate stock
                for (CartItem ci : cartItems) {
                    Cursor c = db.rawQuery(
                            "SELECT " + COLUMN_STOCK_QUANTITY +
                                    " FROM " + TABLE_PHONES +
                                    " WHERE " + COLUMN_PHONE_ID + " = ?",
                            new String[]{String.valueOf(ci.getPhone().getId())});
                    int stock = 0;
                    if (c.moveToFirst()) stock = c.getInt(0);
                    c.close();
                    if (ci.getQuantity() > stock) {
                        db.endTransaction();
                        return -1;
                    }
                }

                // 2) insert into sales
                ContentValues saleValues = new ContentValues();
                saleValues.put(COLUMN_SALE_USER_ID, 1); // default user; extend if logged-in
                saleValues.put(COLUMN_SALE_DATE, nowTimestamp());
                saleValues.put(COLUMN_SALE_TOTAL_COST, totalCost);
                saleValues.put(COLUMN_SALE_TOTAL_REVENUE, totalRevenue);
                saleValues.put(COLUMN_SALE_PROFIT_LOSS, profitLoss);

                long saleId = db.insert(TABLE_SALES, null, saleValues);
                if (saleId == -1) { db.endTransaction(); return -1; }

                // 3) insert into sale_items + reduce stock
                for (CartItem ci : cartItems) {
                    Phone p = ci.getPhone();

                    ContentValues item = new ContentValues();
                    item.put(COLUMN_SALE_ITEM_SALE_ID, saleId);
                    item.put(COLUMN_SALE_ITEM_PHONE_ID, p.getId());
                    item.put(COLUMN_SALE_ITEM_QUANTITY, ci.getQuantity());
                    item.put(COLUMN_SALE_ITEM_UNIT_COST, p.getCostPrice());
                    item.put(COLUMN_SALE_ITEM_UNIT_PRICE, p.getSellingPrice());
                    db.insert(TABLE_SALE_ITEMS, null, item);

                    db.execSQL(
                            "UPDATE " + TABLE_PHONES +
                                    " SET " + COLUMN_STOCK_QUANTITY +
                                    " = " + COLUMN_STOCK_QUANTITY + " - ?" +
                                    " WHERE " + COLUMN_PHONE_ID + " = ?",
                            new Object[]{ci.getQuantity(), p.getId()});
                }

                db.setTransactionSuccessful();
                return saleId;
            } finally {
                db.endTransaction();
            }
        }

        private String nowTimestamp() {
            return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                    java.util.Locale.US).format(new java.util.Date());
        }
        /** Daily revenue, cost, profit/loss for a given yyyy-MM-dd date. */
        public double[] getDailyTotals(String date) {
            SQLiteDatabase db = getReadableDatabase();
            Cursor c = db.rawQuery(
                    "SELECT " +
                            "IFNULL(SUM(" + COLUMN_SALE_TOTAL_COST + "),0), " +
                            "IFNULL(SUM(" + COLUMN_SALE_TOTAL_REVENUE + "),0), " +
                            "IFNULL(SUM(" + COLUMN_SALE_PROFIT_LOSS + "),0) " +
                            " FROM " + TABLE_SALES +
                            " WHERE " + COLUMN_SALE_DATE + " LIKE ?",
                    new String[]{date + "%"});
            double cost = 0, revenue = 0, profit = 0;
            if (c.moveToFirst()) {
                cost    = c.getDouble(0);
                revenue = c.getDouble(1);
                profit  = c.getDouble(2);
            }
            c.close();
            return new double[]{cost, revenue, profit};
        }

        /** Quantity sold today per phone. Key = brand + " " + model. */
        public java.util.LinkedHashMap<String, Integer> getTodayQuantitiesByPhone(String date) {
            java.util.LinkedHashMap<String, Integer> map = new java.util.LinkedHashMap<>();
            SQLiteDatabase db = getReadableDatabase();
            Cursor c = db.rawQuery(
                    "SELECT p." + COLUMN_BRAND + ", p." + COLUMN_MODEL + ", " +
                            "SUM(si." + COLUMN_SALE_ITEM_QUANTITY + ") " +
                            " FROM " + TABLE_SALE_ITEMS + " si " +
                            " JOIN " + TABLE_SALES + " s ON s." + COLUMN_SALE_ID +
                            " = si." + COLUMN_SALE_ITEM_SALE_ID +
                            " JOIN " + TABLE_PHONES + " p ON p." + COLUMN_PHONE_ID +
                            " = si." + COLUMN_SALE_ITEM_PHONE_ID +
                            " WHERE s." + COLUMN_SALE_DATE + " LIKE ?" +
                            " GROUP BY p." + COLUMN_PHONE_ID +
                            " ORDER BY SUM(si." + COLUMN_SALE_ITEM_QUANTITY + ") DESC",
                    new String[]{date + "%"});
            while (c.moveToNext()) {
                map.put(c.getString(0) + " " + c.getString(1), c.getInt(2));
            }
            c.close();
            return map;
        }

        /** Highest-selling phone today, or null. */
        public String getMostSoldPhoneToday(String date) {
            java.util.LinkedHashMap<String, Integer> map = getTodayQuantitiesByPhone(date);
            for (java.util.Map.Entry<String, Integer> e : map.entrySet()) return e.getKey();
            return null;
        }

        public int getMostSoldQuantityToday(String date) {
            java.util.LinkedHashMap<String, Integer> map = getTodayQuantitiesByPhone(date);
            for (java.util.Map.Entry<String, Integer> e : map.entrySet()) return e.getValue();
            return 0;
        }
        /**
         * Clears only this session's view of "today": deletes sales + sale_items
         * made on or before the given date. Users and phones are untouched.
         * Stock is NOT restored — it remains as actually sold.
         */
        public void resetDailySales(String dateUpToInclusive) {
            SQLiteDatabase db = getWritableDatabase();
            db.beginTransaction();
            try {
                db.delete(TABLE_SALE_ITEMS,
                        COLUMN_SALE_ITEM_SALE_ID + " IN (" +
                                "SELECT " + COLUMN_SALE_ID + " FROM " + TABLE_SALES +
                                " WHERE " + COLUMN_SALE_DATE + " <= ?)",
                        new String[]{dateUpToInclusive + " 23:59:59"});
                db.delete(TABLE_SALES,
                        COLUMN_SALE_DATE + " <= ?",
                        new String[]{dateUpToInclusive + " 23:59:59"});
                db.setTransactionSuccessful();
            } finally {
                db.endTransaction();
            }
        }
        