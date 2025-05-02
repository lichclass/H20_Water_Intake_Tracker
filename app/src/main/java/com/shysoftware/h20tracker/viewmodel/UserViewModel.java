package com.shysoftware.h20tracker.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.shysoftware.h20tracker.model.Gender;
import com.shysoftware.h20tracker.model.Location;
import com.shysoftware.h20tracker.model.Rank;
import com.shysoftware.h20tracker.model.User;
import com.shysoftware.h20tracker.repository.UserRepository;
import com.shysoftware.h20tracker.views.SignInActivity;
import com.shysoftware.h20tracker.views.TestActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.logging.Handler;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class  UserViewModel extends ViewModel {
    private final MutableLiveData<List<User>> users = new MutableLiveData<>();
    private final UserRepository repository = new UserRepository();
    private final MutableLiveData<Boolean> updateStatus = new MutableLiveData<>();
    public LiveData<Boolean> getUpdateStatus() {
        return updateStatus;
    }
    public LiveData<List<User>> getUsers() {
        return users;
    }
    private final MutableLiveData<List<Location>> geocodingResults = new MutableLiveData<>();

    public void loadUsers() {
        repository.fetchUsers(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("USER_FETCH", "Network error", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    String json = response.body().string();
                    Log.d("USER_RAW_JSON", json); // 👈 Add this line
                    List<User> userList = repository.parseUserResponse(json);
                    users.postValue(userList);
                }
            }
        });
    }


    /**
     * Register User (Auth, no other personal data)
     * @param email
     * @param password
     * @param confirmPassword
     * @return
     */
    public Integer registerUser(String email, String password, String confirmPassword){

        if(email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()){
            return 1;
        }

        if(!password.equals(confirmPassword)){
            return 2;
        }

        repository.signUpUser(email, password, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("SIGN-UP", "Sign-up failed!", e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    assert response.body() != null;
                    String responseBody = response.body().string();
                    Log.d("SIGN-UP", "Success: " + responseBody);
                } else {
                    Log.d("SIGN-UP", "An error has occurred!");
                }
            }
        });

        return 0;
    }


    /**
     * Login User Function
     * @param email
     * @param password
     * @return
     */
    public Integer loginUser(String email, String password, Context context) {

        if (email.isEmpty() || password.isEmpty()) {
            return 1; // Fields empty
        }

        repository.signInUser(email, password, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("SIGN-IN", "Login failed!", e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    String responseBody = response.body().string();
                    Log.d("SIGN-IN", "Success: " + responseBody);

                    new android.os.Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            context.startActivity(new Intent(context, TestActivity.class));
                        }
                    });

                    // Code for saving the tokens in cache (token is in response body)

                } else {
                    new android.os.Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "Incorrect Credentials", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });

        return 0;
    }

    // To be Tested
    public Integer completeSignUp(
            String userId,
            String username,
            Double latitude,
            Double longitude,
            String address,
            LocalDate dateOfBirth,
            Double height,
            Double weight,
            Gender gender
    ) {
        // Validate fields
        if (    username.isEmpty() || latitude == null || longitude == null ||
                address.isEmpty() || dateOfBirth == null || height == null ||
                weight == null || gender == null) {
            return 1; // naay kuwang or Fields or letters missing
        }

        User user = new User(
                null, // UserID  added
                username,
                latitude,
                longitude,
                address,
                dateOfBirth,
                height,
                weight,
                gender,
                0,      // initial streaks
                0.0,    // initial XPs
                Rank.DRIPLET, // default rank thinies
                null    // updatedAt handled by backends
        );

        // Save additional profile info to Supabase
        repository.createUserProfile(user, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("PROFILE-CREATE", "Failed to create profile", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d("PROFILE-CREATE", "Profile created successfully");
                } else {
                    Log.d("PROFILE-CREATE", "Failed to create profile: " + response.message());
                }
            }
        });

        return 0;
    }


    public void searchAddress(String address) {
        repository.forwardGeocode(address, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("FORWARD_GEOCODING",
                        "Network error: " + e.getClass().getSimpleName() + " – " + e.getMessage(),
                        e);
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

                        Log.d("GEOCODE_SUGGESTIONS", suggestions.toString());

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

    public LiveData<List<Location>> getGeocodingResults() {
        return geocodingResults;
    }


    public void updateUserProfile(User user) { //unsure if works
        repository.updateUserProfile(user, new Callback() {
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
     * Get User Rank
     * @param user
     * @return
     */
    public Rank getUserRank(User user){
        Double xp = user.getXp();
        Rank rank;

        if(xp <= 50)            rank = Rank.DRIPLET;
        else if (xp <= 150)     rank = Rank.SIPPER;
        else if (xp <= 300)     rank = Rank.GULPER;
        else if (xp <= 500)     rank = Rank.HYDRATION_SEEKER;
        else if (xp <= 750)     rank = Rank.WATER_WARRIOR;
        else if (xp <= 1050)    rank = Rank.AQUA_ACHIEVER;
        else if (xp <= 1400)    rank = Rank.HYDRO_HERO;
        else if (xp <= 1800)    rank = Rank.OCEAN_GUARDIAN;
        else if (xp <= 2300)    rank = Rank.LIQUID_LEGEND;
        else                    rank = Rank.H2OVERLORD;

        return rank;
    }
}


