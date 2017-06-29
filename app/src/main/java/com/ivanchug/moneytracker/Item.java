package com.ivanchug.moneytracker;

import java.io.Serializable;

/**
 * Created by Иван on 24.06.2017.
 */

public class Item implements Serializable {
    public static final String TYPE_EXPENSE = "expense";
    public static final String TYPE_INCOME = "income";

    public String name, type;
    public int id = -1, price;

    public Item(String name, int price, String type) {
        this.name = name;
        this.price = price;
        this.type = type;
    }
}
