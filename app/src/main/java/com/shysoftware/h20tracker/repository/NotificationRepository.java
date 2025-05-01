package com.shysoftware.h20tracker.repository;

import com.google.gson.Gson;

import okhttp3.OkHttpClient;

public class NotificationRepository {
    private final OkHttpClient client;
    private final Gson gson;

    public NotificationRepository() {
        client = new OkHttpClient();
        gson = new Gson();
    }
}
