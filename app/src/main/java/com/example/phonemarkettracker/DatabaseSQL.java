package com.example.phonemarkettracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/** Local SQLite backend shared with the other two team pages later. */
public class DatabaseSQL extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "phonemarkettracker.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseSQL(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users (" +
                "user_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT NOT NULL UNIQUE, " +
                "password_hash TEXT NOT NULL)");

        db.execSQL("CREATE TABLE phones (" +
                "phone_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "brand TEXT NOT NULL, " +
                "model TEXT NOT NULL, " +
                "cost_price REAL NOT NULL CHECK(cost_price >= 0), " +
                "selling_price REAL NOT NULL CHECK(selling_price >= 0), " +
                "stock_quantity INTEGER NOT NULL CHECK(stock_quantity >= 0))");

        ContentValues owner = new ContentValues();
        owner.put("username", "admin");
        owner.put("password_hash", hashPassword("admin123"));
        db.insert("users", null, owner);

        // Sample data lets the Cart page run before the team's Product page is joined.
        insertSample(db, "Apple", "iPhone 13 128GB", 1800.00, 2199.00, 8);
        insertSample(db, "Apple", "iPhone 15 128GB", 2850.00, 3299.00, 5);
        insertSample(db, "Samsung", "Galaxy S24 256GB", 2600.00, 3099.00, 6);
        insertSample(db, "Xiaomi", "Redmi Note 13", 650.00, 799.00, 10);
        insertSample(db, "OPPO", "Reno 11F 5G", 1050.00, 1299.00, 7);
    }

    private void insertSample(SQLiteDatabase db, String brand, String model,
                              double cost, double selling, int stock) {
        ContentValues values = new ContentValues();
        values.put("brand", brand);
        values.put("model", model);
        values.put("cost_price", cost);
        values.put("selling_price", selling);
        values.put("stock_quantity", stock);
        db.insert("phones", null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS phones");
        db.execSQL("DROP TABLE IF EXISTS users");
        onCreate(db);
    }

    public boolean authenticate(String username, String password) {
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor cursor = db.query(
                "users",
                new String[]{"user_id"},
                "username = ? AND password_hash = ?",
                new String[]{username.trim(), hashPassword(password)},
                null, null, null)) {
            return cursor.moveToFirst();
        }
    }

    public boolean usernameExists(String username) {
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor cursor = db.query(
                "users",
                new String[]{"user_id"},
                "LOWER(username) = LOWER(?)",
                new String[]{username.trim()},
                null, null, null)) {
            return cursor.moveToFirst();
        }
    }

    public boolean registerUser(String username, String password) {
        ContentValues user = new ContentValues();
        user.put("username", username.trim());
        user.put("password_hash", hashPassword(password));
        return getWritableDatabase().insert("users", null, user) != -1;
    }

    /**
     * Columns: 0 ID, 1 brand, 2 model, 3 cost price,
     * 4 selling price, 5 available stock.
     */
    public Object[][] getAvailablePhones() {
        SQLiteDatabase db = getReadableDatabase();
        try (Cursor cursor = db.query(
                "phones",
                new String[]{"phone_id", "brand", "model", "cost_price",
                        "selling_price", "stock_quantity"},
                "stock_quantity > 0",
                null, null, null,
                "brand ASC, model ASC")) {

            Object[][] phones = new Object[cursor.getCount()][6];
            int row = 0;
            while (cursor.moveToNext()) {
                phones[row][0] = cursor.getInt(0);
                phones[row][1] = cursor.getString(1);
                phones[row][2] = cursor.getString(2);
                phones[row][3] = cursor.getDouble(3);
                phones[row][4] = cursor.getDouble(4);
                phones[row][5] = cursor.getInt(5);
                row++;
            }
            return phones;
        }
    }

    /** Updates every stock row as one SQLite transaction. */
    public void checkout(Object[][] cartData, int cartCount) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            for (int i = 0; i < cartCount; i++) {
                int phoneId = (int) cartData[i][0];
                int quantity = (int) cartData[i][5];
                int stock;

                try (Cursor cursor = db.query(
                        "phones",
                        new String[]{"stock_quantity"},
                        "phone_id = ?",
                        new String[]{String.valueOf(phoneId)},
                        null, null, null)) {
                    if (!cursor.moveToFirst()) {
                        throw new IllegalStateException("A selected phone no longer exists.");
                    }
                    stock = cursor.getInt(0);
                }

                if (quantity > stock) {
                    throw new IllegalStateException(
                            cartData[i][1] + " " + cartData[i][2] +
                                    " only has " + stock + " unit(s) left.");
                }

                ContentValues update = new ContentValues();
                update.put("stock_quantity", stock - quantity);
                int changed = db.update(
                        "phones", update,
                        "phone_id = ? AND stock_quantity = ?",
                        new String[]{String.valueOf(phoneId), String.valueOf(stock)});

                if (changed != 1) {
                    throw new IllegalStateException("Stock changed. Please refresh and try again.");
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    private static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder result = new StringBuilder();
            for (byte value : hash) {
                result.append(String.format("%02x", value & 0xff));
            }
            return result.toString();
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to protect password.", exception);
        }
    }
}
