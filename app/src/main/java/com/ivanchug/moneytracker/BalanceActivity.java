package com.ivanchug.moneytracker;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.ivanchug.moneytracker.items.BalanceResult;


public class BalanceActivity extends AppCompatActivity {

    final static String BALANCE_RESULT = "balance_result";

    BalanceResult balanceResult;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance);

        balanceResult = (BalanceResult) (getIntent().getSerializableExtra(BALANCE_RESULT));

        TextView balance = (TextView) findViewById(R.id.balance_amount);
        TextView expense = (TextView) findViewById(R.id.expense_amount);
        TextView income = (TextView) findViewById(R.id.income_amount);
        DiagramView diagram = (DiagramView) findViewById(R.id.diagram);

        balance.setText(getString(R.string.price, balanceResult.totalIncome - balanceResult.totalExpenses));
        expense.setText(getString(R.string.price, balanceResult.totalExpenses));
        income.setText(getString(R.string.price, balanceResult.totalIncome));
        diagram.update(balanceResult.totalExpenses, balanceResult.totalIncome);
    }



}
