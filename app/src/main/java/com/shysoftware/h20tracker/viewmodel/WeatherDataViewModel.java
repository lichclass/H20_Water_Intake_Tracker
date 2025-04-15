package com.shysoftware.h20tracker.viewmodel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

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

    public void getForecast(Double lat, Double lon) {
        weatherDataRepository.fetchCurrentWeather(lat, lon, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("WEATHER", "Network error!", e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    String json = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        JSONArray jsonArray = jsonObject.getJSONArray("list");

                        JSONObject loc = jsonObject.getJSONObject("city");
                        String loc_msg = "LOCATION DATA: "
                                + "\n" + loc;
                        Log.d("WEATHER", loc_msg);

                        String targetTime = LocalDate.now().plusDays(1) + " 12:00:00";

                        for(int i = 0; i < jsonArray.length(); i++){
                            JSONObject entry = jsonArray.getJSONObject(i);
                            String dt_txt = entry.getString("dt_txt");

                            if(dt_txt.equals(targetTime)){
                                double temp = entry.getJSONObject("main").getDouble("feels_like");
                                double humidity = entry.getJSONObject("main").getInt("humidity");
                                String weatherCond = entry.getJSONArray("weather").getJSONObject(0).getString("main");
                                String weatherDesc = entry.getJSONArray("weather").getJSONObject(0).getString("description");

                                String msg = "WEATHER TOMORROW 12 NOON: "
                                        + "\nTemp: " + temp
                                        + "\nHumidity: " + humidity
                                        + "\nWeather Cond: " + weatherCond
                                        + "\nWeather Desc: " + weatherDesc;

                                Log.d("WEATHER FORECAST:", msg);

                                break;
                            }
                        }

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                } else {
                    Log.d("WEATHER", "An error has occurred!");
                }
            }
        });

    }
}