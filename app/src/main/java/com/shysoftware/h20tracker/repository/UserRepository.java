package com.shysoftware.h20tracker.repository;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.shysoftware.h20tracker.model.ProfileStats;
import com.shysoftware.h20tracker.network.NetworkUtil;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;

public class UserRepository {
    private static final String BASE_URL = "https://your.supabase.project.url";
    private final OkHttpClient client = new OkHttpClient();

    /**
     * Fetches profile statistics for a given userId.
     *
     * @param userId  The user ID to fetch profile stats for.
     * @param callback Callback to handle success or failure.
     */
    public void fetchProfileStats(String userId, Callback callback) {
        String url = BASE_URL + "/rpc/get_profile_stats"; // RPC endpoint in Supabase
        
        // Create the request body with userId as input
        RequestBody body = new FormBody.Builder()
                .add("user_id_input", userId)
                .build();

        // Build the request
        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", NetworkUtil.API_KEY)
                .addHeader("Authorization", "Bearer " + NetworkUtil.getAccessToken()) // Assuming you're using bearer token for authorization
                .post(body)
                .build();

        // Execute the request asynchronously
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
