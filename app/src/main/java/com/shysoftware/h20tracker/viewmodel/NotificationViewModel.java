package com.shysoftware.h20tracker.viewmodel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.shysoftware.h20tracker.model.Notification;
import com.shysoftware.h20tracker.model.NotifyType;
import com.shysoftware.h20tracker.repository.NotificationRepository;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
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
                if(response.isSuccessful()){
                    if (response.body() == null) {
                        Log.e("NOTIFICATION", "Empty response body");
                        return;
                    }
                    String responseBody = response.body().string();

                    List<Notification> notificationList = new ArrayList<>();

                    try {
                        JSONArray array = new JSONArray(responseBody);

                        for(int i = 0; i < array.length(); i++){
                            JSONObject jsonObject = array.getJSONObject(i);

                            Notification notification1 = new Notification();
                            notification1.setNotificationId(jsonObject.getInt("notification_id"));
                            notification1.setUserId(jsonObject.getString("user_id"));
                            notification1.setNotifyType(NotifyType.fromValue(jsonObject.getString("notification_type")));
                            notification1.setTitle(jsonObject.getString("title"));
                            notification1.setMessage(jsonObject.getString("message"));
                            notification1.setSeen(jsonObject.getBoolean("is_seen"));
                            notification1.setCreatedAt(ZonedDateTime.parse(jsonObject.getString("created_at")));

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
}
