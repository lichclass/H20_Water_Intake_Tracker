package com.shysoftware.h20tracker.views;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
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

import java.time.format.DateTimeFormatter;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME  = "user_prefs";
    private static final String KEY_USER_ID = "user_id";

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageButton navMenuBtn;
    private TextView screenName, headerUsernameTxt, headerCreatedYrTxt;

    private UserViewModel userViewModel;
    private WeatherDataViewModel weatherDataViewModel;
    private HydrationGoalViewModel hydrationGoalViewModel;
    private WaterIntakeViewModel waterIntakeViewModel;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Notification Permission Check
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (MainActivity.this.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1001);
            }
        }

        // 1) Bind ALL your UI references up front:
        initViews();

        // 2) Now set up your ViewModels
        initVM();

        // 3) Read prefs & kick off initial data load
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String userId = prefs.getString(KEY_USER_ID, null);
        if (userId != null) {
            userViewModel.getUser(userId);
        }

        // 4) Observe your user → update UI & load dependent data
        userViewModel.getCurrentUser().observe(this, u -> {
            if (u != null) {
                this.user = u;
                setupObservers(u);
            }
        });


        // 6) Set default screen on launch
        replaceFragment(new DashboardFragment(), "Dashboard");

        // 7) Menu button opens the drawer
        navMenuBtn.setOnClickListener(v ->
                drawerLayout.openDrawer(GravityCompat.START)
        );

        // 8) Handle nav menu item clicks
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            Fragment selectedFragment;
            String title;

            if (id == R.id.nav_home) {
                selectedFragment = new DashboardFragment();
                title = "Dashboard";
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
                startActivity(new Intent(this, SignInActivity.class));
                return true;
            } else {
                return false;  // unhandled
            }

            replaceFragment(selectedFragment, title);
            drawerLayout.closeDrawer(GravityCompat.START);
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

    private void initViews(){
        drawerLayout            = findViewById(R.id.main);
        navigationView          = findViewById(R.id.nav_view);
        View header             = navigationView.getHeaderView(0);
        navMenuBtn              = findViewById(R.id.nav_menu_btn);
        screenName              = findViewById(R.id.screen_name);
        headerUsernameTxt       = header.findViewById(R.id.header_username);
        headerCreatedYrTxt      = header.findViewById(R.id.header_created_at);
    }

    private void initVM(){
        userViewModel           = new ViewModelProvider(this).get(UserViewModel.class);
        weatherDataViewModel    = new ViewModelProvider(this).get(WeatherDataViewModel.class);
        hydrationGoalViewModel  = new ViewModelProvider(this).get(HydrationGoalViewModel.class);
        waterIntakeViewModel    = new ViewModelProvider(this).get(WaterIntakeViewModel.class);
    }

    private void setupObservers(User u){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");

        headerUsernameTxt.setText(u.getUsername());
        headerCreatedYrTxt.setText("Hydrated Since " + u.getCreatedAt().format(formatter));
        weatherDataViewModel.setWeatherData(u);
        waterIntakeViewModel.getProgress(u);

        weatherDataViewModel.getTodayWeather().observe(this, w -> {
            if (w != null) {
                hydrationGoalViewModel.setTodayGoal(user, w);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1001) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("PERMISSION", "Notification permission granted.");
            } else {
                Log.w("PERMISSION", "Notification permission denied.");
            }
        }
    }
}
