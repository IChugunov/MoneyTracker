package com.ivanchug.moneytracker;


import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

public class CategoriesActivity extends AppCompatActivity {

    public static final String ARG_TYPE = "type";

    private String type;
    private View add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        type = getIntent().getStringExtra(ARG_TYPE);
        add = findViewById(R.id.add_category_flbutton);

        CategoriesFragment categoriesFragment = new CategoriesFragment();
        android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.categories_fragment_container, categoriesFragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
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
}
