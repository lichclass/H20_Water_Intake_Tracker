package com.shysoftware.h20tracker.viewmodel;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.shysoftware.h20tracker.model.User;
import com.shysoftware.h20tracker.repository.UserRepository;
import com.shysoftware.h20tracker.repository.WaterIntakeRepository;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDate;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
public class WaterIntakeViewModel extends ViewModel {
    private final WaterIntakeRepository waterIntakeRepository = new WaterIntakeRepository();
    private final MutableLiveData<Double> todayIntake = new MutableLiveData<>();

    public LiveData<Double> getTodayIntake() {
        return todayIntake;
    }

    /**
     * Gets Today's Progress of a User
     *
     * @param user
     * @return
     */
    public void getProgress(User user) {
        waterIntakeRepository.getTodayProgress(user.getUserId(), new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("PROGRESS", "Failed to fetch intake: " + e.getMessage());
                todayIntake.postValue(0.00);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.d("WATER INTAKE", response.toString());
                if (response.isSuccessful() && response.body() != null) {
                    String body = response.body().string();

                    try {
                        JSONArray array = new JSONArray(body);
                        double total = 0.00;

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            if (obj.has("amount") && !obj.isNull("amount")) {
                                total += obj.getDouble("amount");
                            }
                        }

                        Log.d("HYDRATION_PROGRESS", "Total water intake today: " + total);
                        todayIntake.postValue(total);

                    } catch (JSONException e) {
                        Log.e("HYDRATION_PROGRESS", "JSON parsing error", e);
                        todayIntake.postValue(0.00);
                    }
                } else {
                    Log.w("HYDRATION_PROGRESS", "Non-successful response: " + response.code());
                    todayIntake.postValue(0.00);
                }
            }
        });
    }

    public void getWeeklyProgress(User user, LocalDate today){
        waterIntakeRepository.read(user.getUserId(), new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("WEEKLY_PROGRESS", "Failed to fetch intake: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String body = response.body().string();
                    double weeklyTotal = 0.00;

                    try {
                        JSONArray array = new JSONArray(body);

                        // Week start is Monday, and week end is today (NOT Sunday)
                        LocalDate weekStart = today.with(java.time.DayOfWeek.MONDAY);
                        LocalDate weekEnd = today;  // Only up to today, not full Sunday

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);

                            if (obj.has("amount") && !obj.isNull("amount") &&
                                    obj.has("date") && !obj.isNull("date")) {

                                double amount = obj.getDouble("amount");
                                LocalDate entryDate = LocalDate.parse(obj.getString("date"));

                                // Check if entryDate is within [weekStart, today]
                                if (!entryDate.isBefore(weekStart) && !entryDate.isAfter(weekEnd)) {
                                    weeklyTotal += amount;
                                }
                            }
                        }

                        Log.d("WEEKLY_PROGRESS", "Weekly water intake (Mon to Today): " + weeklyTotal);
                        // Optional: post to LiveData if needed

                    } catch (JSONException e) {
                        Log.e("WEEKLY_PROGRESS", "JSON error", e);
                    }
                } else {
                    Log.w("WEEKLY_PROGRESS", "Non-successful response: " + response.code());
                }
            }
        });
    }
    public Double computeDailyGoal(User user){
        return user.getWeight() * 35;
    }

    public Double computeWeeklyGoal(User user){
        return computeDailyGoal(user) * 7;
    }
    public Double computeMonthlyGoal(User user){
        return computeWeeklyGoal(user) * 4;
    }
}

