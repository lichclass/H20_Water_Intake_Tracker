// all existing imports preserved
package com.shysoftware.h20tracker.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.shysoftware.h20tracker.model.Gender;
import com.shysoftware.h20tracker.model.Location;
import com.shysoftware.h20tracker.model.Rank;
import com.shysoftware.h20tracker.model.User;
import com.shysoftware.h20tracker.repository.UserRepository;
import com.shysoftware.h20tracker.views.InputDetailsActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class UserViewModel extends ViewModel {
    private final UserRepository userRepository = new UserRepository();
    private final MutableLiveData<Boolean> updateStatus = new MutableLiveData<>();
    private final MutableLiveData<Boolean> profileExists = new MutableLiveData<>();
    private final MutableLiveData<List<Location>> geocodingResults = new MutableLiveData<>();
    private final MutableLiveData<List<User>> topUsers = new MutableLiveData<>();
    private final MutableLiveData<Double> totalWaterLogged = new MutableLiveData<>();
    private final MutableLiveData<Integer> longestStreak = new MutableLiveData<>();
    private final MutableLiveData<Double> averageDailyIntake = new MutableLiveData<>();

    private static final String PREFS_NAME = "user_prefs";
    private static final String KEY_USER_ID = "user_id";

    public LiveData<List<User>> getTopUsers() { return topUsers; }
    public LiveData<Boolean> getProfileExists() { return profileExists; }
    public LiveData<List<Location>> getGeocodingResults() { return geocodingResults; }
    public LiveData<Boolean> getUpdateStatus() { return updateStatus; }
    public LiveData<Double> getTotalWaterLogged() { return totalWaterLogged; }
    public LiveData<Integer> getLongestStreak() { return longestStreak; }
    public LiveData<Double> getAverageDailyIntake() { return averageDailyIntake; }

    public void registerUser(Context context, String email, String password, String confirmPassword) {
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showToast(context, "Some fields are empty");
            return;
        }
        if (!password.equals(confirmPassword)) {
            showToast(context, "Passwords are not equal");
            return;
        }

        userRepository.signUpUser(email, password, new Callback() {
            @Override public void onFailure(@NonNull Call call, @NonNull IOException e) {
                showToast(context, "Sign-up failed: " + e.getMessage());
            }

            @Override public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String body = response.body() != null ? response.body().string() : "";
                if (response.isSuccessful()) {
                    try {
                        JSONObject root = new JSONObject(body);
                        String newUserId = root.getJSONObject("user").getString("id");

                        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                        prefs.edit().putString(KEY_USER_ID, newUserId).apply();

                        new android.os.Handler(Looper.getMainLooper()).post(() -> {
                            Toast.makeText(context, "Successful Registration!", Toast.LENGTH_SHORT).show();
                            context.startActivity(new Intent(context, InputDetailsActivity.class));
                        });
                    } catch (JSONException e) {
                        showToast(context, "Sign-up response parse error");
                    }
                } else {
                    String toastMsg = "Sign-up failed";
                    try {
                        JSONObject err = new JSONObject(body);
                        toastMsg = "user_already_exists".equals(err.optString("error_code")) ? "Email already exists" : err.optString("msg", toastMsg);
                    } catch (JSONException ignored) {}
                    showToast(context, toastMsg);
                }
            }
        });
    }

    public void loginUser(Context context, String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            showToast(context, "Email and password must not be empty");
            return;
        }

        userRepository.signInUser(email, password, new Callback() {
            @Override public void onFailure(@NonNull Call call, @NonNull IOException e) {
                showToast(context, "Login failed: " + e.getMessage());
            }

            @Override public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String body = response.body() != null ? response.body().string() : "";
                if (response.isSuccessful()) {
                    try {
                        JSONObject userObj = new JSONObject(body).getJSONObject("user");
                        String userId = userObj.getString("id");

                        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                        prefs.edit().putString(KEY_USER_ID, userId).apply();

                        checkProfile(userId);
                    } catch (JSONException e) {
                        showToast(context, "Login response parse error");
                    }
                } else {
                    showToast(context, "Incorrect credentials");
                }
            }
        });
    }

    public Integer completeSignUp(User user) {
        if (user.getUserId() == null || user.getUsername().isEmpty() ||
            user.getAddress() == null || user.getDateOfBirth() == null ||
            user.getLocationLat() == null || user.getLocationLong() == null ||
            user.getHeight() == null || user.getWeight() == null || user.getGender() == null) {
            return 1;
        }

        userRepository.createUserProfile(user, new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                updateStatus.postValue(false);
            }

            @Override public void onResponse(Call call, Response response) {
                updateStatus.postValue(response.isSuccessful());
            }
        });

        return 0;
    }

    public void updateUserProfile(User user) {
        userRepository.updateUserProfile(user, new Callback() {
            @Override public void onFailure(@NonNull Call call, @NonNull IOException e) {
                updateStatus.postValue(false);
            }

            @Override public void onResponse(@NonNull Call call, @NonNull Response response) {
                updateStatus.postValue(response.isSuccessful());
            }
        });
    }

    public void searchAddress(String address) {
        userRepository.forwardGeocode(address, new Callback() {
            @Override public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("FORWARD_GEOCODING", "Network error: ");
            }

            @Override public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        List<Location> suggestions = new ArrayList<>();
                        JSONObject root = new JSONObject(response.body().string());
                        JSONArray features = root.getJSONArray("features");

                        for (int i = 0; i < features.length(); i++) {
                            JSONObject feature = features.getJSONObject(i);
                            String placeName = feature.getString("place_name");
                            JSONArray coords = feature.getJSONObject("geometry").getJSONArray("coordinates");
                            double lon = coords.getDouble(0);
                            double lat = coords.getDouble(1);
                            suggestions.add(new Location(placeName, lon, lat));
                        }

                        new android.os.Handler(Looper.getMainLooper()).post(() -> geocodingResults.setValue(suggestions));
                    } catch (JSONException e) {
                        Log.e("FORWARD_GEOCODING", "JSON parse error", e);
                    }
                }
            }
        });
    }

    public Rank getUserRank(User user) {
        double xp = user.getXp();
        if (xp <= 50) return Rank.DRIPLET;
        else if (xp <= 150) return Rank.SIPPER;
        else if (xp <= 300) return Rank.GULPER;
        else if (xp <= 500) return Rank.HYDRATION_SEEKER;
        else if (xp <= 750) return Rank.WATER_WARRIOR;
        else if (xp <= 1050) return Rank.AQUA_ACHIEVER;
        else if (xp <= 1400) return Rank.HYDRO_HERO;
        else if (xp <= 1800) return Rank.OCEAN_GUARDIAN;
        else if (xp <= 2300) return Rank.LIQUID_LEGEND;
        else return Rank.H2OVERLORD;
    }

    public void checkProfile(String userId) {
        userRepository.isProfileExist(userId, new Callback() {
            @Override public void onFailure(Call call, IOException e) {
                profileExists.postValue(false);
            }

            @Override public void onResponse(Call call, Response response) throws IOException {
                boolean exists = false;
                String body = response.body() != null ? response.body().string() : "<empty>";
                if (response.isSuccessful()) {
                    try {
                        JSONArray arr = new JSONArray(body);
                        exists = arr.length() > 0;
                    } catch (JSONException ignored) {}
                }
                profileExists.postValue(exists);
            }
        });
    }

    public void topUsers() {
        userRepository.fetchTopUsers(new Callback() {
            @Override public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("TOP_USERS", "Failed to fetch: " + e.getMessage());
            }

            @Override public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String body = response.body().string();
                        Type listType = new com.google.gson.reflect.TypeToken<List<User>>(){}.getType();
                        List<User> users = new Gson().fromJson(body, listType);
                        new android.os.Handler(Looper.getMainLooper()).post(() -> topUsers.setValue(users));
                    } catch (Exception e) {
                        Log.e("TOP_USERS", "JSON parse error", e);
                    }
                }
            }
        });
    }

    public void fetchProfileStats(String userId) {
        userRepository.fetchProfileStats(userId, new Callback() {
            @Override public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("PROFILE_STATS", "Fetch failed: " + e.getMessage());
            }

            @Override public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JSONArray arr = new JSONArray(response.body().string());
                        if (arr.length() > 0) {
                            JSONObject stats = arr.getJSONObject(0);
                            double total = stats.optDouble("total_water", 0.0);
                            int streak = stats.optInt("longest_streak", 0);
                            double avg = stats.optDouble("average_daily", 0.0);

                            new android.os.Handler(Looper.getMainLooper()).post(() -> {
                                totalWaterLogged.setValue(total);
                                longestStreak.setValue(streak);
                                averageDailyIntake.setValue(avg);
                            });
                        }
                    } catch (JSONException e) {
                        Log.e("PROFILE_STATS", "JSON parse error", e);
                    }
                }
            }
        });
    }

    private void showToast(Context context, String message) {
        new android.os.Handler(Looper.getMainLooper()).post(() ->
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show());
    }
}
