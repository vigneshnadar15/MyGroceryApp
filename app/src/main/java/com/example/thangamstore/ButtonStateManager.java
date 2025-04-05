package com.example.thangamstore;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class ButtonStateManager extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "cart.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "cart";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_PRICE = "price";
    private static final String COLUMN_IMAGE_ID = "image_id";
    private static final String COLUMN_STATE = "state";
    public static final String COLUMN_QUANTITY = "quantity";

    public ButtonStateManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY," +
                COLUMN_NAME + " TEXT," +
                COLUMN_PRICE + " TEXT," +
                COLUMN_IMAGE_ID + " INTEGER," +
                COLUMN_STATE + " INTEGER," +
                COLUMN_QUANTITY + " INTEGER DEFAULT 1)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void updateCartState(int productId, String productName, String productPrice, int imageResourceId, boolean isAdded) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, productId);
        values.put(COLUMN_NAME, productName);
        values.put(COLUMN_PRICE, productPrice);
        values.put(COLUMN_IMAGE_ID, imageResourceId);
        values.put(COLUMN_STATE, isAdded ? 1 : 0);
        values.put(COLUMN_QUANTITY, 1);

        if (itemExists(productId)) {
            db.update(TABLE_NAME, values, COLUMN_ID + "=?", new String[]{String.valueOf(productId)});
        } else {
            db.insert(TABLE_NAME, null, values);
        }
        db.close();
    }

    public boolean itemExists(int productId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, COLUMN_ID + "=?", new String[]{String.valueOf(productId)}, null, null, null);
        boolean exists = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) cursor.close();
        return exists;
    }

    public void updateQuantity(int productId, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_QUANTITY, quantity);

        if (itemExists(productId)) {
            db.update(TABLE_NAME, contentValues, COLUMN_ID + " = ?", new String[]{String.valueOf(productId)});
        } else {
            contentValues.put(COLUMN_ID, productId);
            contentValues.put(COLUMN_STATE, 1); // Default to added state
            db.insert(TABLE_NAME, null, contentValues);
        }
        db.close();
    }

    public void removeItem(int productId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{String.valueOf(productId)});
        db.close();
    }

    public List<GroceryItem> getCartItems() {
        List<GroceryItem> cartItems = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, null, COLUMN_STATE + " = ?", new String[]{"1"}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int productId = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
                @SuppressLint("Range") String productName = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                @SuppressLint("Range") String productPrice = cursor.getString(cursor.getColumnIndex(COLUMN_PRICE));
                @SuppressLint("Range") int imageResourceId = cursor.getInt(cursor.getColumnIndex(COLUMN_IMAGE_ID));
                @SuppressLint("Range") int quantity = cursor.getInt(cursor.getColumnIndex(COLUMN_QUANTITY));
                @SuppressLint("Range") int stock = cursor.getInt(cursor.getColumnIndex(COLUMN_QUANTITY));

                GroceryItem item = new GroceryItem(productName, productPrice, imageResourceId, productId, quantity, stock);
                item.setQuantity(quantity);
                cartItems.add(item);
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
        db.close();
        return cartItems;
    }
    public Integer getCartState(int productId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_STATE}, COLUMN_ID + " = ?", new String[]{String.valueOf(productId)}, null, null, null);

        Integer state = null;
        if (cursor != null && cursor.moveToFirst()) {
            @SuppressLint("Range") int retrievedState = cursor.getInt(cursor.getColumnIndex(COLUMN_STATE));
            state = retrievedState;
            cursor.close();
        }
        db.close();
        return state;
    }

}
