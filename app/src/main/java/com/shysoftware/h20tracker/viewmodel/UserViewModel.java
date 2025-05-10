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

import com.shysoftware.h20tracker.model.AuthUser;
import com.shysoftware.h20tracker.model.Gender;
import com.shysoftware.h20tracker.model.Location;
import com.shysoftware.h20tracker.model.User;
import com.shysoftware.h20tracker.repository.UserRepository;
import com.shysoftware.h20tracker.views.InputDetailsActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class  UserViewModel extends ViewModel {
    private final UserRepository userRepository = new UserRepository();

    private final MutableLiveData<Boolean> updateStatus = new MutableLiveData<>();
    private final MutableLiveData<Boolean> profileExists = new MutableLiveData<>();
    private final MutableLiveData<List<Location>> geocodingResults = new MutableLiveData<>();
    private final MutableLiveData<User> currentUser = new MutableLiveData<>();
    private final MutableLiveData<AuthUser> authUser = new MutableLiveData<>();


    private static final String PREFS_NAME    = "user_prefs";
    private static final String KEY_USER_ID   = "user_id";


    public LiveData<Boolean> getProfileExists() {
        return profileExists;
    }
    public LiveData<List<Location>> getGeocodingResults() {
        return geocodingResults;
    }
    public LiveData<Boolean> getUpdateStatus() {
        return updateStatus;
    }
    public LiveData<User> getCurrentUser(){ return currentUser; }
    public LiveData<AuthUser> getAuthUser(){ return authUser; }

    /**
     * Register user, but Email and Password only (For Auth)
     *
     * @param email
     * @param password
     * @param confirmPassword
     * @return
     */
    public void registerUser(Context context, String email, String password, String confirmPassword) {
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            new android.os.Handler(Looper.getMainLooper()).post(() ->
                    Toast.makeText(context, "Some fields are empty", Toast.LENGTH_SHORT).show()
            );
            return;
        }
        if (!password.equals(confirmPassword)) {
            new android.os.Handler(Looper.getMainLooper()).post(() ->
                    Toast.makeText(context, "Passwords are not equal", Toast.LENGTH_SHORT).show()
            );
            return;
        }

        userRepository.signUpUser(email, password, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                new android.os.Handler(Looper.getMainLooper()).post(() ->
                        Toast.makeText(context, "Sign-up failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String body = response.body() != null ? response.body().string() : "";
                if (response.isSuccessful()) {
                    try {
                        JSONObject root = new JSONObject(body);
                        String newUserId = root.getJSONObject("user").getString("id");

                        // save userId
                        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                        prefs.edit().putString(KEY_USER_ID, newUserId).apply();

                        new android.os.Handler(Looper.getMainLooper()).post(() -> {
                            Toast.makeText(context, "Successful Registration!", Toast.LENGTH_SHORT).show();
                            context.startActivity(new Intent(context, InputDetailsActivity.class));
                        });
                    } catch (JSONException e) {
                        new android.os.Handler(Looper.getMainLooper()).post(() ->
                                Toast.makeText(context, "Sign-up response parse error", Toast.LENGTH_SHORT).show()
                        );
                    }
                } else {
                    String toastMsg = "Sign-up failed";
                    try {
                        JSONObject err = new JSONObject(body);
                        if ("user_already_exists".equals(err.optString("error_code"))) {
                            toastMsg = "Email already exists";
                        } else {
                            toastMsg = err.optString("msg", toastMsg);
                        }
                    } catch (JSONException ignored) { }
                    String finalMsg = toastMsg;
                    new android.os.Handler(Looper.getMainLooper()).post(() ->
                            Toast.makeText(context, finalMsg, Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }

    /**
     * Sign's in the user
     *
     * @param email
     * @param password
     */
    public void loginUser(Context context, String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            new android.os.Handler(Looper.getMainLooper()).post(() ->
                    Toast.makeText(context,
                            "Email and password must not be empty",
                            Toast.LENGTH_SHORT).show()
            );
            return;
        }

        userRepository.signInUser(email, password, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                new android.os.Handler(Looper.getMainLooper()).post(() ->
                        Toast.makeText(context,
                                "Login failed: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String body = response.body() != null ? response.body().string() : "";
                if (response.isSuccessful()) {
                    try {
                        JSONObject root    = new JSONObject(body);
                        JSONObject userObj = root.getJSONObject("user");
                        String userId      = userObj.getString("id");

                        // save userId
                        SharedPreferences prefs = context
                                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                        prefs.edit()
                                .putString(KEY_USER_ID, userId)
                                .apply();

                        // then check if profile exists
                        checkProfile(userId);
                    } catch (JSONException e) {
                        new android.os.Handler(Looper.getMainLooper()).post(() ->
                                Toast.makeText(context,
                                        "Login response parse error",
                                        Toast.LENGTH_SHORT).show()
                        );
                    }
                } else {
                    new android.os.Handler(Looper.getMainLooper()).post(() ->
                            Toast.makeText(context,
                                    "Incorrect credentials",
                                    Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }

    /**
     * Completes the Registration of the user
     *
     * @param user
     * @return
     */
    public Integer completeSignUp(User user) {

        // Validate fields
        if (    user.getUserId()                == null ||
                user.getUsername().isEmpty()            ||
                user.getAddress()               == null ||
                user.getDateOfBirth()           == null ||
                user.getLocationLat()           == null ||
                user.getLocationLong()          == null ||
                user.getHeight()                == null ||
                user.getWeight()                == null ||
                user.getGender()                == null )
        {
            return 1;
        }

        // Save additional profile info to Supabase
        userRepository.createUserProfile(user, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                updateStatus.postValue(false);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String resp = response.body() != null ? response.body().string() : "<empty>";
                Log.d("PROFILE-CREATE", "HTTP " + response.code() + " → " + resp);
                updateStatus.postValue(response.isSuccessful());
            }
        });

        return 0;
    }


    public void updateUserProfile(User user) { //unsure if works
        userRepository.updateUserProfile(user, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                updateStatus.postValue(false);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                updateStatus.postValue(response.isSuccessful());
            }
        });
    }


    /**
     * Given a string, it searches for matching addresses via Forward geocoding
     *
     * @param address
     */
    public void searchAddress(String address) {
        userRepository.forwardGeocode(address, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("FORWARD_GEOCODING", "Network error: ");
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    List<Location> suggestions = new ArrayList<>();
                    try {
                        String json = response.body().string();
                        JSONObject root = new JSONObject(json);
                        JSONArray features = root.getJSONArray("features");

                        for (int i = 0; i < features.length(); i++) {
                            JSONObject feature = features.getJSONObject(i);
                            String placeName = feature.getString("place_name");

                            // extract coords
                            JSONArray coords = feature
                                    .getJSONObject("geometry")
                                    .getJSONArray("coordinates");
                            double lon = coords.getDouble(0);
                            double lat = coords.getDouble(1);

                            Location location = new Location(placeName, lon, lat);

                            suggestions.add(location);
                        }

                        // Post back on the main thread
                        new android.os.Handler(Looper.getMainLooper()).post(() -> {
                            geocodingResults.setValue(suggestions);
                        });

                    } catch (JSONException e) {
                        Log.e("FORWARD_GEOCODING", "JSON parse error", e);
                    }
                }
            }
        });
    }

    /**
     * Checks the db if a users data already exists (the details, not the auth)
     *
     * @param userId
     */
    public void checkProfile(String userId) {
        userRepository.isProfileExist(userId, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                profileExists.postValue(false);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String body = response.body() != null ? response.body().string() : "<empty>";
                boolean exists = false;
                if (response.isSuccessful()) {
                    try {
                        JSONArray arr = new JSONArray(body);
                        exists = arr.length() > 0;
                    } catch (JSONException e) {
                        //
                    }
                }
                profileExists.postValue(exists);
            }
        });
    }


    public void getUser(String userId){
        userRepository.fetchUser(userId, new Callback() {

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    assert response.body() != null;
                    String responseBody = response.body().string();

                    try {
                        JSONArray data = new JSONArray(responseBody);
                        if (data.length() > 0) {
                            JSONObject userJson = data.getJSONObject(0);
                            User user = new User();

                            user.setUserId(userJson.getString("user_id"));
                            user.setUsername(userJson.getString("username"));
                            user.setLocationLat(userJson.getDouble("location_lat"));
                            user.setLocationLong(userJson.getDouble("location_long"));
                            user.setAddress(userJson.getString("address"));
                            user.setDateOfBirth(LocalDate.parse(userJson.getString("date_of_birth").substring(0, 10)));
                            user.setHeight(userJson.getDouble("height"));
                            user.setWeight(userJson.getDouble("weight"));
                            user.setGender(Gender.fromValue(userJson.getString("gender")));

                            new android.os.Handler(Looper.getMainLooper()).post(() -> {
                                currentUser.setValue(user);
                            });
                        }
                    } catch (JSONException e) {
                        Log.e("GET_USER", "JSON parse error", e);
                    }
                } else {
                    Log.e("GET_USER", "Failed with code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("GET_USER", "Fetch failed: " + e.getMessage());
            }
        });
    }

}


