package com.shysoftware.h20tracker.repository;

import android.os.Build;

import com.google.gson.Gson;
import com.shysoftware.h20tracker.BuildConfig;
import com.shysoftware.h20tracker.model.WeatherCondition;

import java.time.LocalDate;

import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WeatherDataRepository {
    private OkHttpClient client;
    private Gson gson;

    public WeatherDataRepository() {
        client = new OkHttpClient();
        gson = new Gson();
    }

    public void fetchCurrentWeather(Double lat, Double lon, Callback callback) {
        String url = "https://api.openweathermap.org/data/2.5/forecast"
                + "?lat=" + lat
                + "&lon=" + lon
                + "&appid=" + BuildConfig.OPENWEATHER_API_KEY
                + "&units=metric";

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public void checkCurrentForecast(String userId, Callback callback) {
        LocalDate today = LocalDate.now();
        String todayStr = today.toString();

        HttpUrl url = HttpUrl.parse(BuildConfig.SUPABASE_URL + "/weather_data")
                .newBuilder()
                .addQueryParameter("select", "*")
                .addQueryParameter("user_id", "eq." + userId)
                .addQueryParameter("created_at", "eq." + todayStr)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", BuildConfig.SUPABASE_API_KEY)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(callback);
    }

}
