package com.ivanchug.moneytracker;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.ivanchug.moneytracker.fragments.CategoriesFragment;
import com.ivanchug.moneytracker.items.AbstractItem;

import java.util.ArrayList;

public class CategoriesActivity extends AppCompatActivity {

    public static final String ARG_TYPE = "type";
    public static final String ITEMS_TO_SHOW = "items to show";
    public static final String CATEGORY = "category";

    private String type;
    private ArrayList<AbstractItem> itemsToShow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        type = getIntent().getStringExtra(ARG_TYPE);
        itemsToShow = (ArrayList<AbstractItem>) getIntent().getSerializableExtra(ITEMS_TO_SHOW);


        CategoriesFragment categoriesFragment = new CategoriesFragment();
        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.categories_fragment_container, categoriesFragment);
        ft.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public String getType() {
        return type;
    }

    public ArrayList<AbstractItem> getItemsToShow() {
        return itemsToShow;
    }
}
