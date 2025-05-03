package com.shysoftware.h20tracker.views;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;
import com.shysoftware.h20tracker.R;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageButton navMenuBtn;
    private TextView screenName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
