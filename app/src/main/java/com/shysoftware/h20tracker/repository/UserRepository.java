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

    public void fetchUsers(Callback callback){
        Request request = new Request.Builder()
                .url(BuildConfig.SUPABASE_URL + "/users?select=*")
                .get()
                .addHeader("apikey", BuildConfig.SUPABASE_API_KEY )
                .addHeader("Authorization", "Bearer " + BuildConfig.SUPABASE_API_KEY)
                .build();

        client.newCall(request).enqueue(callback);
    }

    /**
     * Forward Geocoding
     * @param placeName
     * @param callback
     */
    public void forwardGeocode(String placeName, Callback callback) {
        Log.d("FORWARD_GEOCODE", "Place: " + placeName);
        HttpUrl httpUrl = HttpUrl.parse(
                        "https://api.mapbox.com/geocoding/v5/mapbox.places/" + Uri.encode(placeName) + ".json"
                )
                .newBuilder()
                .addQueryParameter("access_token", BuildConfig.MAPBOX_TOKEN)
                .addQueryParameter("autocomplete", "true")
                .addQueryParameter("limit", "5")
                .build();

        Request request = new Request.Builder()
                .url(httpUrl)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public void signUpUser(String email, String password, Callback callback){

        // Storing email and password in a json
        JsonObject json = new JsonObject();
        json.addProperty("email", email);
        json.addProperty("password", password);

        // Encapsulating them in a request body
        RequestBody body = RequestBody.create(
                gson.toJson(json),
                MediaType.parse("application/json")
        );

       // Preparing the Complete URL
        Request request = new Request.Builder()
                .url(BuildConfig.SUPABASE_AUTH_URL + "/signup")
                .post(body)
                .addHeader("apikey", BuildConfig.SUPABASE_API_KEY)
                .addHeader("Content-Type", "application/json")
                .build();

        // Actual API call
        client.newCall(request).enqueue(callback);
    }

    public void signInUser(String email, String password, Callback callback) {
        // Create JSON payload
        JsonObject json = new JsonObject();
        json.addProperty("email", email);
        json.addProperty("password", password);

        // Build request body
        RequestBody body = RequestBody.create(
                gson.toJson(json),
                MediaType.parse("application/json")
        );

        // Build request for Supabase login endpoint
        Request request = new Request.Builder()
                .url(BuildConfig.SUPABASE_AUTH_URL + "/token?grant_type=password")
                .post(body)
                .addHeader("apikey", BuildConfig.SUPABASE_API_KEY)
                .addHeader("Content-Type", "application/json")
                .build();

        // Enqueue async call
        client.newCall(request).enqueue(callback);
    }

    // To be Tested
    public void createUserProfile(User user, Callback callback) {
        OkHttpClient client = new OkHttpClient();

        String json = new Gson().toJson(user);
        RequestBody body = RequestBody.create(json, MediaType.get("application/json"));

        Request request = new Request.Builder()
                .url(BuildConfig.SUPABASE_AUTH_URL + "/users")
                .addHeader("apikey", BuildConfig.SUPABASE_API_KEY)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        client.newCall(request).enqueue(callback);
    }


    public void updateUserProfile(User user, Callback callback) {
        JsonObject json = new JsonObject();

        json.addProperty("username", user.getUsername());
        json.addProperty("address", user.getAddress()); // if only city is needed, extract it

        // Note: Will add longitude and latitude

        json.addProperty("height", user.getHeight());
        json.addProperty("weight", user.getWeight());
        json.addProperty("dateOfBirth", user.getDateOfBirth().toString()); // ISO format
        json.addProperty("gender", user.getGender().toString()); // assuming enum with values like MALE/FEMALE

        RequestBody body = RequestBody.create(
                gson.toJson(json),
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(BuildConfig.SUPABASE_AUTH_URL + "/rest/v1/users?id=eq." + user.getUserId())
                .patch(body) // Or PUT, depending on Supabase setup
                .addHeader("apikey", BuildConfig.SUPABASE_API_KEY)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(callback);
    }
    public List<User> parseUserResponse(String json) {
        Type listType = new TypeToken<List<User>>(){}.getType();
        return gson.fromJson(json, listType);
    }


}
