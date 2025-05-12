package com.shysoftware.h20tracker.viewmodel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.shysoftware.h20tracker.model.User;
import com.shysoftware.h20tracker.model.WaterIntake;
import com.shysoftware.h20tracker.repository.WaterIntakeRepository;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
public class WaterIntakeViewModel extends ViewModel {
    private final WaterIntakeRepository waterIntakeRepository = new WaterIntakeRepository();
    private final MutableLiveData<Double> todayIntake = new MutableLiveData<>();
    private final MutableLiveData<Double> weeklyProgress = new MutableLiveData<>();
    private final MutableLiveData<Boolean> intakeLogged = new MutableLiveData<>();
    private final MutableLiveData<Boolean> deleteSuccess = new MutableLiveData<>();
    private final MutableLiveData<Double> monthlyProgress = new MutableLiveData<>();
    private final MutableLiveData<ArrayList<WaterIntake>> intakeList = new MutableLiveData<>();
    private final MutableLiveData<Double> totalWaterIntake = new MutableLiveData<>();
    private final MutableLiveData<LinkedHashMap<LocalDate, Double>> dailyGroupedIntake = new MutableLiveData<>();
    private final MutableLiveData<Double> weeklyAverageIntake = new MutableLiveData<>();

    public LiveData<Boolean> getDeleteStatus() { return deleteSuccess; }
    public LiveData<Double> getTodayIntake() { return todayIntake; }
    public LiveData<Double> getWeeklyProgress() { return weeklyProgress; }
    public LiveData<Double> getMonthlyProgress() { return monthlyProgress; }
    public LiveData<ArrayList<WaterIntake>> getIntakeList() { return intakeList; }
    public LiveData<Double> getTotalWaterIntake() { return totalWaterIntake; }
    public LiveData<LinkedHashMap<LocalDate, Double>> getDailyGroupedIntake() { return dailyGroupedIntake; }
    public LiveData<Double> getWeeklyAverageIntake() { return weeklyAverageIntake; }


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

    public void logWater(User user, double amount) {
        waterIntakeRepository.logWaterIntake(user.getUserId(), amount, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("LOG_WATER", "Failed to log intake: " + e.getMessage());

                intakeLogged.postValue(false);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                if(response.isSuccessful()){
                    getProgress(user);
                    setMonthlyProgress(user, LocalDate.now());
                    setWeeklyProgress(user, LocalDate.now());
                    setWaterIntakeList(user);
                } else {
                    intakeLogged.postValue(false);
                }
            }
        });
    }

    /**
     * Delete Intake Entry
     *
     * @param waterIntake
     */
    public void deleteIntakeEntry(WaterIntake waterIntake, User currentUser) {
        waterIntakeRepository.deleteIntakeEntry(waterIntake.getIntakeId(), new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("DELETE_ENTRY", "Failed to delete: " + e.getMessage());
                deleteSuccess.postValue(false);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                boolean ok = response.isSuccessful();
                deleteSuccess.postValue(ok);
                if(ok){
                    setWaterIntakeList(currentUser);
                }
            }
        });
    }


    public void setWeeklyProgress(User user, LocalDate today){
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
                        LocalDate weekEnd = today;

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

                        weeklyProgress.postValue(weeklyTotal);
                    } catch (JSONException e) {
                        Log.e("WEEKLY_PROGRESS", "JSON error", e);
                    }
                } else {
                    Log.w("WEEKLY_PROGRESS", "Non-successful response: " + response.code());
                }
            }
        });
    }


    public void setMonthlyProgress(User user, LocalDate today) {
        waterIntakeRepository.read(user.getUserId(), new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("MONTHLY_PROGRESS", "Failed to fetch intake: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String body = response.body().string();
                    double monthlyTotal = 0.00;

                    try {
                        JSONArray array = new JSONArray(body);

                        LocalDate monthStart = today.withDayOfMonth(1);
                        LocalDate monthEnd = today;

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);

                            if (obj.has("amount") && !obj.isNull("amount") &&
                                    obj.has("date") && !obj.isNull("date")) {

                                double amount = obj.getDouble("amount");
                                LocalDate entryDate = LocalDate.parse(obj.getString("date"));

                                if (!entryDate.isBefore(monthStart) && !entryDate.isAfter(monthEnd)) {
                                    monthlyTotal += amount;
                                }
                            }
                        }

                        monthlyProgress.postValue(monthlyTotal);
                    } catch (JSONException e) {
                        Log.e("MONTHLY_PROGRESS", "JSON error", e);
                    }
                } else {
                    Log.w("MONTHLY_PROGRESS", "Non-successful response: " + response.code());
                }
            }
        });
    }


    public void setWaterIntakeList(User user) {
        waterIntakeRepository.read(user.getUserId(), new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("WATER INTAKE LIST", "Failed to fetch intake list: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String body = response.body().string();
                    ArrayList<WaterIntake> intakes = new ArrayList<>();
                    Double totalAmount = 0.0;

                    try {
                        JSONArray jsonArray = new JSONArray(body);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);

                            WaterIntake waterIntake = new WaterIntake();
                            waterIntake.setIntakeId(obj.getInt("intake_id"));
                            waterIntake.setUserId(obj.getString("user_id"));
                            waterIntake.setAmount(obj.getDouble("amount"));
                            waterIntake.setCreatedAt(ZonedDateTime.parse(obj.getString("created_at")));
                            waterIntake.setUpdatedAt(ZonedDateTime.parse(obj.getString("updated_at")));
                            waterIntake.setDate(LocalDate.parse(obj.getString("date")));

                            totalAmount += waterIntake.getAmount();
                            intakes.add(waterIntake);
                        }

                        totalWaterIntake.postValue(totalAmount);
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                    intakeList.postValue(intakes);
                }
            }
        });
    }

    public void getDailyIntakeGrouped(User user) {
        waterIntakeRepository.read(user.getUserId(), new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("GROUPED_DAILY", "Failed to fetch intake list: " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String body = response.body().string();
                    LinkedHashMap<LocalDate, Double> grouped = new LinkedHashMap<>();

                    try {
                        JSONArray jsonArray = new JSONArray(body);
                        LocalDate earliest = LocalDate.now(); // temp earliest date
                        LocalDate today = LocalDate.now();

                        // Step 1: Group by date
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);

                            if (!obj.has("date") || obj.isNull("date")) continue;

                            LocalDate date = LocalDate.parse(obj.getString("date"));
                            double amount = obj.optDouble("amount", 0.0);

                            if (date.isBefore(earliest)) earliest = date;

                            grouped.put(date, grouped.getOrDefault(date, 0.0) + amount);
                        }

                        // Step 2: Fill missing dates with 0
                        LinkedHashMap<LocalDate, Double> filled = new LinkedHashMap<>();
                        LocalDate date = earliest;

                        while (!date.isAfter(today)) {
                            filled.put(date, grouped.getOrDefault(date, 0.0));
                            date = date.plusDays(1);
                        }

                        dailyGroupedIntake.postValue(filled);

                    } catch (JSONException e) {
                        Log.e("GROUPED_DAILY", "JSON parse error", e);
                    }
                }
            }
        });
    }


    public void computeWeeklyAverage(User user) {
        waterIntakeRepository.read(user.getUserId(), new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("WEEKLY_AVG", "Failed to fetch intake list: " + e.getMessage());
                weeklyAverageIntake.postValue(0.0);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful() || response.body() == null) {
                    weeklyAverageIntake.postValue(0.0);
                    return;
                }

                String body = response.body().string();
                try {
                    JSONArray arr = new JSONArray(body);

                    // 1) Build a map date → total amount that day
                    Map<LocalDate, Double> grouped = new LinkedHashMap<>();
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject obj = arr.getJSONObject(i);
                        LocalDate date = LocalDate.parse(obj.getString("date"));
                        double amt = obj.optDouble("amount", 0.0);
                        grouped.put(date, grouped.getOrDefault(date, 0.0) + amt);
                    }

                    // 2) Walk the past 7 days, summing and counting non-zero days
                    LocalDate today = LocalDate.now();
                    LocalDate start = today.minusDays(6);
                    double sum = 0.0;
                    int nonZeroDays = 0;

                    for (LocalDate d = start; !d.isAfter(today); d = d.plusDays(1)) {
                        double v = grouped.getOrDefault(d, 0.0);
                        sum += v;
                        if (v > 0) nonZeroDays++;
                    }

                    // 3) Compute average over only days with intake
                    double avg = (nonZeroDays > 0) ? (sum / nonZeroDays) : 0.0;
                    weeklyAverageIntake.postValue(avg);

                } catch (JSONException e) {
                    Log.e("WEEKLY_AVG", "JSON parse error", e);
                    weeklyAverageIntake.postValue(0.0);
                }
            }
        });
    }


    // UTILITY FUNCTIONS
    public Double computeDailyGoal(User user){
        return user.getWeight() * 35;
    }

    public Double computeWeeklyGoal(User user){
        return computeDailyGoal(user) * 7;
    }
    public Double computeMonthlyGoal(User user) {
        LocalDate today = LocalDate.now();
        int daysInMonth = YearMonth.from(today).lengthOfMonth();
        return computeDailyGoal(user) * daysInMonth;
    }

}

