package com.ivanchug.moneytracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ivanchug.moneytracker.api.BalanceResult;
import com.ivanchug.moneytracker.api.Item;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Иван on 17.07.2017.
 */

public class MoneyTrackerDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "moneytracker";
    private static final int DB_VERSION = 1;
    private static final String BALANCE = "BALANCE";
    private static final String TOTAL_EXPENSES = "TOTAL_EXPENSES";
    private static final String TOTAL_INCOME = "TOTAL_INCOME";
    private static final String ITEMS = "ITEMS";
    private static final String ID = "ID";
    private static final String TYPE = "TYPE";
    private static final String PRICE = "PRICE";
    private static final String NAME = "NAME";
    private static final String DATE = "DATE";
    private static final String CATEGORIES = "CATEGORIES";

    public MoneyTrackerDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE ITEMS ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "NAME TEXT, "
                + "PRICE INTEGER, "
                + "TYPE TEXT, " +
                "ID INTEGER, " +
                "DATE INTEGER, " +
                "CATEGORY TEXT);");

        db.execSQL("CREATE TABLE BALANCE ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "TOTAL_EXPENSES INTEGER, "
                + "TOTAL_INCOME INTEGER);");

        db.execSQL("CREATE TABLE CATEGORIES (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "NAME TEXT, " +
                "TYPE TEXT);");


        ContentValues values = new ContentValues();
        values.put(TOTAL_EXPENSES, 0);
        values.put(TOTAL_INCOME, 0);
        db.insert(BALANCE, null, values);

        ContentValues categoriesValues = new ContentValues();
        values.put(NAME, "Без категории");
        values.put(TYPE, Item.TYPE_EXPENSE);
        db.insert(CATEGORIES, null, categoriesValues);

        ContentValues categoriesValues1 = new ContentValues();
        values.put(NAME, "Без категории");
        values.put(TYPE, Item.TYPE_INCOME);
        db.insert(CATEGORIES, null, categoriesValues1);



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public List<Item> getItems(SQLiteDatabase db, String type) {

        try (Cursor cursor = db.query(ITEMS, new String[]{NAME, PRICE, ID, DATE}, "TYPE = ?", new String[]{type}, null, null, null)) {
            List<Item> result = new ArrayList<>();
            if (cursor.moveToLast()) {
                result.add(new Item(cursor.getString(0), cursor.getInt(1), type, cursor.getLong(2), new Date(cursor.getLong(3))));
                Item.setNextId(cursor.getLong(2));
                while (cursor.moveToPrevious())
                    result.add(new Item(cursor.getString(0), cursor.getInt(1), type, cursor.getLong(2), new Date(cursor.getLong(3))));
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
            values.put(NAME, item.getName());
            values.put(PRICE, item.getPrice());
            values.put(TYPE, item.getType());
            values.put(ID, item.getId());
            values.put(DATE, item.getDate().getTime());
            db.insert(ITEMS, null, values);
            addToBalance(db, item.getType(), item.getPrice());
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
            db.delete(ITEMS, "NAME = ? AND PRICE = ? AND TYPE = ? AND ID = ?", new String[]{item.getName(), Integer.toString(item.getPrice()), item.getType(), Long.toString(item.getId())});
            removeFromBalance(db, item.getType(), item.getPrice());
            return item;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            db.close();
        }
    }

    public BalanceResult getBalance(SQLiteDatabase db) {
        try (Cursor cursor = db.query(BALANCE, new String[]{TOTAL_EXPENSES, TOTAL_INCOME}, null, null, null, null, null)) {
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
        try (Cursor cursor = db.query(BALANCE, new String[]{TOTAL_EXPENSES, TOTAL_INCOME}, null, null, null, null, null)) {
            String columnToUpdate = null;
            int columnId = 0;
            if (type.equals(Item.TYPE_EXPENSE))
                columnToUpdate = TOTAL_EXPENSES;
            else {
                columnToUpdate = TOTAL_INCOME;
                columnId = 1;
            }
            cursor.moveToFirst();

            int resultAmount = cursor.getInt(columnId) + price;
            ContentValues values = new ContentValues();
            values.put(columnToUpdate, resultAmount);
            db.update(BALANCE, values, null, null);

        }
    }

    private void removeFromBalance(SQLiteDatabase db, String type, int price) {
        try (Cursor cursor = db.query(BALANCE, new String[]{TOTAL_EXPENSES, TOTAL_INCOME}, null, null, null, null, null)) {
            String columnToUpdate = null;
            int columnId = 0;
            if (type.equals(Item.TYPE_EXPENSE))
                columnToUpdate = TOTAL_EXPENSES;
            else {
                columnToUpdate = TOTAL_INCOME;
                columnId = 1;
            }
            cursor.moveToFirst();

            int resultAmount = cursor.getInt(columnId) - price;
            ContentValues values = new ContentValues();
            values.put(columnToUpdate, resultAmount);
            db.update(BALANCE, values, null, null);

        }
    }
}
