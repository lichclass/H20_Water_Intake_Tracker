package com.shysoftware.h20tracker.repository;

import com.google.gson.Gson;

import okhttp3.OkHttpClient;

public class HydrationGoalRepository {
    private final OkHttpClient client;
    private final Gson gson;

    public HydrationGoalRepository() {
        client = new OkHttpClient();
        gson = new Gson();
    }
}
