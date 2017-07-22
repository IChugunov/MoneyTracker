package com.ivanchug.moneytracker;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class CategoriesFragment extends Fragment {


    public CategoriesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_categories, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final RecyclerView categories = (RecyclerView) view.findViewById(R.id.categories);
        CategoriesAdapter adapter = new CategoriesAdapter();
        categories.setAdapter(adapter);

        RecyclerView.ItemAnimator categoryAnimator = new DefaultItemAnimator();
        categoryAnimator.setAddDuration(1000);
        categoryAnimator.setRemoveDuration(1000);
        categories.setItemAnimator(categoryAnimator);
    }
}
