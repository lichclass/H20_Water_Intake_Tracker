package com.shysoftware.h20tracker.repository;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.shysoftware.h20tracker.BuildConfig;
import com.shysoftware.h20tracker.model.User;

import java.time.ZonedDateTime;
import java.util.UUID;

import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class WaterIntakeRepository {
    private final OkHttpClient client;
    private final Gson gson;
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public WaterIntakeRepository() {
        client = new OkHttpClient();
        gson = new Gson();
    }

    public void getTodayProgress(User user, Callback callback) {
        String userId = user.getUserId();
        String today = java.time.LocalDate.now().toString();

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

    public void storeWaterIntake(UUID userId, double amountMl, Callback callback) {
        JsonObject json = new JsonObject();
        json.addProperty("user_id", userId.toString());
        json.addProperty("amount", amountMl);
        json.addProperty("timestamp", ZonedDateTime.now().toString());

        RequestBody body = RequestBody.create(json.toString(), JSON);

        Request request = new Request.Builder()
                .url(BuildConfig.SUPABASE_URL + "/water_intakes")
                .post(body)
                .addHeader("apikey", BuildConfig.SUPABASE_API_KEY)
                .addHeader("Authorization", "Bearer " + BuildConfig.SUPABASE_API_KEY)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(callback);
    }

    // CRUD placeholders
    public void create() {}
    public void read() {}
    public void update() {}
    public void delete() {}
}
