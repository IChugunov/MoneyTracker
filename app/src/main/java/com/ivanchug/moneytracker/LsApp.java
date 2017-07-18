package com.ivanchug.moneytracker;

import android.app.Application;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ivanchug.moneytracker.db.Item;
import com.ivanchug.moneytracker.db.LSApi;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Иван on 29.06.2017.
 */

public class LsApp extends Application {

    private LSApi api;

    private static final String PREFERENCES_SESSION = "session";
    private static final String KEY_AUTH_TOKEN = "auth-token";
    private static final String ITEM_NEXT_ID = "nextId";

    public LSApi api() {
        return api;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Gson gson = getGson();

        final OkHttpClient client = getOkHttpClient();

        Retrofit retrofit = getBuild(gson, client);

        api = retrofit.create(LSApi.class);
    }

    @NonNull
    private Retrofit getBuild(Gson gson, OkHttpClient client) {
        return new Retrofit.Builder().baseUrl("http://android.loftschool.com/basic/v1/")
                .addConverterFactory(GsonConverterFactory.create(gson)).client(client).build();
    }

    @NonNull
    private OkHttpClient getOkHttpClient() {
        return new OkHttpClient.Builder().addInterceptor(new HttpLoggingInterceptor()
                .setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE))
                .addInterceptor(new AuthIntercepter())
                .build();
    }

    @NonNull
    private Gson getGson() {
        return new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    }

    public void setAuthToken(String token) {
        getSharedPreferences(PREFERENCES_SESSION, MODE_PRIVATE)
                .edit()
                .putString(KEY_AUTH_TOKEN, token)
                .apply();
    }

    public String getAuthToken() {
        return getSharedPreferences(PREFERENCES_SESSION, MODE_PRIVATE)
                .getString(KEY_AUTH_TOKEN, "");
    }

    public void setItemsNextId(long nextId) {
        getSharedPreferences(PREFERENCES_SESSION, MODE_PRIVATE)
                .edit()
                .putLong(ITEM_NEXT_ID, Item.getNextId())
                .apply();
    }

    public long getItemsNextId() {
        return getSharedPreferences(PREFERENCES_SESSION, MODE_PRIVATE)
                .getLong(ITEM_NEXT_ID, 0);
    }

    public boolean isLoggedIn() {
        return !TextUtils.isEmpty(getAuthToken());
    }


    private class AuthIntercepter implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();
            HttpUrl url = originalRequest.url().newBuilder()
                    .addQueryParameter("auth-token", getAuthToken())
                    .build();
            return chain.proceed(originalRequest.newBuilder().url(url).build());
        }
    }
}
