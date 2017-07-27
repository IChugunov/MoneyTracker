package com.ivanchug.moneytracker;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ivanchug.moneytracker.adapters.CategoriesAdapter;
import com.ivanchug.moneytracker.db.MoneyTrackerDbHelper;
import com.ivanchug.moneytracker.items.Item;

import java.util.Date;
import java.util.List;

public class AddActivity extends AppCompatActivity {

    public static final String EXTRA_TYPE = "type";
    public static final String RESULT_ITEM = "item";
    public static final int RC_ADD_ITEM = 99;

    private String type;
    private String baseCategory;
    private String category;
    private Date date = new Date();
    private RecyclerView categories;
    private CategoriesAdapter adapter;
    private View addCategoryLayout;
    private EditText newCategoryName;
    private TextView addCategory;
    private EditText name;
    private EditText amount;
    private TextView add;
    private MenuItem remove;
    private EditText day;
    private EditText month;
    private EditText year;
    private View setDateLayout;
    private View setDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        type = getIntent().getStringExtra(EXTRA_TYPE);
        if (type.equals(Item.TYPE_EXPENSE))
            baseCategory = getString(R.string.expenses_base_category);
        else
            baseCategory = getString(R.string.income_base_category);

        category = baseCategory;

        newCategoryName = (EditText) findViewById(R.id.new_category_name);
        addCategory = (TextView) findViewById(R.id.add_category);
        addCategoryLayout = findViewById(R.id.new_category_layout);
        addCategoryLayout.setVisibility(View.GONE);
        name = (EditText) findViewById(R.id.add_name);
        amount = (EditText) findViewById(R.id.add_amount);
        add = (TextView) findViewById(R.id.add);
        setDateLayout = findViewById(R.id.set_date_layout);
        setDateLayout.setVisibility(View.GONE);
        day = (EditText) findViewById(R.id.set_day);
        month = (EditText) findViewById(R.id.set_month);
        year = (EditText) findViewById(R.id.set_year);
        setDate = findViewById(R.id.set_date);
        setDate.setEnabled(false);

        categories = (RecyclerView) findViewById(R.id.categories_in_add_activity);
        adapter = new CategoriesAdapter(this);
        categories.setAdapter(adapter);
        loadCategories();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                add.setEnabled(!TextUtils.isEmpty(name.getText()) && !TextUtils.isEmpty(amount.getText()) && !amount.getText().toString().matches("[0]*"));
                addCategory.setEnabled(!TextUtils.isEmpty(newCategoryName.getText()));
                boolean dayIsOk = !TextUtils.isEmpty(day.getText()) && !day.getText().toString().matches("[0]*");
                boolean monthIsOk = !TextUtils.isEmpty(month.getText()) && !month.getText().toString().matches("[0]*");
                boolean yearIsOk = !TextUtils.isEmpty(year.getText()) && !year.getText().toString().matches("[0]*") && year.getText().toString().length() == 4;
                setDate.setEnabled(dayIsOk && monthIsOk && yearIsOk);
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

        name.addTextChangedListener(textWatcher);
        amount.addTextChangedListener(textWatcher);
        newCategoryName.addTextChangedListener(textWatcher);
        day.addTextChangedListener(textWatcher);
        month.addTextChangedListener(textWatcher);
        year.addTextChangedListener(textWatcher);


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent result = new Intent();
                result.putExtra(RESULT_ITEM, new Item(name.getText().toString(), Integer.valueOf(amount.getText().toString()), type, AddActivity.this, category, date));
                setResult(RESULT_OK, result);
                finish();
            }
        });

        addCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCategory(newCategoryName.getText().toString());
                newCategoryName.setText("");
                addCategoryLayout.setVisibility(View.GONE);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_add, menu);
        remove = menu.getItem(0);
        setRemoveState();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_remove:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.money_tracker)
                        .setMessage(R.string.confirm_remove_category)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                removeCategory();
                                setRemoveState();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, null)
                        .setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                setRemoveState();
                            }
                        })
                        .setCancelable(false)
                        .show();
                return true;
            case R.id.menu_set_date:
                if (setDateLayout.getVisibility() == View.GONE)
                    setDateLayout.setVisibility(View.VISIBLE);
                else
                    setDateLayout.setVisibility(View.GONE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public void setRemoveState() {
        remove.setVisible(!category.equals(baseCategory));
    }

    public void openNewCategoryLayout() {
        if (addCategoryLayout.getVisibility() == View.GONE)
            addCategoryLayout.setVisibility(View.VISIBLE);
        else
            addCategoryLayout.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void loadCategories() {
        getSupportLoaderManager().restartLoader(0, null, new LoaderManager.LoaderCallbacks<List<String>>() {
            @Override
            public Loader<List<String>> onCreateLoader(int id, Bundle args) {
                return new AsyncTaskLoader<List<String>>(AddActivity.this) {
                    @Override
                    public List<String> loadInBackground() {

                        try {
                            MoneyTrackerDbHelper dbHelper = new MoneyTrackerDbHelper(getContext());
                            return dbHelper.getCategories(dbHelper.getReadableDatabase(), type);

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
                    Toast.makeText(AddActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
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

    private void addCategory(final String category) {
        getSupportLoaderManager().restartLoader(category.hashCode(), null, new LoaderManager.LoaderCallbacks<String>() {
            @Override
            public Loader<String> onCreateLoader(int id, Bundle args) {
                return new AsyncTaskLoader<String>(AddActivity.this) {
                    @Override
                    public String loadInBackground() {
                        try {
                            MoneyTrackerDbHelper dbHelper = new MoneyTrackerDbHelper(getContext());
                            return dbHelper.addCategory(dbHelper.getWritableDatabase(), category, type);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return null;
                        }
                    }
                };
            }

            @Override
            public void onLoadFinished(Loader<String> loader, String data) {
                if (data == null) {
                    Toast.makeText(AddActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
                } else {
                    adapter.add(data);
                    categories.smoothScrollToPosition(0);

                }
            }

            @Override
            public void onLoaderReset(Loader<String> loader) {

            }
        }).forceLoad();
    }

    private void removeCategory() {
        getSupportLoaderManager().restartLoader(category.hashCode(), null, new LoaderManager.LoaderCallbacks<String>() {
            @Override
            public Loader<String> onCreateLoader(int id, Bundle args) {
                return new AsyncTaskLoader<String>(AddActivity.this) {
                    @Override
                    public String loadInBackground() {
                        try {
                            MoneyTrackerDbHelper dbHelper = new MoneyTrackerDbHelper(getContext());
                            return dbHelper.removeCategory(dbHelper.getWritableDatabase(), category, type);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return null;
                        }
                    }
                };
            }

            @Override
            public void onLoadFinished(Loader<String> loader, String data) {
                if (data == null) {
                    Toast.makeText(AddActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
                } else {
                    adapter.remove();
                    category = baseCategory;
                    setRemoveState();
                }
            }

            @Override
            public void onLoaderReset(Loader<String> loader) {

            }
        }).forceLoad();
    }
}
