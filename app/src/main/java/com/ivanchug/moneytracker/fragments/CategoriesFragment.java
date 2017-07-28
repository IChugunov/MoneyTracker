package com.ivanchug.moneytracker.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ivanchug.moneytracker.CategoriesActivity;
import com.ivanchug.moneytracker.R;
import com.ivanchug.moneytracker.adapters.CategoriesAdapter;
import com.ivanchug.moneytracker.db.MoneyTrackerDbHelper;
import com.ivanchug.moneytracker.items.Item;

import java.util.List;


public class CategoriesFragment extends Fragment {

    private static final int LOADER_CATEGORIES = 0;
    private static final int LOADER_ADD = 1;
    private static final int LOADER_REMOVE = 2;

    private CategoriesAdapter adapter;



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
        adapter = new CategoriesAdapter(getActivity());
        categories.setAdapter(adapter);


        RecyclerView.ItemAnimator categoryAnimator = new DefaultItemAnimator();
        categoryAnimator.setAddDuration(1000);
        categoryAnimator.setRemoveDuration(1000);
        categories.setItemAnimator(categoryAnimator);


        loadCategories();
    }

    @Override
    public void onResume() {
        super.onResume();
        String title;
        CategoriesActivity activity = (CategoriesActivity) getActivity();
        if (activity.getType().equals(Item.TYPE_EXPENSE))
            title = activity.getString(R.string.expenses_title);
        else
            title = activity.getString(R.string.incomes_title);

        title = title + activity.getString(R.string.period);
        getActivity().setTitle(title);
    }

    void loadCategories() {
        getActivity().getSupportLoaderManager().restartLoader(LOADER_CATEGORIES, null, new LoaderManager.LoaderCallbacks<List<String>>() {
            @Override
            public Loader<List<String>> onCreateLoader(int id, Bundle args) {
                return new AsyncTaskLoader<List<String>>(getContext()) {
                    @Override
                    public List<String> loadInBackground() {

                        try {
                            MoneyTrackerDbHelper dbHelper = new MoneyTrackerDbHelper(getContext());
                            return dbHelper.getCategories(dbHelper.getReadableDatabase(), ((CategoriesActivity) getActivity()).getType());

                        } catch (Exception e) {
                            e.printStackTrace();
                            return null;
                        }
                    }
                };
            }

            @Override
            public void onLoadFinished(Loader<List<String>> loader, List<String> data) {
                if (data == null) {
                    Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
                } else {
                    adapter.clear();
                    adapter.addAll(data);
                }
            }

            @Override
            public void onLoaderReset(Loader<List<String>> loader) {

            }
        }).forceLoad();
    }
}
