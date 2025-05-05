package com.shysoftware.h20tracker.viewmodel;

import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.shysoftware.h20tracker.model.User;
import com.shysoftware.h20tracker.model.WeatherCondition;
import com.shysoftware.h20tracker.model.WeatherData;
import com.shysoftware.h20tracker.repository.WeatherDataRepository;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDate;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherDataViewModel extends ViewModel {

    private final WeatherDataRepository weatherDataRepository = new WeatherDataRepository();
    private final MutableLiveData<WeatherData> todayWeather = new MutableLiveData<>();
    public LiveData<WeatherData> getTodayWeather() { return todayWeather; }


    public void setWeatherData(User user) {
        weatherDataRepository.read(user.getUserId(), LocalDate.now(), new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("SET WEATHER DATA", "Network error!", e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()) {
                    assert response.body() != null;
                    String responseBody = response.body().string();

                    try {
                        JSONArray jsonArray = new JSONArray(responseBody);

                        if (jsonArray.length() > 0) {
                            Log.d("SET WEATHER DATA", "Weather data already exists for today.");

                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            WeatherData weatherData = new WeatherData();

                            weatherData.setWeatherId(jsonObject.getInt("weather_id"));
                            weatherData.setUserId(jsonObject.getString("user_id"));
                            weatherData.setTemperatureC(jsonObject.getDouble("temperature_c"));
                            weatherData.setHumidityPercent(jsonObject.getInt("humidity_percent"));
                            weatherData.setWeatherCondition(WeatherCondition.fromValue(jsonObject.getString("weather_condition")));
                            weatherData.setDate(LocalDate.parse(jsonObject.getString("date")));

                            todayWeather.postValue(weatherData);

                        } else {
                            Log.d("SET WEATHER DATA", "No weather data found for today.");
                            getForecast(user);
                        }

                    } catch (JSONException e) {
                        Log.e("SET WEATHER DATA", "Failed to parse JSON", e);
                    }

                } else {
                    Log.e("SET WEATHER DATA", "An error has occurred!");
                }
            }
        });
    }

    public void getForecast(User user) {
        weatherDataRepository.fetchCurrentForecast(user.getLocationLat(), user.getLocationLong(), new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("WEATHER", "Network error!", e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    String json = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        JSONArray jsonArray = jsonObject.getJSONArray("list");

                        String targetTime = LocalDate.now() + " 12:00:00";

                        for(int i = 0; i < jsonArray.length(); i++){
                            JSONObject entry = jsonArray.getJSONObject(i);
                            String dt_txt = entry.getString("dt_txt");

                            if(dt_txt.equals(targetTime)){
                                LocalDate dt = LocalDate.parse(dt_txt.split(" ")[0]);
                                double temp = entry.getJSONObject("main").getDouble("feels_like");
                                Integer humidity = entry.getJSONObject("main").getInt("humidity");
                                String weatherCond = entry.getJSONArray("weather").getJSONObject(0).getString("main").toLowerCase();

                                WeatherData weatherData = new WeatherData();

                                weatherData.setUserId(user.getUserId());
                                weatherData.setTemperatureC(temp);
                                weatherData.setHumidityPercent(humidity);
                                weatherData.setWeatherCondition(WeatherCondition.fromValue(weatherCond));
                                weatherData.setDate(dt);

                                weatherDataRepository.create(weatherData, new Callback() {
                                    @Override
                                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                                        Log.e("CREATE WEATHER", "Failed to create weather data", e);
                                    }

                                    @Override
                                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                                        if (response.isSuccessful()) {
                                            Log.d("CREATE WEATHER", "Weather data created successfully");
                                            todayWeather.postValue(weatherData);
                                        } else {
                                            Log.e("CREATE WEATHER", "Failed to store weather data: " + response);
                                        }
                                    }
                                });

                                break;
                            }
                        }

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    Log.e("WEATHER", "An error has occurred!: " + response);
                }
            }
        });

    }
}