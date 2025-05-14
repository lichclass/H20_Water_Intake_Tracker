package com.shysoftware.h20tracker.viewmodel;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.shysoftware.h20tracker.model.Notification;
import com.shysoftware.h20tracker.model.NotifyType;
import com.shysoftware.h20tracker.repository.NotificationRepository;
import com.shysoftware.h20tracker.repository.WaterIntakeRepository;
import com.shysoftware.h20tracker.utils.NotificationHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class NotificationViewModel extends ViewModel {

    private final MutableLiveData<List<Notification>> notifications = new MutableLiveData<>();

    private final NotificationRepository repository = new NotificationRepository();


    public LiveData<List<Notification>> getNotifications() {
        return notifications;
    }

    public void logNotification(Notification notification) {
        repository.create(notification);
    }

    public void getNotification(String userId) {
        repository.read(userId, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("NOTIFICATION", "Failed to read notification", e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    if (response.body() == null) {
                        Log.e("NOTIFICATION", "Empty response body" );
                        return;
                    }
                    String responseBody = response.body().string();

                    List<Notification> notificationList = new ArrayList<>();

                    try {
                        JSONArray array = new JSONArray(responseBody);

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject jsonObject = array.getJSONObject(i);

                            Notification notification1 = new Notification();
                            notification1.setNotificationId(jsonObject.getInt("notification_id" ));
                            notification1.setUserId(jsonObject.getString("user_id" ));
                            notification1.setNotifyType(NotifyType.fromValue(jsonObject.getString("notification_type" )));
                            notification1.setTitle(jsonObject.getString("title" ));
                            notification1.setMessage(jsonObject.getString("message" ));
                            notification1.setSeen(jsonObject.getBoolean("is_seen" ));
                            notification1.setCreatedAt(ZonedDateTime.parse(jsonObject.getString("created_at" )));

                            notificationList.add(notification1);
                        }

                        notifications.postValue(notificationList);

                    } catch (JSONException e) {
                        Log.e("NOTIFICATION", "JSON parsing error", e);
                    }
                }
            }
        });
    }

    private void checkHydrationReminders(String userId, Context ctx) {
        WaterIntakeRepository waterRepo = new WaterIntakeRepository();
        NotificationHelper.createNotificationChannel(ctx);

        waterRepo.getTodayProgress(userId, new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                if (!response.isSuccessful()) return;

                String responseBody = response.body().string();
                JsonArray array = new Gson().fromJson(responseBody, JsonArray.class);

                double totalIntake = 0;

                for (JsonElement element : array) {
                    JsonObject obj = element.getAsJsonObject();
                    totalIntake += obj.get("amount").getAsDouble();
                }

                double goal = 2000;

                int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                if (hour >= 7 && array.isEmpty()) {
                    NotificationHelper.sendNotification(
                            ctx,
                            "Morning Reminder",
                            "Start your day right — drink a glass of water!"
                    );
                }

                if (hour >= 12 && totalIntake < goal * 0.5) {
                    NotificationHelper.sendNotification(
                            ctx,
                            "Hydration Check",
                            "Half the day’s gone — don’t forget to drink water and hit your hydration goal!"
                    );
                }
            }
        });

    }
}



