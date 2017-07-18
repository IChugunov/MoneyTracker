package com.ivanchug.moneytracker.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Иван on 17.07.2017.
 */

public class MoneyTrackerDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "moneytracker";
    private static final int DB_VERSION = 1;

    public MoneyTrackerDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE ITEMS ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "NAME TEXT, "
                + "PRICE INTEGER, "
                + "TYPE TEXT);");

        db.execSQL("CREATE TABLE BALANCE ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "TOTALEXPENSES INTEGER, "
                + "TOTALINCOME INTEGER);");

        ContentValues values = new ContentValues();
        values.put("TOTALEXPENSES", 0);
        values.put("TOTALINCOME", 0);
        db.insert("BALANCE", null, values);



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public List<Item> getItems(SQLiteDatabase db, String type) {

        try (Cursor cursor = db.query("ITEMS", new String[]{"NAME", "PRICE"}, "TYPE = ?", new String[]{type}, null, null, null)) {
            List<Item> result = new ArrayList<>();
            if (cursor.moveToLast()) {
                result.add(new Item(cursor.getString(0), cursor.getInt(1), type));
                while (cursor.moveToPrevious())
                    result.add(new Item(cursor.getString(0), cursor.getInt(1), type));
            }

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            db.close();
        }

    }

    public Item addItem(SQLiteDatabase db, Item item) {
        try {
            ContentValues values = new ContentValues();
            values.put("NAME", item.name);
            values.put("PRICE", item.price);
            values.put("TYPE", item.type);
            db.insert("ITEMS", null, values);
            addToBalance(db, item.type, item.price);
            return item;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            db.close();
        }
    }

    public Item removeItem(SQLiteDatabase db, Item item) {
        try {
            db.delete("ITEMS", "NAME = ? AND PRICE = ? AND TYPE = ?", new String[]{item.name, Integer.toString(item.price), item.type});
            removeFromBalance(db, item.type, item.price);
            return item;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            db.close();
        }
    }

    public BalanceResult getBalance(SQLiteDatabase db) {
        try (Cursor cursor = db.query("BALANCE", new String[]{"TOTALEXPENSES", "TOTALINCOME"}, null, null, null, null, null)) {
            long totalExpenses = 0;
            long totalIncome = 0;
            if (cursor.moveToFirst()) {
                totalExpenses = cursor.getInt(0);
                totalIncome = cursor.getInt(1);
            }
            return new BalanceResult(totalExpenses, totalIncome);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            db.close();
        }
    }

    private void addToBalance(SQLiteDatabase db, String type, int price) {
        try (Cursor cursor = db.query("BALANCE", new String[]{"TOTALEXPENSES", "TOTALINCOME"}, null, null, null, null, null)) {
            String columnToUpdate = null;
            int colomnId = 0;
            if (type.equals(Item.TYPE_EXPENSE))
                columnToUpdate = "TOTALEXPENSES";
            else {
                columnToUpdate = "TOTALINCOME";
                colomnId = 1;
            }
            cursor.moveToFirst();

            int resultAmount = cursor.getInt(colomnId) + price;
            ContentValues values = new ContentValues();
            values.put(columnToUpdate, resultAmount);
            db.update("BALANCE", values, null, null);

        }
    }

    private void removeFromBalance(SQLiteDatabase db, String type, int price) {
        try (Cursor cursor = db.query("BALANCE", new String[]{"TOTALEXPENSES", "TOTALINCOME"}, null, null, null, null, null)) {
            String columnToUpdate = null;
            int colomnId = 0;
            if (type.equals(Item.TYPE_EXPENSE))
                columnToUpdate = "TOTALEXPENSES";
            else {
                columnToUpdate = "TOTALINCOME";
                colomnId = 1;
            }
            cursor.moveToFirst();

            int resultAmount = cursor.getInt(colomnId) - price;
            ContentValues values = new ContentValues();
            values.put(columnToUpdate, resultAmount);
            db.update("BALANCE", values, null, null);

        }
    }
}
