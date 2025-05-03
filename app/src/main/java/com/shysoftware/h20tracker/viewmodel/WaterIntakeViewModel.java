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


import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
public class WaterIntakeViewModel extends ViewModel {
    private final WaterIntakeRepository waterIntakeRepository = new WaterIntakeRepository();
    private final MutableLiveData<Double> todayIntake = new MutableLiveData<>();
    public LiveData<Double> getProgress(User user) {
        waterIntakeRepository.getTodayProgress(user, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("PROGRESS", "Failed to fetch intake: " + e.getMessage());
                todayIntake.postValue(-1.0); // Error signal
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String body = response.body().string();

                    try {
                        JSONArray array = new JSONArray(body);
                        double totalIntake = 0.0;

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            totalIntake += obj.optDouble("amount", 0.0);
                        }

                        todayIntake.postValue(totalIntake);

                    } catch (JSONException e) {
                        Log.e("HYDRATION_PROGRESS", "JSON parsing error", e);
                        todayIntake.postValue(-1.0); // Optionally indicate parsing error
                    }
                } else {
                    Log.w("HYDRATION_PROGRESS", "Non-successful response: " + response.code());
                    todayIntake.postValue(-1.0);
                }
            }
        });

        return todayIntake;
    }
}
