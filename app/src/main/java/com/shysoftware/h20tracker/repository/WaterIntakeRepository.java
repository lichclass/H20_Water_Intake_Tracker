package com.shysoftware.h20tracker.repository;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.shysoftware.h20tracker.BuildConfig;

import java.time.LocalDate;

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


    public void logWaterIntake(String userId, double amount, Callback callback) {
        String date = LocalDate.now().toString();

        JsonObject payload = new JsonObject();
        payload.addProperty("user_id", userId);
        payload.addProperty("amount", amount);
        payload.addProperty("date", date);

        RequestBody body = RequestBody.create(
                gson.toJson(payload),
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(BuildConfig.SUPABASE_URL + "/water_intakes")
                .post(body)
                .addHeader("apikey", BuildConfig.SUPABASE_API_KEY)
                .addHeader("Authorization", "Bearer " + BuildConfig.SUPABASE_API_KEY)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(callback);
    }


    public void deleteIntakeEntry(Integer entryId, Callback callback) {
        HttpUrl url = HttpUrl.parse(BuildConfig.SUPABASE_URL + "/water_intakes")
                .newBuilder()
                .addQueryParameter("intake_id", "eq." + entryId)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .delete()
                .addHeader("apikey", BuildConfig.SUPABASE_API_KEY)
                .addHeader("Authorization", "Bearer " + BuildConfig.SUPABASE_API_KEY)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public void read(String userId, Callback callback){
        HttpUrl url = HttpUrl.parse(BuildConfig.SUPABASE_URL + "/water_intakes")
                .newBuilder()
                .addQueryParameter("select", "*")
                .addQueryParameter("user_id", "eq." + userId)
                .addQueryParameter("order", "created_at.desc")
                .build();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("apikey", BuildConfig.SUPABASE_API_KEY)
                .addHeader("Authorization", "Bearer " + BuildConfig.SUPABASE_API_KEY)
                .build();

        client.newCall(request).enqueue(callback);
    }

}
