package com.ivanchug.moneytracker.api;

import java.io.Serializable;

/**
 * Created by Иван on 08.07.2017.
 */

public class BalanceResult extends Result implements Serializable {
    public long totalExpenses;
    public long totalIncome;

    public BalanceResult(long totalExpenses, long totalIncome) {
        this.totalExpenses = totalExpenses;
        this.totalIncome = totalIncome;
    }
}
