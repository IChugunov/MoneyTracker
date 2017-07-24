package com.ivanchug.moneytracker.items;

import java.io.Serializable;

/**
 * Created by Иван on 19.07.2017.
 */

public class HeaderItem extends AbstractItem implements Serializable {
    private String date;

    public String getDate() {
        return date;
    }

    public HeaderItem(String date) {

        this.date = date;
    }

    @Override
    public int getItemType() {
        return ITEM_TYPE_HEADER;
    }
}
