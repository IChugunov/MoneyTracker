package com.ivanchug.moneytracker.fragments;

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
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ivanchug.moneytracker.AddActivity;
import com.ivanchug.moneytracker.MainActivity;
import com.ivanchug.moneytracker.R;
import com.ivanchug.moneytracker.adapters.ItemsAdapter;
import com.ivanchug.moneytracker.db.MoneyTrackerDbHelper;
import com.ivanchug.moneytracker.items.Item;
import com.ivanchug.moneytracker.items.ItemsSortingUtil;

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

    private String type;
    private View add;



    private ActionMode actionMode;
    private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.itemsfragment_menu, menu);
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
                            .setTitle(R.string.app_name)
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
        final RecyclerView items = (RecyclerView) view.findViewById(R.id.items);
        adapter = new ItemsAdapter((MainActivity) getActivity());
        items.setAdapter(adapter);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(1000);
        itemAnimator.setRemoveDuration(1000);
        items.setItemAnimator(itemAnimator);

        type = getArguments().getString(ARG_TYPE);
        add = view.findViewById(R.id.add_flbutton);

        final GestureDetector gestureDetector = new GestureDetector(getActivity(), new GestureDetector.SimpleOnGestureListener() {

            @Override
            public void onLongPress(MotionEvent e) {
                if (actionMode == null)
                    actionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(actionModeCallback);

                toggleSelection(e, items);
                actionMode.setTitle(adapter.getSelectedItems().size() + " " + getString(R.string.items_selected));
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (actionMode != null) {
                    toggleSelection(e, items);
                    actionMode.setTitle(adapter.getSelectedItems().size() + " " + getString(R.string.items_selected));
                }


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

        final SwipeRefreshLayout refresh = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadItems(((MainActivity) getActivity()).getMenuItemSelected());
                refresh.setRefreshing(false);
            }
        });

        loadItems(((MainActivity) getActivity()).getMenuItemSelected());
    }


    private void toggleSelection(MotionEvent e, RecyclerView items) {
        adapter.toggleSelection(items.getChildLayoutPosition(items.findChildViewUnder(e.getX(), e.getY())));
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

    void loadItems(final int menuItemSelected) {
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
                            return dbHelper.getItems(dbHelper.getReadableDatabase(), type);

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
                    adapter.addAll(ItemsSortingUtil.prepareItemsForItemsFragment(data, menuItemSelected, getContext()));
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
                    adapter.add(item);
                    ((MainActivity) getActivity()).getAllItems(type).add(0, item);
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
