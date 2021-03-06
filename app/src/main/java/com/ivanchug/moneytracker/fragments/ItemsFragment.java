package com.ivanchug.moneytracker.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.ivanchug.moneytracker.AddActivity;
import com.ivanchug.moneytracker.MainActivity;
import com.ivanchug.moneytracker.R;
import com.ivanchug.moneytracker.adapters.ItemsAdapter;
import com.ivanchug.moneytracker.db.MoneyTrackerDbHelper;
import com.ivanchug.moneytracker.items.Item;
import com.ivanchug.moneytracker.items.ItemsSortingUtil;

import java.text.SimpleDateFormat;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Иван on 27.06.2017.
 */

public class ItemsFragment extends Fragment {
    private static final int LOADER_ITEMS_EXPENSE = 999;
    private static final int LOADER_ITEMS_INCOME = 888;
    private static final int LOADER_ADD = 1;
    private static final int LOADER_REMOVE = 2;

    public static final String ARG_TYPE = "type";
    private ItemsAdapter adapter;
    private RecyclerView items;


    private EditText day;
    private String type;
    private View add;
    private View datePanel;
    private EditText month;
    private EditText year;
    private View setTimeLapse;




    private ActionMode actionMode;
    private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.remove_menu, menu);
            add.setVisibility(View.INVISIBLE);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_remove:
                    new AlertDialog.Builder(getContext())
                            .setTitle(R.string.money_tracker)
                            .setMessage(R.string.confirm_remove)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    List<Integer> selectedItems = adapter.getSelectedItems();
                                    for (int i = selectedItems.size() - 1; i >= 0; i--)
                                        removeItem(adapter.remove(selectedItems.get(i)));
                                }
                            })
                            .setNegativeButton(android.R.string.cancel, null)
                            .setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    actionMode.finish();
                                }
                            })
                            .setCancelable(false)
                            .show();
                    return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            add.setVisibility(View.VISIBLE);
            actionMode = null;
            adapter.clearSelections();

        }
    };




    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_items, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        items = (RecyclerView) view.findViewById(R.id.items);
        adapter = new ItemsAdapter((MainActivity) getActivity());
        items.setAdapter(adapter);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(1000);
        itemAnimator.setRemoveDuration(1000);
        items.setItemAnimator(itemAnimator);

        type = getArguments().getString(ARG_TYPE);
        add = view.findViewById(R.id.add_flbutton);
        datePanel = view.findViewById(R.id.date_panel);
        setDatePanelVisible(false);
        month = (EditText) view.findViewById(R.id.month);
        year = (EditText) view.findViewById(R.id.year);
        day = (EditText) view.findViewById(R.id.day);
        setTimeLapse = view.findViewById(R.id.set_time_lapse);
        setTimeLapse.setEnabled(false);



        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                boolean monthIsOk = TextUtils.isEmpty(month.getText()) || !month.getText().toString().matches("[0]*");
                boolean dayIsOk = TextUtils.isEmpty(month.getText()) ? TextUtils.isEmpty(day.getText()) : (TextUtils.isEmpty(day.getText()) || !day.getText().toString().matches("[0]*"));
                boolean yearIsOk = !TextUtils.isEmpty(year.getText()) && !year.getText().toString().matches("[0]*") && year.getText().toString().length() == 4;
                setTimeLapse.setEnabled(dayIsOk && monthIsOk && yearIsOk);
            }

            @Override
            public void afterTextChanged(Editable s) {
                String selectedDay = day.getText().toString();
                String selectedMonth = month.getText().toString();
                try {
                    if (Integer.parseInt(selectedDay) > 31)
                        day.setText("31");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    if (Integer.parseInt(selectedMonth) > 12)
                        month.setText("12");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        day.addTextChangedListener(textWatcher);
        month.addTextChangedListener(textWatcher);
        year.addTextChangedListener(textWatcher);

        final GestureDetector gestureDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {

            @Override
            public void onLongPress(MotionEvent e) {
                if (actionMode == null)
                    actionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(actionModeCallback);

                toggleSelection(e, items);
                actionMode.setTitle(adapter.getSelectedItems().size() + " " + getString(R.string.items_selected));
                setDatePanelVisible(false);
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (actionMode != null) {
                    toggleSelection(e, items);
                    actionMode.setTitle(adapter.getSelectedItems().size() + " " + getString(R.string.items_selected));
                }

                setDatePanelVisible(false);
                return super.onSingleTapConfirmed(e);
            }
        });

        items.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });



        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(getActivity(), AddActivity.class);
                intent.putExtra(AddActivity.EXTRA_TYPE, type);
                startActivityForResult(intent, AddActivity.RC_ADD_ITEM);
            }
        });

        setTimeLapse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).setTimeLapse(getSelectedTimeLapse());
                ((MainActivity) getActivity()).reloadItemsFragments();
                setDatePanelVisible(false);
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        });

        final SwipeRefreshLayout refresh = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadItems(((MainActivity) getActivity()).getTimeLapse(), ((MainActivity) getActivity()).getFormat());
                refresh.setRefreshing(false);
            }
        });

        loadItems(((MainActivity) getActivity()).getTimeLapse(), ((MainActivity) getActivity()).getFormat());

    }


    private void toggleSelection(MotionEvent e, RecyclerView items) {
        adapter.toggleSelection(items.getChildLayoutPosition(items.findChildViewUnder(e.getX(), e.getY())));
    }

    @Override
    public void onResume() {
        super.onResume();
        setDatePanelVisible(false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AddActivity.RC_ADD_ITEM && resultCode == RESULT_OK) {
            Item item = (Item) data.getSerializableExtra(AddActivity.RESULT_ITEM);
            addItem(item);
        }
    }

    public ItemsAdapter getAdapter() {
        return adapter;
    }

    public String getSelectedTimeLapse() {
        StringBuilder result = new StringBuilder();
        String selectedDay = day.getText().toString();
        String selectedMonth = month.getText().toString();

        if (!TextUtils.isEmpty(selectedDay)) {
            if (selectedDay.length() == 1)
                selectedDay = "0" + selectedDay;

            result.append(selectedDay).append(".");
        }

        if (!TextUtils.isEmpty(selectedMonth)) {
            if (selectedMonth.length() == 1)
                selectedMonth = "0" + selectedMonth;

            result.append(selectedMonth).append(".");
        }

        else
            result.append(".");

        result.append(year.getText().toString());


        return result.toString();
    }

    public void setDatePanelVisible(boolean visible) {
        if (visible) {
            datePanel.setVisibility(View.VISIBLE);
        } else
            datePanel.setVisibility(View.GONE);

    }


    void loadItems(final String timeLapse, final SimpleDateFormat format) {
        Integer loaderId = LOADER_ITEMS_EXPENSE;
        if (type.equals(Item.TYPE_INCOME))
            loaderId = LOADER_ITEMS_INCOME;
        getActivity().getSupportLoaderManager().restartLoader(loaderId, null, new LoaderManager.LoaderCallbacks<List<Item>>() {
            @Override
            public Loader<List<Item>> onCreateLoader(int id, Bundle args) {
                return new AsyncTaskLoader<List<Item>>(getContext()) {
                    @Override
                    public List<Item> loadInBackground() {

                        try {
                            MoneyTrackerDbHelper dbHelper = new MoneyTrackerDbHelper(getContext());
                            return dbHelper.getItems(dbHelper.getWritableDatabase(), type);

                        } catch (Exception e) {
                            e.printStackTrace();
                            return null;
                        }
                    }
                };
            }

            @Override
            public void onLoadFinished(Loader<List<Item>> loader, List<Item> data) {
                if (data == null) {
                    Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
                } else {
                    adapter.clear();
                    adapter.addAll(ItemsSortingUtil.prepareItemsForItemsFragment(data, timeLapse, getContext(), format, type));
                    ((MainActivity) getActivity()).setAllItems(data, type);
                }
            }

            @Override
            public void onLoaderReset(Loader<List<Item>> loader) {

            }
        }).forceLoad();
    }

    private void addItem(final Item item) {
        getLoaderManager().restartLoader(LOADER_ADD + item.hashCode(), null, new LoaderManager.LoaderCallbacks<Item>() {
            @Override
            public Loader<Item> onCreateLoader(int id, Bundle args) {
                return new AsyncTaskLoader<Item>(getContext()) {
                    @Override
                    public Item loadInBackground() {
                        try {
                            MoneyTrackerDbHelper dbHelper = new MoneyTrackerDbHelper(getContext());
                            return dbHelper.addItem(dbHelper.getWritableDatabase(), item);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return null;
                        }
                    }
                };
            }

            @Override
            public void onLoadFinished(Loader<Item> loader, Item data) {
                if (data == null) {
                    Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
                } else {
                    ((MainActivity) getActivity()).getAllItems(type).add(item);
                    adapter.add(item);
                    items.smoothScrollToPosition(0);
                }
            }

            @Override
            public void onLoaderReset(Loader<Item> loader) {

            }
        }).forceLoad();
    }

    private void removeItem(final Item item) {
        getLoaderManager().restartLoader(LOADER_REMOVE + item.hashCode(), null, new LoaderManager.LoaderCallbacks<Item>() {
            @Override
            public Loader<Item> onCreateLoader(int id, Bundle args) {
                return new AsyncTaskLoader<Item>(getContext()) {
                    @Override
                    public Item loadInBackground() {
                        try {
                            MoneyTrackerDbHelper dbHelper = new MoneyTrackerDbHelper(getContext());
                            return dbHelper.removeItem(dbHelper.getWritableDatabase(), item);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return null;
                        }
                    }
                };
            }

            @Override
            public void onLoadFinished(Loader<Item> loader, Item data) {
                if (data == null) {
                    Toast.makeText(getContext(), R.string.error, Toast.LENGTH_SHORT).show();
                } else {
                    ((MainActivity) getActivity()).getAllItems(type).remove(item);
                }
            }

            @Override
            public void onLoaderReset(Loader<Item> loader) {

            }
        }).forceLoad();
    }


}
