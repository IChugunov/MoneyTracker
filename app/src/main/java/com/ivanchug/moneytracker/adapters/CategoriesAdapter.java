package com.ivanchug.moneytracker.adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ivanchug.moneytracker.AddActivity;
import com.ivanchug.moneytracker.CategoriesActivity;
import com.ivanchug.moneytracker.R;
import com.ivanchug.moneytracker.fragments.SimpleItemsFragment;
import com.ivanchug.moneytracker.items.AbstractItem;
import com.ivanchug.moneytracker.items.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Иван on 22.07.2017.
 */

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder> {

    private List<String> categories = new ArrayList<>();
    private Context context;
    private View previousView;
    private int selectedCategoryPosition;

    public CategoriesAdapter(Context context) {
        this.context = context;
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CategoryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.category, parent, false));
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {
        final String category = categories.get(position);
        holder.categoryName.setText(category);
        if (context instanceof AddActivity) {
            holder.categoryName.setGravity(Gravity.CENTER);
            holder.itemsCount.setVisibility(View.GONE);
        } else {
            int count = 0;
            for (AbstractItem i : ((CategoriesActivity) context).getItemsToShow()) {
                if (i instanceof Item && ((Item) i).getCategory().equals(category))
                    count++;
            }
            holder.itemsCount.setText(String.valueOf(count));
        }


    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public void add(String category) {
        categories.add(0, category);
        notifyItemInserted(0);
    }

    public void addAll(List<String> categories) {
        if (context instanceof AddActivity) {
            categories.remove(categories.size() - 1);
            categories.add(context.getString(R.string.add_new_category));
        }

        this.categories.addAll(categories);
        notifyDataSetChanged();
    }


    public void clear() {
        categories.clear();
    }


    public String remove() {
        final String category = categories.remove(selectedCategoryPosition);
        notifyItemRemoved(selectedCategoryPosition);
        previousView.setBackgroundResource(R.color.colorLightBlueGrey);
        previousView = null;
        return category;
    }

    public void setSelectedCategoryPosition(int selectedCategoryPosition) {
        this.selectedCategoryPosition = selectedCategoryPosition;
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView categoryName;
        private final TextView itemsCount;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            categoryName = (TextView) itemView.findViewById(R.id.category_name);
            itemsCount = (TextView) itemView.findViewById(R.id.items_count);

        }


        @Override
        public void onClick(View v) {
            if (context instanceof AddActivity) {
                String category = categoryName.getText().toString();
                AddActivity addActivity = (AddActivity) context;

                if (category.equals(addActivity.getString(R.string.add_new_category)))
                    addActivity.openNewCategoryLayout();
                else if (!addActivity.getCategory().equals(category)) {
                    addActivity.setCategory(category);
                    v.setBackgroundResource(R.color.backgroundColor);
                    if (previousView != null)
                        previousView.setBackgroundResource(R.color.colorLightBlueGrey);
                    previousView = v;
                    selectedCategoryPosition = getAdapterPosition();
                    addActivity.setRemoveState();
                }


            } else {
                CategoriesActivity categoriesActivity = (CategoriesActivity) context;
                SimpleItemsFragment simpleItemsFragment = new SimpleItemsFragment();
                Bundle args = new Bundle();
                args.putSerializable(CategoriesActivity.ITEMS_TO_SHOW, categoriesActivity.getItemsToShow());
                args.putString(CategoriesActivity.CATEGORY, categoryName.getText().toString());
                simpleItemsFragment.setArguments(args);
                android.support.v4.app.FragmentTransaction ft = categoriesActivity.getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.categories_fragment_container, simpleItemsFragment);
                ft.addToBackStack(null);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }

        }
    }
}
