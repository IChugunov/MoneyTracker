package com.ivanchug.moneytracker;

import android.app.Application;

/**
 * Created by Иван on 29.06.2017.
 */

public class LsApp extends Application {

    private static final String PREFERENCES_SESSION = "session";
    private static final String ITEM_NEXT_ID = "nextId";

    private boolean isLoggedIn;



    public void setItemNextId(long nextId) {
        getSharedPreferences(PREFERENCES_SESSION, MODE_PRIVATE)
                .edit()
                .putLong(ITEM_NEXT_ID, nextId)
                .apply();
    }

    public long getItemNextId() {
        return getSharedPreferences(PREFERENCES_SESSION, MODE_PRIVATE)
                .getLong(ITEM_NEXT_ID, 0);
    }


    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }

}
