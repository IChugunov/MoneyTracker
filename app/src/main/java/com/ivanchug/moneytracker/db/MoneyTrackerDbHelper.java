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


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public List<Item> getItems(SQLiteDatabase db, String type) {
        List<Item> result = new ArrayList<>();
        Cursor cursor = db.query("ITEMS", new String[]{"NAME", "PRICE"}, "TYPE = ?", new String[]{type}, null, null, null);
        if (cursor.moveToLast()) {
            result.add(new Item(cursor.getString(0), cursor.getInt(1), type));
            while (cursor.moveToPrevious())
                result.add(new Item(cursor.getString(0), cursor.getInt(1), type));
        }
        cursor.close();
        db.close();
        return result;
    }

    public Item addItem(SQLiteDatabase db, Item item) {
        ContentValues values = new ContentValues();
        values.put("NAME", item.name);
        values.put("PRICE", item.price);
        values.put("TYPE", item.type);
        db.insert("ITEMS", null, values);
        db.close();
        return item;
    }

    public Item removeItem(SQLiteDatabase db, Item item) {
        db.delete("ITEMS", "NAME = ? AND PRICE = ? AND TYPE = ?", new String[]{item.name, Integer.toString(item.price), item.type});
        db.close();
        return item;
    }
}
