package com.ivanchug.moneytracker;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.items);
        final RecyclerView items = (RecyclerView) findViewById(R.id.items);
        items.setAdapter(new ItemsAdapter());
    }

    private class ItemsAdapter extends RecyclerView.Adapter<ItemViewHolder> {

        private List<Item> items = new ArrayList<>();

        public ItemsAdapter() {
            items.add(new Item("Car", 10000));
            items.add(new Item("Apple", 400));
            items.add(new Item("car", 100));
            items.add(new Item("Сковородка с крышкой и антипригарным покрытием", 400));
            items.add(new Item("car", 100));
            items.add(new Item("apple", 400));
            items.add(new Item("CAR", 10));
            items.add(new Item("apple", 400));
            items.add(new Item("car", 100));
            items.add(new Item("apple", 400));
            items.add(new Item("car", 100));
            items.add(new Item("apple", 4));
            items.add(new Item("car", 100));
            items.add(new Item("apple", 400));
        }

        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item, null));
        }

        @Override
        public void onBindViewHolder(ItemViewHolder holder, int position) {
            final Item item = items.get(position);
            holder.name.setText(item.name);
            holder.price.setText(item.price + getString(R.string.rouble));
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        void add(Item item) {
            items.add(0, item);
            notifyItemInserted(0);
        }
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView name, price;

        public ItemViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            price = (TextView) itemView.findViewById(R.id.price);
        }
    }
}
