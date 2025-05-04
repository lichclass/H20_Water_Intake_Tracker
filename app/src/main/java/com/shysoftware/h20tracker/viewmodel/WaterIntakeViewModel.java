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

    public LiveData<Double> getTodayIntake(){ return todayIntake; }

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

}
