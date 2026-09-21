package com.example.phonemarkettracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.Locale;

//owns v1 account and phone setup; sales integration remains separate.
public class DatabasePMT extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "phonemarkettracker.db";
    private static final int DATABASE_VERSION = 4;

    private static final String[][] DEFAULT_PHONE_DETAILS = {
            {"Apple", "iPhone 13 128GB", "1800.00", "2199.00", "8"},
            {"Apple", "iPhone 15 128GB", "2850.00", "3299.00", "5"},
            {"Samsung", "Galaxy S24 256GB", "2600.00", "3099.00", "6"},
            {"Xiaomi", "Redmi Note 13", "650.00", "799.00", "10"},
            {"OPPO", "Reno 11F 5G", "1050.00", "1299.00", "7"},
            {"OPPO", "Reno 12", "1500.00", "1899.00", "8"}
    };

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

    //1.database setup and upgrades
    public DatabasePMT(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        createUsersTable(database);
        createPhonesTable(database);
        insertCurrentPhoneDetails(database);
    }

    @Override
    public void onUpgrade(
            SQLiteDatabase database,
            int oldVersion,
            int newVersion
    ) {
        if (oldVersion < 3) {
            createPhonesTable(database);
            insertCurrentPhoneDetails(database);
        }

        if (oldVersion < 4) {
            database.execSQL("DROP TABLE IF EXISTS users_backup");
            database.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
            createUsersTable(database);
        }
    }

    //2.create tables and starting phones
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

    private void insertCurrentPhoneDetails(SQLiteDatabase database) {
        for (String[] phoneDetails : DEFAULT_PHONE_DETAILS) {
            addPhoneIfMissing(
                    database,
                    phoneDetails[0],
                    phoneDetails[1],
                    Double.parseDouble(phoneDetails[2]),
                    Double.parseDouble(phoneDetails[3]),
                    Integer.parseInt(phoneDetails[4])
            );
        }
    }

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

    //3.accounts: register and verify
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

        long newUserId = database.insert(TABLE_USERS, null, userDetails);
        return newUserId != -1;
    }

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

    public int getUserId(String emailAddress, String password) {
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery(
                "SELECT " + COLUMN_USER_ID +
                        " FROM " + TABLE_USERS +
                        " WHERE " + COLUMN_EMAIL + " = ?" +
                        " AND " + COLUMN_PASSWORD + " = ?",
                new String[]{normalizeEmail(emailAddress), password}
        );

        int userId = -1;

        if (cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID));
        }

        cursor.close();
        return userId;
    }

    //keep the v1 login api available for existing callers.
    public boolean checkUser(String emailAddress, String password) {
        return getUserId(emailAddress, password) != -1;
    }

    private String normalizeEmail(String emailAddress) {
        return emailAddress.trim().toLowerCase(Locale.ROOT);
    }
}
