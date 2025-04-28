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

import com.shysoftware.h20tracker.model.Rank;
import com.shysoftware.h20tracker.model.User;
import com.shysoftware.h20tracker.repository.UserRepository;
import com.shysoftware.h20tracker.views.SignInActivity;
import com.shysoftware.h20tracker.views.TestActivity;

import java.io.IOException;
import java.util.List;
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


