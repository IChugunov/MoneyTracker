package com.ivanchug.moneytracker;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.ivanchug.moneytracker.api.BalanceResult;
import com.ivanchug.moneytracker.api.Item;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    private TabLayout tabs;
    private ViewPager pages;

    private ArrayList<ItemsFragment> itemsFragments = new ArrayList<>();
    private BalanceFragment balanceFragment;



    private int totalExpenses = 0;
    private int totalIncome = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        tabs = (TabLayout) findViewById(R.id.tabs);
        pages = (ViewPager) findViewById(R.id.pages);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() != R.id.action_choose_time_lapse) {

            int menuItemSelected = 0;
            switch (item.getItemId()) {
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
            for (ItemsFragment fragment : itemsFragments) {

                fragment.setMenuItemSelected(menuItemSelected);
                fragment.loadItems(menuItemSelected);

            }
            if (balanceFragment != null) {
                Handler handler = new Handler();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        balanceFragment.updateData();
                    }
                });
            }
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


    public BalanceResult getBalance() {
        return new BalanceResult(totalExpenses, totalIncome);
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
            if (position == getCount() - 1) {
                BalanceFragment balanceFragment = new BalanceFragment();
                MainActivity.this.balanceFragment = balanceFragment;
                return balanceFragment;
            }


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
