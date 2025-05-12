package com.shysoftware.h20tracker.repository;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.shysoftware.h20tracker.BuildConfig;
import com.shysoftware.h20tracker.model.HydrationGoal;
import com.shysoftware.h20tracker.model.User;

import org.json.JSONObject;

import java.time.LocalDate;

import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HydrationGoalRepository {
    private final OkHttpClient client;
    private final Gson gson;

    public HydrationGoalRepository() {
        client = new OkHttpClient();
        gson = new Gson();
    }

    // CRUD Operations
    public void create(HydrationGoal hydrationGoal, Callback callback){
        JsonObject payload = new JsonObject();
        payload.addProperty("user_id", hydrationGoal.getUserId());
        payload.addProperty("target_amount_ml", hydrationGoal.getTargetAmountMl());
        payload.addProperty("basis_weight", hydrationGoal.getBasisWeight());
        payload.addProperty("basis_humidity", hydrationGoal.getBasisHumidity());
        payload.addProperty("basis_temp", hydrationGoal.getBasisTemp());
        payload.addProperty("forecast_date", hydrationGoal.getForecastDate().toString());

        RequestBody body = RequestBody.create(
                gson.toJson(payload),
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(BuildConfig.SUPABASE_URL + "/hydration_goals")
                .post(body)
                .addHeader("apikey", BuildConfig.SUPABASE_API_KEY)
                .addHeader("Authorization", "Bearer " + BuildConfig.SUPABASE_API_KEY)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(callback);
    }

    public void read(String userId, LocalDate date, Callback callback) {
        String dateString = date.toString();

        HttpUrl url = HttpUrl.parse(BuildConfig.SUPABASE_URL + "/hydration_goals")
                .newBuilder()
                .addQueryParameter("select", "*")
                .addQueryParameter("user_id", "eq." + userId)
                .addQueryParameter("forecast_date", "eq." + dateString)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("apikey", BuildConfig.SUPABASE_API_KEY)
                .addHeader("Authorization", "Bearer " + BuildConfig.SUPABASE_API_KEY)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public void readAll(String userId, Callback callback) {
        HttpUrl url = HttpUrl.parse(BuildConfig.SUPABASE_URL + "/hydration_goals")
                .newBuilder()
                .addQueryParameter("select", "*")
                .addQueryParameter("user_id", "eq." + userId)
                .addQueryParameter("order", "forecast_date.asc")
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
