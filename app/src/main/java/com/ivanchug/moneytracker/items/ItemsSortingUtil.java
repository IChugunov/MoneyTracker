package com.ivanchug.moneytracker.items;

import android.content.Context;

import com.ivanchug.moneytracker.MainActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
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

    public static List<Item> sortByTimeLapse(List<Item> items, String timeLapse, SimpleDateFormat format) {
        if (timeLapse == null || items == null || items.isEmpty())
            return items;
        List<Item> result = new ArrayList<>();

        for (Item item : items) {
            String date = format.format(item.getDate());
            if (timeLapse.equals(date))
                result.add(item);

        }

        return result;
    }

    public static List<AbstractItem> sortItemsToShow(TreeMap<String, List<Item>> items) {
        List<AbstractItem> itemsToShow = new ArrayList<>();

        for (String date : items.keySet()) {
            String[] splitDate = date.split("\\.");
            StringBuilder builder = new StringBuilder();
            builder.append(splitDate[2]).append(".").append(splitDate[1]).append(".").append(splitDate[0]);
            HeaderItem header = new HeaderItem(builder.toString());
            itemsToShow.add(header);
            for (Item item : items.get(date)) {
                itemsToShow.add(item);
            }
        }
        return itemsToShow;
    }

    public static List<AbstractItem> prepareItemsForItemsFragment(List<Item> items, String timeLapse, Context context, SimpleDateFormat format) {
        List<Item> itemsSortedByTimeLapse = ItemsSortingUtil.sortByTimeLapse(items, timeLapse, format);

        int totalAmount = 0;

        if (itemsSortedByTimeLapse != null && !itemsSortedByTimeLapse.isEmpty()) {
            for (Item item : itemsSortedByTimeLapse) {
                totalAmount += item.getPrice();
            }
            ((MainActivity) context).setTotals(totalAmount, itemsSortedByTimeLapse.get(0).getType());
        }

        TreeMap<String, List<Item>> itemsDividedByDate = ItemsSortingUtil.divideByDate(itemsSortedByTimeLapse);

        return ItemsSortingUtil.sortItemsToShow(itemsDividedByDate);
    }

    public static List<AbstractItem> prepareItemsForSimpleItemsFragment(List<AbstractItem> receivedItems, String category) {
        List<Item> itemsWithoutHeaders = new ArrayList<>();
        for (AbstractItem item : receivedItems) {
            if (item instanceof Item && ((Item) item).getCategory().equals(category))
                itemsWithoutHeaders.add((Item) item);
        }

        TreeMap<String, List<Item>> itemsDividedByDate = ItemsSortingUtil.divideByDate(itemsWithoutHeaders);
        return ItemsSortingUtil.sortItemsToShow(itemsDividedByDate);
    }
}
