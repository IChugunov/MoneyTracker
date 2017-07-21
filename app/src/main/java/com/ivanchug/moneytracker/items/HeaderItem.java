package com.ivanchug.moneytracker.items;

/**
 * Created by Иван on 19.07.2017.
 */

public class HeaderItem extends AbstractItem {
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
