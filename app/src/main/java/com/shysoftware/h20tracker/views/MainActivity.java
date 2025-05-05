package com.shysoftware.h20tracker.views;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;


import com.google.android.material.navigation.NavigationView;
import com.shysoftware.h20tracker.R;
import com.shysoftware.h20tracker.model.HydrationGoal;
import com.shysoftware.h20tracker.model.User;
import com.shysoftware.h20tracker.model.WeatherData;
import com.shysoftware.h20tracker.viewmodel.HydrationGoalViewModel;
import com.shysoftware.h20tracker.viewmodel.UserViewModel;
import com.shysoftware.h20tracker.viewmodel.WaterIntakeViewModel;
import com.shysoftware.h20tracker.viewmodel.WeatherDataViewModel;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageButton navMenuBtn;
    private TextView screenName;
    private UserViewModel userViewModel;
    private WeatherDataViewModel weatherDataViewModel;
    private HydrationGoalViewModel hydrationGoalViewModel;
    private WaterIntakeViewModel waterIntakeViewModel;
    private User user;
    private WeatherData weatherData;
    private static final String PREFS_NAME  = "user_prefs";
    private static final String KEY_USER_ID = "user_id";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // |───── Pre-processing Required Data ─────────────────|

        // 0. Pre-requisites
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String userId = prefs.getString(KEY_USER_ID, null);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        weatherDataViewModel = new ViewModelProvider(this).get(WeatherDataViewModel.class);
        hydrationGoalViewModel = new ViewModelProvider(this).get(HydrationGoalViewModel.class);
        waterIntakeViewModel = new ViewModelProvider(this).get(WaterIntakeViewModel.class);

        // 1. User and Weather Data
        if (userId != null) {
            userViewModel.getUser(userId);
        }
        userViewModel.getCurrentUser().observe(this, user -> {
            if(user != null) {
                this.user = user; // Storing User Data
                weatherDataViewModel.setWeatherData(user); // Getting Weather
                waterIntakeViewModel.getProgress(user); // Getting Water Intake Progress
            }
        });
        weatherDataViewModel.getTodayWeather().observe(this, data -> {
            if(data != null) {
                this.weatherData = data;
                hydrationGoalViewModel.setTodayGoal(user, weatherData);
            }
        });



        // |───── For Fragments ─────────────────|
        drawerLayout = findViewById(R.id.main);
        navigationView = findViewById(R.id.nav_view);
        navMenuBtn = findViewById(R.id.nav_menu_btn);
        screenName = findViewById(R.id.screen_name);

        // Set default screen
        replaceFragment(new DashboardFragment(), "Dashboard");

        // Menu button opens drawer
        navMenuBtn.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Handle nav menu clicks
        navigationView.setNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            String title = "";

            int id = item.getItemId();

            if (id == R.id.nav_home) {
                selectedFragment = new DashboardFragment();
                title = "Dashboard";
            } else if (id == R.id.nav_achievements) {
                selectedFragment = new AchievementsFragment();
                title = "Achievements";
            } else if (id == R.id.nav_notifications) {
                selectedFragment = new NotificationsFragment();
                title = "Notifications";
            } else if (id == R.id.nav_history) {
                selectedFragment = new HistoryFragment();
                title = "History";
            } else if (id == R.id.nav_profile) {
                selectedFragment = new ProfileFragment();
                title = "Profile";
            } else if (id == R.id.nav_logout) {
                finish();
                startActivity(new Intent(MainActivity.this, SignInActivity.class));
            }

            if (selectedFragment != null) {
                replaceFragment(selectedFragment, title);
                drawerLayout.closeDrawer(GravityCompat.START);
            }

            return true;
        });

    }

    private void replaceFragment(Fragment fragment, String title) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();

        screenName.setText(title);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.getOnBackPressedDispatcher().onBackPressed();
        }
    }

}
