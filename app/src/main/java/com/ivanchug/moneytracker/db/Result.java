package com.ivanchug.moneytracker.db;

import android.text.TextUtils;

/**
 * Created by Иван on 29.06.2017.
 */

public class Result {
    String status;

    public boolean isSuccess() {
        return TextUtils.equals(status, "success");
    }
}
