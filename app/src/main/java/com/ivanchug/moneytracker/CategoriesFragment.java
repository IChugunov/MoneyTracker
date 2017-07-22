package com.ivanchug.moneytracker;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
        adapter = new CategoriesAdapter();
        categories.setAdapter(adapter);


        RecyclerView.ItemAnimator categoryAnimator = new DefaultItemAnimator();
        categoryAnimator.setAddDuration(1000);
        categoryAnimator.setRemoveDuration(1000);
        categories.setItemAnimator(categoryAnimator);

        view.findViewById(R.id.categorybutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleItemsFragment simpleItemsFragment = new SimpleItemsFragment();
                Bundle args = new Bundle();
                args.putSerializable(CategoriesActivity.ITEMS_TO_SHOW, ((CategoriesActivity) getActivity()).getItemsToShow());
                args.putString(CategoriesActivity.CATEGORY, "без категории");
                simpleItemsFragment.setArguments(args);
                android.support.v4.app.FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.categories_fragment_container, simpleItemsFragment);
                ft.addToBackStack(null);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }
        });

        loadCategories();
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
