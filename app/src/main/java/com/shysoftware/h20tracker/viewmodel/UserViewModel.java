package com.shysoftware.h20tracker.viewmodel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.shysoftware.h20tracker.model.User;
import com.shysoftware.h20tracker.repository.UserRepository;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

public class  UserViewModel extends ViewModel {
    private final MutableLiveData<List<User>> users = new MutableLiveData<>();
    private final UserRepository repository = new UserRepository();

    private final MutableLiveData<Boolean> updateStatus = new MutableLiveData<>(); //for update status logic, feel free to change this
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

    public Integer registerUser(String email, String password, String confirmPassword){

        // Gate-keep them hoes
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

    public Integer loginUser(String email, String password) {

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
                    // You can save token from responseBody here
                } else {
                    Log.d("SIGN-IN", "Invalid credentials or server error");
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
}


