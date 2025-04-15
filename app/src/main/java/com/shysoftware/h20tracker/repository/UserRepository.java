package com.shysoftware.h20tracker.repository;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.shysoftware.h20tracker.BuildConfig;
import com.shysoftware.h20tracker.model.User;

import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Callback;
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

    public List<User> parseUserResponse(String json) {
        Type listType = new TypeToken<List<User>>(){}.getType();
        return gson.fromJson(json, listType);
    }


}
