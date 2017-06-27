package com.ivanchug.moneytracker;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;



public class MainActivity extends AppCompatActivity {

    private TabLayout tabs;
    private ViewPager pages;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabs = (TabLayout) findViewById(R.id.tabs);
        pages = (ViewPager) findViewById(R.id.pages);

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
