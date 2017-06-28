package com.ivanchug.moneytracker;

/**
 * Created by Иван on 24.06.2017.
 */

public class Item {
    public static final String TYPE_EXPENSE = "expense";
    public static final String TYPE_INCOME = "income";

    final String name;
    final int price;

    public Item(String name, int price) {
        this.name = name;
        this.price = price;
    }
}
