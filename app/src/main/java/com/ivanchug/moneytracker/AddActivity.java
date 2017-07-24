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

import java.util.List;

public class AddActivity extends AppCompatActivity {

    public static final String EXTRA_TYPE = "type";
    public static final String RESULT_ITEM = "item";
    public static final int RC_ADD_ITEM = 99;

    private String type;
    private String baseCategory;
    private String category;
    private CategoriesAdapter adapter;
    private View addCategoryLayout;
    private View newCategoryButton;
    private EditText newCategoryName;
    private TextView addCategory;
    private EditText name;
    private EditText amount;
    private TextView add;
    private MenuItem remove;

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


        newCategoryButton = findViewById(R.id.new_category_button);
        newCategoryName = (EditText) findViewById(R.id.new_category_name);
        addCategory = (TextView) findViewById(R.id.add_category);
        addCategoryLayout = findViewById(R.id.new_category_layout);
        addCategoryLayout.setVisibility(View.GONE);
        name = (EditText) findViewById(R.id.add_name);
        amount = (EditText) findViewById(R.id.add_amount);
        add = (TextView) findViewById(R.id.add);

        final RecyclerView categories = (RecyclerView) findViewById(R.id.categories_in_add_activity);
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
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        name.addTextChangedListener(textWatcher);
        amount.addTextChangedListener(textWatcher);
        newCategoryName.addTextChangedListener(textWatcher);

        newCategoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addCategoryLayout.getVisibility() == View.GONE)
                    addCategoryLayout.setVisibility(View.VISIBLE);
                else
                    addCategoryLayout.setVisibility(View.GONE);

            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent result = new Intent();
                result.putExtra(RESULT_ITEM, new Item(name.getText().toString(), Integer.valueOf(amount.getText().toString()), type, AddActivity.this, category));
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
        getMenuInflater().inflate(R.menu.remove_menu, menu);
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
                        .setMessage(R.string.confirm_remove)
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



    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    void loadCategories() {
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
