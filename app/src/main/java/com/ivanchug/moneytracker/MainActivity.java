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
import android.widget.Toast;

import com.ivanchug.moneytracker.api.Item;


public class MainActivity extends AppCompatActivity {

    private TabLayout tabs;
    private ViewPager pages;

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
        switch (item.getItemId()) {
            case R.id.time_lapse_month:
                Toast.makeText(this, "Месяц", Toast.LENGTH_SHORT).show();
                break;
            case R.id.time_lapse_year:
                Toast.makeText(this, "год", Toast.LENGTH_SHORT).show();
                break;
            case R.id.time_lapse_all:
                Toast.makeText(this, "все", Toast.LENGTH_SHORT).show();
                break;
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

    private class MainPagerAdapter extends FragmentPagerAdapter {
        private final String[] titles;
        private final String[] types = {Item.TYPE_EXPENSE, Item.TYPE_INCOME};


        public MainPagerAdapter() {
            super(getSupportFragmentManager());
            titles = getResources().getStringArray(R.array.main_pager_titles);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == getCount() - 1)
                return new BalanceFragment();

            Bundle args = new Bundle();
            final ItemsFragment itemsFragment = new ItemsFragment();
            args.putString(ItemsFragment.ARG_TYPE, types[position]);
            itemsFragment.setArguments(args);
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
