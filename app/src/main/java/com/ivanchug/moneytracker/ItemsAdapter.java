package com.ivanchug.moneytracker;

import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ivanchug.moneytracker.api.AbstractItem;
import com.ivanchug.moneytracker.api.HeaderItem;
import com.ivanchug.moneytracker.api.Item;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ItemViewHolder> {

    private List<AbstractItem> itemsToShow = new ArrayList<>();
    private SparseBooleanArray selectedItems = new SparseBooleanArray();
    private MainActivity context;
    private SimpleDateFormat formater = new SimpleDateFormat("yyyy.MM.dd");


    public ItemsAdapter(MainActivity context) {
        this.context = context;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == AbstractItem.ITEM_TYPE_HEADER)
            return new ItemViewHolder(LayoutInflater.from(context).inflate(R.layout.header, parent, false));
        else
            return new ItemViewHolder(LayoutInflater.from(context).inflate(R.layout.item, parent, false));
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        int type = getItemViewType(position);
        if (type == AbstractItem.ITEM_TYPE_HEADER) {
            final HeaderItem item = (HeaderItem) itemsToShow.get(position);
            holder.headerName.setText(item.getDate());
        } else {
            final Item item = (Item) itemsToShow.get(position);
            holder.name.setText(item.getName());
            holder.price.setText(item.getPrice() + context.getString(R.string.rouble));
            holder.container.setActivated(selectedItems.get(position, false));
        }


    }

    @Override
    public int getItemViewType(int position) {
        return itemsToShow.get(position).getItemType();
    }

    private TreeMap<String, List<Item>> divideByDate(List<Item> items) {

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

    @Override
    public int getItemCount() {
        return itemsToShow.size();
    }

    public void add(Item item) {
        String date = formater.format(item.getDate());
        if (itemsToShow.isEmpty() || !((HeaderItem) itemsToShow.get(0)).getDate().equals(date)) {
            itemsToShow.add(0, new HeaderItem(date));
            notifyItemInserted(0);
        }

        itemsToShow.add(1, item);
        notifyItemInserted(1);
        String type = item.getType();
        int newTotal = context.getTotals(type) + item.getPrice();
        context.setTotals(newTotal, type);
    }

    public void addAll(List<Item> items, int menuItemSelected) {

        List<Item> itemsSortedByTimeLapse = sortByTimeLapse(items, menuItemSelected);

        int totalAmount = 0;

        if (itemsSortedByTimeLapse != null && !itemsSortedByTimeLapse.isEmpty()) {
            for (Item item : itemsSortedByTimeLapse) {
                totalAmount += item.getPrice();
            }
            context.setTotals(totalAmount, itemsSortedByTimeLapse.get(0).getType());
        }

        TreeMap<String, List<Item>> itemsDividedByDate = divideByDate(itemsSortedByTimeLapse);

        for (String date : itemsDividedByDate.keySet()) {
            HeaderItem header = new HeaderItem(date);
            itemsToShow.add(header);
            for (Item item : itemsDividedByDate.get(date)) {
                itemsToShow.add(item);
            }
        }

        notifyDataSetChanged();

    }


    public void toggleSelection(int pos) {
        if (pos == -1 || itemsToShow.get(pos).getItemType() == AbstractItem.ITEM_TYPE_HEADER)
            return;

        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
        } else {
            selectedItems.put(pos, true);
        }
        notifyItemChanged(pos);
    }


    public void clear() {
        itemsToShow.clear();
    }


    public Item remove(int position) {
        final Item item = (Item) itemsToShow.remove(position);
        String type = item.getType();
        notifyItemRemoved(position);
        int newTotal = context.getTotals(type) - item.getPrice();
        context.setTotals(newTotal, type);
        return item;
    }

    public void clearSelections() {
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }

    private List<Item> sortByTimeLapse(List<Item> items, int menuItemSelected) {
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

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final TextView price;
        private final View container;
        private final TextView headerName;

        public ItemViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            price = (TextView) itemView.findViewById(R.id.price);
            container = itemView.findViewById(R.id.item_container);
            headerName = (TextView) itemView.findViewById(R.id.header_name);
        }
    }

}
