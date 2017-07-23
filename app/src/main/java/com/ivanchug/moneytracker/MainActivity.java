package com.ivanchug.moneytracker;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.ivanchug.moneytracker.fragments.ItemsFragment;
import com.ivanchug.moneytracker.items.AbstractItem;
import com.ivanchug.moneytracker.items.BalanceResult;
import com.ivanchug.moneytracker.items.Item;
import com.ivanchug.moneytracker.items.ItemsSortingUtil;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private TabLayout tabs;
    private ViewPager pages;
    private ArrayList<ItemsFragment> itemsFragments = new ArrayList<>();
    private ArrayList<Item> expensesItems;
    private ArrayList<Item> incomeItems;

    private int menuItemSelected = 3;



    private int totalExpenses = 0;
    private int totalIncome = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        tabs = (TabLayout) findViewById(R.id.tabs);
        pages = (ViewPager) findViewById(R.id.pages);

        if (savedInstanceState != null)
            menuItemSelected = savedInstanceState.getInt("menuItemSelected");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("menuItemSelected", menuItemSelected);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_show_balance) {
            final Intent intent = new Intent(this, BalanceActivity.class);
            intent.putExtra(BalanceActivity.BALANCE_RESULT, new BalanceResult(totalExpenses, totalIncome));
            startActivity(intent);
        } else if (itemId == R.id.action_categories) {
            int selectedTadPosition = tabs.getSelectedTabPosition();

            String itemFragmentType = Item.TYPE_EXPENSE;
            if (selectedTadPosition == 1)
                itemFragmentType = Item.TYPE_INCOME;

            final Intent intent = new Intent(this, CategoriesActivity.class);
            intent.putExtra(CategoriesActivity.ITEMS_TO_SHOW, new ArrayList<AbstractItem>(itemsFragments.get(selectedTadPosition).getAdapter().getItemsToShow()));
            intent.putExtra(CategoriesActivity.ARG_TYPE, itemFragmentType);
            startActivity(intent);
        } else if (itemId != R.id.action_choose_time_lapse) {

            switch (itemId) {
                case R.id.time_lapse_month:
                    menuItemSelected = 1;
                    break;
                case R.id.time_lapse_year:
                    menuItemSelected = 2;
                    break;
                case R.id.time_lapse_all:
                    menuItemSelected = 3;
                    break;

            }
            itemsFragments.get(0).getAdapter().clear();
            itemsFragments.get(0).getAdapter().addAll(ItemsSortingUtil.prepareItemsForItemsFragment(expensesItems, menuItemSelected, this));
            itemsFragments.get(1).getAdapter().clear();
            itemsFragments.get(1).getAdapter().addAll(ItemsSortingUtil.prepareItemsForItemsFragment(incomeItems, menuItemSelected, this));
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!((LsApp) getApplication()).isLoggedIn())
            startActivity(new Intent(this, AuthActivity.class));
        else {
            initUI();
        }
    }


    private void initUI() {
        if (pages.getAdapter() != null)
            return;

        pages.setAdapter(new MainPagerAdapter());
        tabs.setupWithViewPager(pages);
    }

    public int getTotals(String type) {
        if (type.equals(Item.TYPE_EXPENSE))
            return totalExpenses;
        else
            return totalIncome;
    }

    public void setTotals(int totalsAmount, String type) {
        if (type.equals(Item.TYPE_EXPENSE))
            totalExpenses = totalsAmount;
        else
            totalIncome = totalsAmount;
    }

    public List<Item> getAllItems(String type) {
        if (type.equals(Item.TYPE_EXPENSE))
            return expensesItems;
        else
            return incomeItems;
    }

    public void setAllItems(List<Item> items, String type) {
        if (type.equals(Item.TYPE_EXPENSE))
            expensesItems = (ArrayList<Item>) items;
        else
            incomeItems = (ArrayList<Item>) items;
        ;
    }

    public int getMenuItemSelected() {
        return menuItemSelected;
    }

    private class MainPagerAdapter extends FragmentPagerAdapter {
        private final String[] titles;
        private final String[] types = {Item.TYPE_EXPENSE, Item.TYPE_INCOME};


        public MainPagerAdapter() {
            super(getSupportFragmentManager());
            titles = getResources().getStringArray(R.array.main_pager_titles);
        }

        @Override
        public Fragment getItem(int position) {

            Bundle args = new Bundle();
            final ItemsFragment itemsFragment = new ItemsFragment();
            args.putString(ItemsFragment.ARG_TYPE, types[position]);
            itemsFragment.setArguments(args);
            itemsFragments.add(itemsFragment);
            return itemsFragment;
        }



        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public int getCount() {
            return titles.length;
        }


    }
}
