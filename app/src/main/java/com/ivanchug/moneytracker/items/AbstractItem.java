package com.ivanchug.moneytracker.items;

/**
 * Created by Иван on 19.07.2017.
 */

public abstract class AbstractItem {
    public static final int ITEM_TYPE_HEADER = 0;
    public static final int ITEM_TYPE_ITEM = 1;

    abstract public int getItemType();
}
