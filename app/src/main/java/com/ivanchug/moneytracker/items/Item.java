package com.ivanchug.moneytracker.items;

import android.app.Activity;

import com.ivanchug.moneytracker.LsApp;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Иван on 24.06.2017.
 */

public class Item extends AbstractItem implements Serializable {
    public static final String TYPE_EXPENSE = "expense";
    public static final String TYPE_INCOME = "income";


    private static long nextId = 0;

    private String name;
    private String type;
    private long id;
    private int price;
    private String category;
    private Date date;


    public Item(String name, int price, String type, Activity activity) {
        nextId = ((LsApp) activity.getApplication()).getItemNextId();
        this.name = name;
        this.price = price;
        this.type = type;
        id = nextId++;
        date = new Date();
        category = "без категории";
        ((LsApp) activity.getApplication()).setItemNextId(nextId);
    }

    public Item(String name, int price, String type, long id, Date date) {
        this.name = name;
        this.type = type;
        this.id = id;
        this.price = price;
        category = "без категории";
        this.date = date;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Item item = (Item) o;

        if (id != item.id) return false;
        if (price != item.price) return false;
        if (name != null ? !name.equals(item.name) : item.name != null) return false;
        return type != null ? type.equals(item.type) : item.type == null;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (int) (id ^ (id >>> 32));
        result = 31 * result + price;
        return result;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public long getId() {
        return id;
    }

    public int getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    public Date getDate() {
        return date;
    }

    public static long getNextId() {
        return nextId;
    }

    public static void setNextId(long nextId) {
        Item.nextId = nextId;
    }

    @Override
    public int getItemType() {
        return ITEM_TYPE_ITEM;
    }


}
