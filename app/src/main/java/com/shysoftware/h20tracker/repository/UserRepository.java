package com.shysoftware.h20tracker.repository;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.shysoftware.h20tracker.model.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class UserRepository {
    private static final String BASE_URL = "https://YOUR-SUPABASE-URL/rest/v1/";
    private static final String API_KEY = "YOUR_SUPABASE_API_KEY";
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public void signUpUser(String email, String password, Callback callback) {
        RequestBody body = RequestBody.create(JSON,
                "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}");

        Request request = new Request.Builder()
                .url("https://YOUR-SUPABASE-URL/auth/v1/signup")
                .addHeader("apikey", API_KEY)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public void signInUser(String email, String password, Callback callback) {
        RequestBody body = RequestBody.create(JSON,
                "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}");

        Request request = new Request.Builder()
                .url("https://YOUR-SUPABASE-URL/auth/v1/token?grant_type=password")
                .addHeader("apikey", API_KEY)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public void createUserProfile(User user, Callback callback) {
        RequestBody body = RequestBody.create(JSON, gson.toJson(user));

        Request request = new Request.Builder()
                .url(BASE_URL + "users")
                .addHeader("apikey", API_KEY)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public void updateUserProfile(User user, Callback callback) {
        RequestBody body = RequestBody.create(JSON, gson.toJson(user));

        Request request = new Request.Builder()
                .url(BASE_URL + "users?id=eq." + user.getUserId())
                .addHeader("apikey", API_KEY)
                .addHeader("Content-Type", "application/json")
                .patch(body)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public void isProfileExist(String userId, Callback callback) {
        Request request = new Request.Builder()
                .url(BASE_URL + "users?user_id=eq." + userId)
                .addHeader("apikey", API_KEY)
                .addHeader("Accept", "application/json")
                .get()
                .build();

        client.newCall(request).enqueue(callback);
    }

    public void forwardGeocode(String address, Callback callback) {
        String encodedAddress = address.replace(" ", "%20");
        Request request = new Request.Builder()
                .url("https://api.mapbox.com/geocoding/v5/mapbox.places/" + encodedAddress + ".json?access_token=YOUR_MAPBOX_TOKEN")
                .get()
                .build();

        client.newCall(request).enqueue(callback);
    }

    public void fetchTopUsers(Callback callback) {
        Request request = new Request.Builder()
                .url(BASE_URL + "users?order=xp.desc&limit=5")
                .addHeader("apikey", API_KEY)
                .addHeader("Accept", "application/json")
                .get()
                .build();

        client.newCall(request).enqueue(callback);
    }

    public void fetchProfileStats(String userId, Callback callback) {
        String url = BASE_URL + "profile_stats?user_id=eq." + userId;
        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", API_KEY)
                .addHeader("Accept", "application/json")
                .get()
                .build();

        client.newCall(request).enqueue(callback);
    }
}
