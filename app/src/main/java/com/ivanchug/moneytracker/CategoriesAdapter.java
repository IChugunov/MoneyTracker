package com.ivanchug.moneytracker;

import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Иван on 22.07.2017.
 */

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.CategoryViewHolder> {

    private List<String> categories = new ArrayList<>();
    private SparseBooleanArray selectedCategories = new SparseBooleanArray();


    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CategoryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.category, parent, false));
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {
        final String category = categories.get(position);
        holder.categoryName.setText(category);

    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    void add(String category) {
        categories.add(0, category);
        notifyItemInserted(0);
    }

    public void addAll(List<String> categories) {
        this.categories.addAll(categories);
        notifyDataSetChanged();
    }

    public void toggleSelection(int pos) {
        if (pos == -1)
            return;
        if (selectedCategories.get(pos, false)) {
            selectedCategories.delete(pos);
        } else {
            selectedCategories.put(pos, true);
        }
        notifyItemChanged(pos);
    }


    public void clear() {
        categories.clear();
    }


    public String remove(int position) {
        final String category = categories.remove(position);
        notifyItemRemoved(position);
        return category;
    }

    public void clearSelections() {
        selectedCategories.clear();
        notifyDataSetChanged();
    }

    public List<Integer> getSelectedCategories() {
        List<Integer> categories = new ArrayList<>(selectedCategories.size());
        for (int i = 0; i < selectedCategories.size(); i++) {
            categories.add(selectedCategories.keyAt(i));
        }
        return categories;
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final TextView categoryName;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            categoryName = (TextView) itemView.findViewById(R.id.category_name);
        }
    }
}
