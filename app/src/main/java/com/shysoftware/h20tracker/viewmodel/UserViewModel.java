package com.shysoftware.h20tracker.viewmodel;

import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.shysoftware.h20tracker.model.ProfileStats;
import com.shysoftware.h20tracker.repository.UserRepository;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import java.io.IOException;

public class UserViewModel extends ViewModel {

    private final UserRepository userRepository = new UserRepository();
    private final MutableLiveData<ProfileStats> profileStats = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public LiveData<ProfileStats> getProfileStats() {
        return profileStats;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    /**
     * Fetch profile stats based on the userId
     * 
     * @param userId The user ID to fetch the profile stats for
     */
    public void fetchProfileStats(String userId) {
        isLoading.setValue(true);

        userRepository.fetchProfileStats(userId, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("UserViewModel", "Error fetching profile stats: " + e.getMessage());
                isLoading.setValue(false);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                isLoading.setValue(false);

                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    try {
                        // Parse the response JSON into ProfileStats object
                        JsonObject jsonObject = new Gson().fromJson(responseBody, JsonObject.class);
                        ProfileStats stats = new ProfileStats();
                        stats.setTotalWaterLogged(jsonObject.get("total_water_logged").getAsDouble());
                        stats.setLongestStreak(jsonObject.get("longest_streak").getAsInt());
                        stats.setAverageDailyIntake(jsonObject.get("average_daily_intake").getAsDouble());

                        // Post the stats to LiveData
                        profileStats.postValue(stats);

                    } catch (Exception e) {
                        Log.e("UserViewModel", "Error parsing profile stats: " + e.getMessage());
                    }
                } else {
                    Log.e("UserViewModel", "Failed to fetch profile stats: " + response.code());
                }
            }
        });
    }
}
