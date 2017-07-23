package com.ivanchug.moneytracker.items;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by Иван on 23.07.2017.
 */

public class ItemsSortingUtil {

    public static TreeMap<String, List<Item>> divideByDate(List<Item> items) {
        SimpleDateFormat formater = new SimpleDateFormat("yyyy.MM.dd");
        TreeMap<String, List<Item>> result = new TreeMap<>(Collections.reverseOrder());

        if (items != null && items.size() > 0) {
            List<Item> itemsForDate = new ArrayList<>();
            String date = formater.format(items.get(0).getDate());
            for (int i = 0; i < items.size(); i++) {
                Item item = items.get(i);
                String dateToCompare = formater.format(item.getDate());
                if (dateToCompare.equals(date)) {
                    itemsForDate.add(item);
                } else {
                    result.put(date, itemsForDate);
                    itemsForDate = new ArrayList<>();
                    itemsForDate.add(item);
                    date = dateToCompare;
                }
            }
            result.put(date, itemsForDate);
        }


        return result;
    }

    public static List<Item> sortByTimeLapse(List<Item> items, int menuItemSelected) {
        if (menuItemSelected == 3 || items == null || items.isEmpty())
            return items;
        List<Item> result = new ArrayList<>();
        SimpleDateFormat format = null;
        Date currentDate = new Date();
        if (menuItemSelected == 1)
            format = new SimpleDateFormat("MM.yyyy");
        else
            format = new SimpleDateFormat("yyyy");
        String formattedDate = format.format(currentDate);

        for (Item item : items) {
            if (formattedDate.equals(format.format(item.getDate())))
                result.add(item);
            else
                break;
        }

        return result;
    }

    public static List<AbstractItem> sortItemsToShow(TreeMap<String, List<Item>> items) {
        List<AbstractItem> itemsToShow = new ArrayList<>();

        for (String date : items.keySet()) {
            HeaderItem header = new HeaderItem(date);
            itemsToShow.add(header);
            for (Item item : items.get(date)) {
                itemsToShow.add(item);
            }
        }
        return itemsToShow;
    }
}
