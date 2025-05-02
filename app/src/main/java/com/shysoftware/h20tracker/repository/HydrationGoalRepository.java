package com.shysoftware.h20tracker.repository;

import com.google.gson.Gson;
import com.shysoftware.h20tracker.model.HydrationGoal;
import com.shysoftware.h20tracker.model.User;

import okhttp3.OkHttpClient;

public class HydrationGoalRepository {
    private final OkHttpClient client;
    private final Gson gson;

    public HydrationGoalRepository() {
        client = new OkHttpClient();
        gson = new Gson();
    }

    public Boolean computeAndStoreGoal(User user){
        Boolean result = false;

        Double baselineWaterIntake = user.getWeight() * 35;

        // Adjustment
        Double tempAdj = 0.00;
        Double humAdj = 0.00;


        return result;
    }





    // CRUD Operations
    public void create(){}
    public void read(){}
    public void update(){}
    public void delete(){}
}
