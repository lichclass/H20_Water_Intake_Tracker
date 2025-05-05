package com.shysoftware.h20tracker.repository;

import com.google.gson.Gson;
import com.shysoftware.h20tracker.BuildConfig;
import com.shysoftware.h20tracker.model.User;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;


public class WaterIntakeRepository {
    private final OkHttpClient client;
    private final Gson gson;

    public WaterIntakeRepository() {
        client = new OkHttpClient();
        gson = new Gson();
    }

    public void getTodayProgress(String userId, Callback callback){ //return progress of the day
        String today = LocalDate.now().toString(); // e.g., "2025-05-03". P.S. This does not work before 26

        HttpUrl url = HttpUrl.parse(BuildConfig.SUPABASE_URL + "/water_intakes")
                .newBuilder()
                .addQueryParameter("select", "amount")
                .addQueryParameter("user_id", "eq." + userId)
                .addQueryParameter("date", "eq." + today)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("apikey", BuildConfig.SUPABASE_API_KEY)
                .addHeader("Authorization", "Bearer " + BuildConfig.SUPABASE_API_KEY)
                .build();

        client.newCall(request).enqueue(callback);
    }


    // CRUD Operations
    public void create(){}
    public void read(String userId, Callback callback){
        HttpUrl url = HttpUrl.parse(BuildConfig.SUPABASE_URL + "/water_intakes")
                .newBuilder()
                .addQueryParameter("select", "*")
                .addQueryParameter("user_id", "eq." + userId)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("apikey", BuildConfig.SUPABASE_API_KEY)
                .addHeader("Authorization", "Bearer " + BuildConfig.SUPABASE_API_KEY)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public void update(){}
    public void delete(){}
}
