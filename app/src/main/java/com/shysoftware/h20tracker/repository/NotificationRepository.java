package com.shysoftware.h20tracker.repository;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.shysoftware.h20tracker.BuildConfig;
import com.shysoftware.h20tracker.model.Notification;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NotificationRepository {
    private final OkHttpClient client;
    private final Gson gson;

    public NotificationRepository() {
        client = new OkHttpClient();
        gson = new Gson();
    }


    // CRUD Operations
    public void create(Notification notification) {

        JsonObject payload = new JsonObject();
        payload.addProperty("user_id", notification.getUserId());
        payload.addProperty("notification_type", notification.getNotifyType().getValue());
        payload.addProperty("title", notification.getTitle());
        payload.addProperty("message", notification.getMessage());
        payload.addProperty("is_seen", false);  // default to false

        RequestBody body = RequestBody.create(
                gson.toJson(payload),
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(BuildConfig.SUPABASE_URL + "/notifications")
                .post(body)
                .addHeader("apikey", BuildConfig.SUPABASE_API_KEY)
                .addHeader("Authorization", BuildConfig.SUPABASE_API_KEY)
                .addHeader("Content-Type", "application/json")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("NOTIF_REPO", "Failed to create notification", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("NOTIF_REPO", "Notification response: " + response.code() + " - " + response.message());
            }
        });
    }

    public void read(String userId, Callback callback){
        HttpUrl url = HttpUrl.parse(BuildConfig.SUPABASE_URL + "/notifications")
                .newBuilder()
                .addQueryParameter("select", "*")
                .addQueryParameter("user_id", "eq." + userId)
                .addQueryParameter("order", "created_at.asc")
                .build();

        Request request = new Request.Builder()
                .url(url)
                .addHeader("apikey", BuildConfig.SUPABASE_API_KEY)
                .addHeader("Authorization", "Bearer " + BuildConfig.SUPABASE_API_KEY)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public void delete(Integer deleteId, Callback callback){
        HttpUrl url = HttpUrl.parse(BuildConfig.SUPABASE_URL + "/notifications")
                .newBuilder()
                .addQueryParameter("notification_id", "eq." + deleteId)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .delete()
                .addHeader("apikey", BuildConfig.SUPABASE_API_KEY)
                .addHeader("Authorization", "Bearer " + BuildConfig.SUPABASE_API_KEY)
                .build();

        client.newCall(request).enqueue(callback);
    }
}
