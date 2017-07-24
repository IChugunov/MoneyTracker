package com.ivanchug.moneytracker.items;

import java.io.Serializable;

/**
 * Created by Иван on 19.07.2017.
 */

public abstract class AbstractItem implements Serializable {
    public static final int ITEM_TYPE_HEADER = 0;
    public static final int ITEM_TYPE_ITEM = 1;

    abstract public int getItemType();
}
