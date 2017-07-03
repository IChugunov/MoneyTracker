package com.ivanchug.moneytracker;

import android.app.Application;
import android.support.annotation.NonNull;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ivanchug.moneytracker.api.LSApi;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Иван on 29.06.2017.
 */

public class LsApp extends Application {
    private LSApi api;

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
        return new Retrofit.Builder().baseUrl("http://loftschoolandroid.getsandbox.com/")
                .addConverterFactory(GsonConverterFactory.create(gson)).client(client).build();
    }

    @NonNull
    private OkHttpClient getOkHttpClient() {
        return new OkHttpClient.Builder().addInterceptor(new HttpLoggingInterceptor()
                .setLevel(BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE))
                .build();
    }

    @NonNull
    private Gson getGson() {
        return new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    }


}
