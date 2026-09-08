package com.example.phonemarkettracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Manages users, phones, completed sales, and daily sales results in one SQLite database. */
public class DatabasePMT extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "phonemarkettracker.db";
    private static final int DATABASE_VERSION = 5;

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

    private static final String TABLE_SALES = "sales";
    private static final String COLUMN_SALE_ID = "sale_id";
    private static final String COLUMN_SALE_DATE = "sale_date";
    private static final String COLUMN_TOTAL_COST = "total_cost";
    private static final String COLUMN_TOTAL_REVENUE = "total_revenue";
    private static final String COLUMN_PROFIT_LOSS = "profit_loss";

    private static final String TABLE_SALE_ITEMS = "sale_items";
    private static final String COLUMN_SALE_ITEM_ID = "sale_item_id";
    private static final String COLUMN_QUANTITY = "quantity";
    private static final String COLUMN_UNIT_COST = "unit_cost";
    private static final String COLUMN_UNIT_PRICE = "unit_price";

    // create
    public DatabasePMT(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // create
    @Override
    public void onConfigure(SQLiteDatabase database) {
        super.onConfigure(database);
        database.setForeignKeyConstraintsEnabled(true);
    }

    // create
    @Override
    public void onCreate(SQLiteDatabase database) {
        createUsersTable(database);
        createPhonesTable(database);
        insertCurrentPhoneDetails(database);
        createSalesTables(database);
    }

    // update
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

        if (oldVersion < 5) {
            createSalesTables(database);
        }
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
    private void createSalesTables(SQLiteDatabase database) {
        String createSalesTable =
                "CREATE TABLE IF NOT EXISTS " + TABLE_SALES + " (" +
                        COLUMN_SALE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_USER_ID + " INTEGER NOT NULL, " +
                        COLUMN_SALE_DATE + " TEXT NOT NULL, " +
                        COLUMN_TOTAL_COST + " REAL NOT NULL, " +
                        COLUMN_TOTAL_REVENUE + " REAL NOT NULL, " +
                        COLUMN_PROFIT_LOSS + " REAL NOT NULL, " +
                        "FOREIGN KEY (" + COLUMN_USER_ID + ") REFERENCES " +
                        TABLE_USERS + "(" + COLUMN_USER_ID + ")" +
                        ")";

        String createSaleItemsTable =
                "CREATE TABLE IF NOT EXISTS " + TABLE_SALE_ITEMS + " (" +
                        COLUMN_SALE_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COLUMN_SALE_ID + " INTEGER NOT NULL, " +
                        COLUMN_PHONE_ID + " INTEGER NOT NULL, " +
                        COLUMN_QUANTITY + " INTEGER NOT NULL, " +
                        COLUMN_UNIT_COST + " REAL NOT NULL, " +
                        COLUMN_UNIT_PRICE + " REAL NOT NULL, " +
                        "FOREIGN KEY (" + COLUMN_SALE_ID + ") REFERENCES " +
                        TABLE_SALES + "(" + COLUMN_SALE_ID + ") ON DELETE CASCADE, " +
                        "FOREIGN KEY (" + COLUMN_PHONE_ID + ") REFERENCES " +
                        TABLE_PHONES + "(" + COLUMN_PHONE_ID + ")" +
                        ")";

        database.execSQL(createSalesTable);
        database.execSQL(createSaleItemsTable);
    }

    // create
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

        long newUserId = database.insert(TABLE_USERS, null, userDetails);
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
    public boolean checkUser(String emailAddress, String password) {
        return getUserId(emailAddress, password) != -1;
    }

    // read
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

    // read
    public List<Phone> getAllPhones() {
        List<Phone> phones = new ArrayList<>();
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.query(
                TABLE_PHONES,
                null,
                null,
                null,
                null,
                null,
                COLUMN_BRAND + ", " + COLUMN_MODEL
        );

        while (cursor.moveToNext()) {
            phones.add(readPhone(cursor));
        }

        cursor.close();
        return phones;
    }

    // create
    public boolean completeSale(int userId, List<CartItem> cartItems) {
        if (userId <= 0 || cartItems == null || cartItems.isEmpty()) {
            return false;
        }

        SQLiteDatabase database = getWritableDatabase();
        database.beginTransaction();

        try {
            validateAvailableStock(database, cartItems);

            ContentValues saleDetails = new ContentValues();
            saleDetails.put(COLUMN_USER_ID, userId);
            saleDetails.put(COLUMN_SALE_DATE, getTodayDate());
            saleDetails.put(COLUMN_TOTAL_COST, SalesCalculator.calculateTotalCost(cartItems));
            saleDetails.put(COLUMN_TOTAL_REVENUE, SalesCalculator.calculateTotalRevenue(cartItems));
            saleDetails.put(COLUMN_PROFIT_LOSS, SalesCalculator.calculateProfitLoss(cartItems));

            long saleId = database.insertOrThrow(TABLE_SALES, null, saleDetails);

            for (CartItem cartItem : cartItems) {
                insertSaleItem(database, saleId, cartItem);
                reducePhoneStock(database, cartItem);
            }

            database.setTransactionSuccessful();
            return true;
        } catch (RuntimeException exception) {
            return false;
        } finally {
            database.endTransaction();
        }
    }

    // read
    public DailySalesSummary getTodaySalesSummary() {
        SQLiteDatabase database = getReadableDatabase();
        double totalCost = 0.0;
        double totalRevenue = 0.0;
        double profitLoss = 0.0;

        Cursor totalsCursor = database.rawQuery(
                "SELECT COALESCE(SUM(" + COLUMN_TOTAL_COST + "), 0), " +
                        "COALESCE(SUM(" + COLUMN_TOTAL_REVENUE + "), 0), " +
                        "COALESCE(SUM(" + COLUMN_PROFIT_LOSS + "), 0) " +
                        "FROM " + TABLE_SALES +
                        " WHERE " + COLUMN_SALE_DATE + " = ?",
                new String[]{getTodayDate()}
        );

        if (totalsCursor.moveToFirst()) {
            totalCost = totalsCursor.getDouble(0);
            totalRevenue = totalsCursor.getDouble(1);
            profitLoss = totalsCursor.getDouble(2);
        }

        totalsCursor.close();

        List<PhoneSalesRecord> phoneSalesRecords = getTodayPhoneSales();
        int totalQuantitySold = 0;
        String mostSoldPhone = "No sales yet";
        int mostSoldQuantity = 0;

        for (PhoneSalesRecord phoneSalesRecord : phoneSalesRecords) {
            totalQuantitySold += phoneSalesRecord.getQuantitySold();

            if (phoneSalesRecord.getQuantitySold() > mostSoldQuantity) {
                mostSoldQuantity = phoneSalesRecord.getQuantitySold();
                mostSoldPhone = phoneSalesRecord.getPhoneName();
            }
        }

        return new DailySalesSummary(
                totalQuantitySold,
                totalCost,
                totalRevenue,
                profitLoss,
                mostSoldPhone,
                mostSoldQuantity
        );
    }

    // read
    public List<PhoneSalesRecord> getTodayPhoneSales() {
        List<PhoneSalesRecord> phoneSalesRecords = new ArrayList<>();
        SQLiteDatabase database = getReadableDatabase();
        String query =
                "SELECT " + TABLE_PHONES + "." + COLUMN_BRAND + ", " +
                        TABLE_PHONES + "." + COLUMN_MODEL + ", " +
                        "COALESCE(SUM(CASE WHEN " + TABLE_SALES + "." + COLUMN_SALE_DATE +
                        " = ? THEN " + TABLE_SALE_ITEMS + "." + COLUMN_QUANTITY +
                        " ELSE 0 END), 0) AS sold_quantity " +
                        "FROM " + TABLE_PHONES + " " +
                        "LEFT JOIN " + TABLE_SALE_ITEMS + " ON " +
                        TABLE_SALE_ITEMS + "." + COLUMN_PHONE_ID + " = " +
                        TABLE_PHONES + "." + COLUMN_PHONE_ID + " " +
                        "LEFT JOIN " + TABLE_SALES + " ON " +
                        TABLE_SALES + "." + COLUMN_SALE_ID + " = " +
                        TABLE_SALE_ITEMS + "." + COLUMN_SALE_ID + " " +
                        "GROUP BY " + TABLE_PHONES + "." + COLUMN_PHONE_ID + " " +
                        "ORDER BY sold_quantity DESC, " + TABLE_PHONES + "." + COLUMN_MODEL;

        Cursor cursor = database.rawQuery(query, new String[]{getTodayDate()});

        while (cursor.moveToNext()) {
            String phoneName = cursor.getString(0) + " " + cursor.getString(1);
            int quantitySold = cursor.getInt(2);
            phoneSalesRecords.add(new PhoneSalesRecord(phoneName, quantitySold));
        }

        cursor.close();
        return phoneSalesRecords;
    }

    // delete
    public void resetTodaySales() {
        SQLiteDatabase database = getWritableDatabase();
        database.delete(
                TABLE_SALES,
                COLUMN_SALE_DATE + " = ?",
                new String[]{getTodayDate()}
        );
    }

    // validate input
    private void validateAvailableStock(
            SQLiteDatabase database,
            List<CartItem> cartItems
    ) {
        for (CartItem cartItem : cartItems) {
            Cursor cursor = database.rawQuery(
                    "SELECT " + COLUMN_STOCK_QUANTITY +
                            " FROM " + TABLE_PHONES +
                            " WHERE " + COLUMN_PHONE_ID + " = ?",
                    new String[]{String.valueOf(cartItem.getPhone().getPhoneId())}
            );

            boolean phoneFound = cursor.moveToFirst();
            int availableStock = phoneFound ? cursor.getInt(0) : 0;
            cursor.close();

            if (!phoneFound || availableStock < cartItem.getQuantity()) {
                throw new IllegalStateException("Phone stock is no longer available");
            }
        }
    }

    // create
    private void insertSaleItem(
            SQLiteDatabase database,
            long saleId,
            CartItem cartItem
    ) {
        Phone phone = cartItem.getPhone();
        ContentValues itemDetails = new ContentValues();
        itemDetails.put(COLUMN_SALE_ID, saleId);
        itemDetails.put(COLUMN_PHONE_ID, phone.getPhoneId());
        itemDetails.put(COLUMN_QUANTITY, cartItem.getQuantity());
        itemDetails.put(COLUMN_UNIT_COST, phone.getCostPrice());
        itemDetails.put(COLUMN_UNIT_PRICE, phone.getSellingPrice());
        database.insertOrThrow(TABLE_SALE_ITEMS, null, itemDetails);
    }

    // update
    private void reducePhoneStock(SQLiteDatabase database, CartItem cartItem) {
        SQLiteStatement stockUpdate = database.compileStatement(
                "UPDATE " + TABLE_PHONES +
                        " SET " + COLUMN_STOCK_QUANTITY + " = " +
                        COLUMN_STOCK_QUANTITY + " - ?" +
                        " WHERE " + COLUMN_PHONE_ID + " = ?" +
                        " AND " + COLUMN_STOCK_QUANTITY + " >= ?"
        );
        stockUpdate.bindLong(1, cartItem.getQuantity());
        stockUpdate.bindLong(2, cartItem.getPhone().getPhoneId());
        stockUpdate.bindLong(3, cartItem.getQuantity());
        int updatedRows = stockUpdate.executeUpdateDelete();

        if (updatedRows != 1) {
            throw new IllegalStateException("Phone stock could not be updated");
        }
    }

    // read
    private Phone readPhone(Cursor cursor) {
        return new Phone(
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PHONE_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_BRAND)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MODEL)),
                cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_COST_PRICE)),
                cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_SELLING_PRICE)),
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STOCK_QUANTITY))
        );
    }

    // read
    private String getTodayDate() {
        return LocalDate.now().toString();
    }

    // validate input
    private String normalizeEmail(String emailAddress) {
        return emailAddress.trim().toLowerCase(Locale.ROOT);
    }
}
