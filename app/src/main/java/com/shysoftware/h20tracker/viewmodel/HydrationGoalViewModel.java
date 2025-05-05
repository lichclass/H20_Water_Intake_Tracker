package com.shysoftware.h20tracker.viewmodel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.shysoftware.h20tracker.model.HydrationGoal;
import com.shysoftware.h20tracker.model.User;
import com.shysoftware.h20tracker.model.WeatherData;
import com.shysoftware.h20tracker.repository.HydrationGoalRepository;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDate;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class HydrationGoalViewModel extends ViewModel {

    private final HydrationGoalRepository hydrationGoalRepository = new HydrationGoalRepository();
    private final MutableLiveData<HydrationGoal> todayGoal = new MutableLiveData<>();
    public LiveData<HydrationGoal> getTodayGoal() { return todayGoal; }

    public void setTodayGoal(User user, WeatherData weatherData) {
        LocalDate today = LocalDate.now();
        hydrationGoalRepository.read(user.getUserId(), today, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("HYDRATION GOAL", "Failed to read hydration goal", e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    assert response.body() != null;
                    String responseBody = response.body().string();
                    Log.d("HYDRATION GOAL", "Response received");

                    try {
                        JSONArray jsonArray = new JSONArray(responseBody);

                        if(jsonArray.length() > 0) {
                            Log.d("HYDRATION GOAL", "Goal already exists for today. Parsing...");
                            JSONObject jsonObject = jsonArray.getJSONObject(0);

                            HydrationGoal existingGoal = new HydrationGoal();
                            existingGoal.setUserId(user.getUserId());
                            existingGoal.setTargetAmountMl(jsonObject.getDouble("target_amount_ml"));
                            existingGoal.setForecastDate(LocalDate.parse(jsonObject.getString("forecast_date")));
                            existingGoal.setBasisWeight(jsonObject.getDouble("basis_weight"));
                            existingGoal.setBasisTemp(jsonObject.getDouble("basis_temp"));
                            existingGoal.setBasisHumidity(jsonObject.getInt("basis_humidity"));

                            Log.d("HYDRATION GOAL", "Existing goal parsed: " + existingGoal.getTargetAmountMl() + " ml");
                            todayGoal.postValue(existingGoal);

                        } else {
                            Log.d("HYDRATION GOAL", "No goal found. Computing new goal...");

                            double computedGoal = computeAndStoreGoal(user, weatherData);
                            Log.d("HYDRATION GOAL", "Computed goal: " + computedGoal + " ml");

                            HydrationGoal newGoal = new HydrationGoal();
                            newGoal.setUserId(user.getUserId());
                            newGoal.setTargetAmountMl(computedGoal);
                            newGoal.setForecastDate(weatherData.getDate());
                            newGoal.setBasisWeight(user.getWeight());
                            newGoal.setBasisTemp(weatherData.getTemperatureC());
                            newGoal.setBasisHumidity(weatherData.getHumidityPercent());

                            Log.d("HYDRATION GOAL", "Creating new goal in database...");
                            hydrationGoalRepository.create(newGoal, new Callback() {
                                @Override
                                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                    Log.e("HYDRATION GOAL", "Failed to create new goal", e);
                                }

                                @Override
                                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                    if(response.isSuccessful()){
                                        Log.d("HYDRATION GOAL", "New goal successfully stored.");
                                        todayGoal.postValue(newGoal);
                                    } else {
                                        Log.e("HYDRATION GOAL", "Failed to store goal. Response: " + response);
                                    }
                                }
                            });
                        }

                    } catch (JSONException e) {
                        Log.e("HYDRATION GOAL", "Failed to parse JSON response", e);
                    }

                } else {
                    Log.e("HYDRATION GOAL", "Unsuccessful response: " + response.code());
                }
            }
        });
    }

    public Double computeAndStoreGoal(User user, WeatherData weatherData){
        double result = 0.00;

        double baselineWaterIntake = user.getWeight() * 35;
        result += baselineWaterIntake;

        // Temp adjustment
        if (weatherData.getTemperatureC() > 30.00){
            result += 250.00;
        } else if (weatherData.getTemperatureC() < 10.00) {
            result -= 100.00;
        }

        // Humidity Adjustment
        if (weatherData.getHumidityPercent() >= 80){
            result += 250.00;
        } else if (weatherData.getHumidityPercent() <= 30) {
            result += 100.00;
        }

        if(result < 0){ result = 0.00; }

        return result;
    }
}
