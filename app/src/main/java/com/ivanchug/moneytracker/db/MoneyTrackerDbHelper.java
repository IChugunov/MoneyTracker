package com.ivanchug.moneytracker.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ivanchug.moneytracker.items.Item;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Иван on 17.07.2017.
 */

public class MoneyTrackerDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "moneytracker";
    private static final int DB_VERSION = 1;
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


        db.execSQL("CREATE TABLE CATEGORIES (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "NAME TEXT, " +
                "TYPE TEXT);");




        ContentValues categoriesValues = new ContentValues();
        categoriesValues.put(NAME, "Без категории расходы");
        categoriesValues.put(TYPE, Item.TYPE_EXPENSE);
        db.insert(CATEGORIES, null, categoriesValues);

        ContentValues categoriesValues1 = new ContentValues();
        categoriesValues1.put(NAME, "Без категории доходы");
        categoriesValues1.put(TYPE, Item.TYPE_INCOME);
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

    public List<String> getCategories(SQLiteDatabase db, String type) {

        try (Cursor cursor = db.query(CATEGORIES, new String[]{NAME}, "TYPE = ?", new String[]{type}, null, null, null)) {
            List<String> result = new ArrayList<>();
            if (cursor.moveToLast()) {
                result.add(cursor.getString(0));
                while (cursor.moveToPrevious())
                    result.add(cursor.getString(0));
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
            return item;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            db.close();
        }
    }

    public String addCategory(SQLiteDatabase db, String category, String type) {
        try {
            ContentValues values = new ContentValues();
            values.put(NAME, category);
            values.put(TYPE, type);
            db.insert(CATEGORIES, null, values);
            return category;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            db.close();
        }
    }
}
