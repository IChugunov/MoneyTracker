package com.ivanchug.moneytracker.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ivanchug.moneytracker.CategoriesActivity;
import com.ivanchug.moneytracker.R;
import com.ivanchug.moneytracker.adapters.ItemsAdapter;
import com.ivanchug.moneytracker.items.AbstractItem;
import com.ivanchug.moneytracker.items.ItemsSortingUtil;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class SimpleItemsFragment extends Fragment {

    private String category;


    public SimpleItemsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_simple_items, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        category = getArguments().getString(CategoriesActivity.CATEGORY);
        final RecyclerView items = (RecyclerView) view.findViewById(R.id.simple_items);
        ItemsAdapter adapter = new ItemsAdapter(getContext());
        items.setAdapter(adapter);

        ArrayList<AbstractItem> receivedItems = (ArrayList<AbstractItem>) getArguments().getSerializable(CategoriesActivity.ITEMS_TO_SHOW);

        adapter.addAll(ItemsSortingUtil.prepareItemsForSimpleItemsFragment(receivedItems, category));

        getActivity().setTitle(category);
    }
}
