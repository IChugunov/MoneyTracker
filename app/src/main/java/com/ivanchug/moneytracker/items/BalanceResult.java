package com.ivanchug.moneytracker.items;

import java.io.Serializable;

/**
 * Created by Иван on 08.07.2017.
 */

public class BalanceResult implements Serializable {
    public long totalExpenses;
    public long totalIncome;

    public BalanceResult(long totalExpenses, long totalIncome) {
        this.totalExpenses = totalExpenses;
        this.totalIncome = totalIncome;
    }
}
