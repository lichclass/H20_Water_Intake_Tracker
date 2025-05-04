package com.shysoftware.h20tracker.repository;

import android.os.Build;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.shysoftware.h20tracker.BuildConfig;
import com.shysoftware.h20tracker.model.WeatherCondition;
import com.shysoftware.h20tracker.model.WeatherData;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WeatherDataRepository {
    private OkHttpClient client;
    private Gson gson;

    public WeatherDataRepository() {
        client = new OkHttpClient();
        gson = new Gson();
    }

    public void fetchCurrentForecast(Double lat, Double lon, Callback callback) {
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

    public void create(WeatherData weatherData, Callback callback) {

        JsonObject payload = new JsonObject();
        payload.addProperty("user_id", weatherData.getUserId());
        payload.addProperty("temperature_c", weatherData.getTemperatureC());
        payload.addProperty("humidity_percent", weatherData.getHumidityPercent());
        payload.addProperty("weather_condition", weatherData.getWeatherCondition().getValue().toLowerCase());
        payload.addProperty("date", weatherData.getDate().toString());

        RequestBody body = RequestBody.create(
                gson.toJson(payload),
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(BuildConfig.SUPABASE_URL + "/weather_data")
                .post(body)
                .addHeader("apikey", BuildConfig.SUPABASE_API_KEY)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(callback);
    }

    public void read(String userId, LocalDate date, Callback callback){
        String dateString = date.toString();

        HttpUrl url = HttpUrl.parse(BuildConfig.SUPABASE_URL + "/weather_data")
                .newBuilder()
                .addQueryParameter("select", "*")
                .addQueryParameter("user_id", "eq." + userId)
                .addQueryParameter("date", "eq." + dateString)
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
