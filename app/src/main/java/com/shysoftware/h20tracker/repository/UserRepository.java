package com.shysoftware.h20tracker.repository;

import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.shysoftware.h20tracker.BuildConfig;
import com.shysoftware.h20tracker.model.User;

import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class UserRepository {
    private final OkHttpClient client;
    private final Gson gson;

    public UserRepository() {
        client = new OkHttpClient();
        gson = new Gson();
    }

    /**
     * Auth Registration
     *
     * @param email
     * @param password
     * @param callback
     */
    public void signUpUser(String email, String password, Callback callback){

        // 1. Store in Json
        JsonObject json = new JsonObject();
        json.addProperty("email", email);
        json.addProperty("password", password);

        // 2. Encapsulate
        RequestBody body = RequestBody.create(
                gson.toJson(json),
                MediaType.parse("application/json")
        );

       // 3. Preparation
        Request request = new Request.Builder()
                .url(BuildConfig.SUPABASE_AUTH_URL + "/signup")
                .post(body)
                .addHeader("apikey", BuildConfig.SUPABASE_API_KEY)
                .addHeader("Content-Type", "application/json")
                .build();

        // Enqueue async call
        client.newCall(request).enqueue(callback);
    }

    /**
     * Auth login
     *
     * @param email
     * @param password
     * @param callback
     */
    public void signInUser(String email, String password, Callback callback) {

        // 1. Store in Json
        JsonObject json = new JsonObject();
        json.addProperty("email", email);
        json.addProperty("password", password);

        // 2. Encapsulation
        RequestBody body = RequestBody.create(
                gson.toJson(json),
                MediaType.parse("application/json")
        );

        // 3. Preparation
        Request request = new Request.Builder()
                .url(BuildConfig.SUPABASE_AUTH_URL + "/token?grant_type=password")
                .post(body)
                .addHeader("apikey", BuildConfig.SUPABASE_API_KEY)
                .addHeader("Content-Type", "application/json")
                .build();

        // Enqueue async call
        client.newCall(request).enqueue(callback);
    }

    /**
     * Complete User Details Back-end
     *
     * @param user
     * @param callback
     */
    public void createUserProfile(User user, Callback callback) {

        // 1. Store in Json
        JsonObject payload = new JsonObject();
        payload.addProperty("user_id",       user.getUserId());
        payload.addProperty("username",      user.getUsername());
        payload.addProperty("location_lat",  user.getLocationLat());
        payload.addProperty("location_long", user.getLocationLong());
        payload.addProperty("address",       user.getAddress());
        payload.addProperty("date_of_birth", user.getDateOfBirth().toString());
        payload.addProperty("height",        user.getHeight());
        payload.addProperty("weight",        user.getWeight());
        payload.addProperty("gender",        user.getGender().getValue());

        // 2. Encapsulation
        RequestBody body = RequestBody.create(
                gson.toJson(payload),
                MediaType.parse("application/json")
        );

        // 3. Preparation
        Request request = new Request.Builder()
                .url(BuildConfig.SUPABASE_URL + "/users")
                .post(body)
                .addHeader("apikey", BuildConfig.SUPABASE_API_KEY)
                .addHeader("Content-Type", "application/json")
                .build();

        // Enqueue async call
        client.newCall(request).enqueue(callback);
    }

    /**
     * Update user details back-end
     *
     * @param user
     * @param callback
     */
    public void updateUserProfile(User user, Callback callback) {

        // 1. Store in Json
        JsonObject payload = new JsonObject();
        payload.addProperty("username",      user.getUsername());
        payload.addProperty("location_lat",  user.getLocationLat());
        payload.addProperty("location_long", user.getLocationLong());
        payload.addProperty("address",       user.getAddress());
        payload.addProperty("date_of_birth", user.getDateOfBirth().toString());
        payload.addProperty("height",        user.getHeight());
        payload.addProperty("weight",        user.getWeight());
        payload.addProperty("gender",        user.getGender().getValue());

        // 2. Encapsulation
        RequestBody body = RequestBody.create(
                gson.toJson(payload),
                MediaType.parse("application/json")
        );

        // 3. Preparation
        Request request = new Request.Builder()
                .url(BuildConfig.SUPABASE_URL + "/users?user_id=eq." + user.getUserId())
                .patch(body)
                .addHeader("apikey", BuildConfig.SUPABASE_API_KEY)
                .addHeader("Content-Type", "application/json")
                .build();

        // Enqueue async call
        client.newCall(request).enqueue(callback);
    }

    /**
     * Back-end for checking if a profile exists for a certain user
     *
     * @param userId
     * @param callback
     */
    public void isProfileExist(String userId, Callback callback) {

        // Preparation
        HttpUrl url = HttpUrl.parse(BuildConfig.SUPABASE_URL + "/users")
                .newBuilder()
                .addQueryParameter("select", "user_id")
                .addQueryParameter("user_id", "eq." + userId)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("apikey", BuildConfig.SUPABASE_API_KEY)
                .addHeader("Authorization", "Bearer " + BuildConfig.SUPABASE_API_KEY)
                .build();

        // Enqueue async call
        client.newCall(request).enqueue(callback);
    }

    /**
     * Forward Geocoding given an address name
     *
     * @param placeName
     * @param callback
     */
    public void forwardGeocode(String placeName, Callback callback) {

        // Preparation
        HttpUrl httpUrl = HttpUrl.parse(
                        "https://api.mapbox.com/geocoding/v5/mapbox.places/"
                                + Uri.encode(placeName) + ".json"
                )
                .newBuilder()
                .addQueryParameter("access_token", BuildConfig.MAPBOX_TOKEN)
                .addQueryParameter("autocomplete", "true")
                .addQueryParameter("limit", "5")
                .build();

        Request request = new Request.Builder()
                .url(httpUrl)
                .build();

        // Enqueue async call
        client.newCall(request).enqueue(callback);
    }

    public void fetchUser(String userId, Callback callback) {
        HttpUrl url = HttpUrl.parse(BuildConfig.SUPABASE_URL + "/users")
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
}
