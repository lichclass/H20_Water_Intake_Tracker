package com.shysoftware.h20tracker.views;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.shysoftware.h20tracker.R;
import com.shysoftware.h20tracker.model.Gender;
import com.shysoftware.h20tracker.model.Location;
import com.shysoftware.h20tracker.model.Notification;
import com.shysoftware.h20tracker.model.User;
import com.shysoftware.h20tracker.utils.NotificationHelper;
import com.shysoftware.h20tracker.viewmodel.NotificationViewModel;
import com.shysoftware.h20tracker.viewmodel.UserViewModel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class InputDetailsActivity extends AppCompatActivity {

    private EditText usernameTxt, heightTxt, weightTxt, dateOfBirthTxt;
    private AutoCompleteTextView addressDropdown;
    private Spinner genderSpinner;


    private ArrayAdapter<Location> locationArrayAdapter;
    private UserViewModel userViewModel;


    private final String[] genders = {
            "Select Gender",
            "Male",
            "Female",
            "Other"
    };

    private final User userData = new User();
    Button signUpBtn;
    private static final String PREFS_NAME  = "user_prefs";
    private static final String KEY_USER_ID = "user_id";

    NotificationViewModel notifViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_details);

        usernameTxt      = findViewById(R.id.usernameEditText);
        heightTxt        = findViewById(R.id.heightEditText);
        weightTxt        = findViewById(R.id.weightEditText);
        dateOfBirthTxt   = findViewById(R.id.dobTextView);
        addressDropdown  = findViewById(R.id.addressDropdown);
        genderSpinner    = findViewById(R.id.genderSpinner);
        signUpBtn        = findViewById(R.id.signUpButton);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        notifViewModel = new ViewModelProvider(this).get(NotificationViewModel.class);

        userViewModel.getUpdateStatus().observe(this, success -> {
            if (success != null && success) {
                Toast.makeText(this, "Profile created!", Toast.LENGTH_SHORT).show();

                // Phone Notification -------------------
                NotificationHelper.createNotificationChannel(this); // Ensure channel exists
                NotificationHelper.sendNotification(
                        this,
                        "Welcome, " + userData.getUsername() + "!",
                        "Your profile is now set up. Stay hydrated and track your water intake daily!"
                );
                // -------------------

                // Save To Database -------------------
                Notification welcomeNotif = new Notification(
                        null,
                        userData.getUserId(),
                        com.shysoftware.h20tracker.model.NotifyType.SYSTEM,
                        "Welcome, " + userData.getUsername() + "!",
                        "Your profile is now set up. Stay hydrated and track your water intake daily!",
                        false,
                        java.time.ZonedDateTime.now(),
                        java.time.ZonedDateTime.now()
                );
                notifViewModel.logNotification(welcomeNotif);
                // -------------------

                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Failed to create profile.", Toast.LENGTH_SHORT).show();
            }
        });

        // ─── Gender Spinner Setup ──────────────────────────────
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                genders
        );
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);
        genderSpinner.setSelection(0);

        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (pos == 0) {
                    userData.setGender(null);
                } else {
                    String sel = genders[pos].toUpperCase();
                    userData.setGender(Gender.valueOf(sel));
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) { }
        });

        // ─── Date of Birth Picker ───────────────────────────────
        dateOfBirthTxt.setOnClickListener(v -> {
            Calendar now = Calendar.getInstance();
            int y = now.get(Calendar.YEAR);
            int m = now.get(Calendar.MONTH);
            int d = now.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dp = new DatePickerDialog(
                    InputDetailsActivity.this,
                    (DatePicker view, int year, int monthOfYear, int dayOfMonth) -> {
                        monthOfYear++;  // zero‐based → human‐readable
                        @SuppressLint("DefaultLocale") String formatted = String.format(
                                "%02d/%02d/%04d",
                                dayOfMonth, monthOfYear, year
                        );
                        dateOfBirthTxt.setText(formatted);

                        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                        LocalDate dob = LocalDate.parse(formatted, fmt);
                        userData.setDateOfBirth(dob);
                    },
                    y, m, d
            );

            dp.getDatePicker().setMaxDate(System.currentTimeMillis()); // Disable future dates
            dp.show();
        });

        // ─── Address Autocomplete ──────────────────────────────
        locationArrayAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line
        );
        addressDropdown.setAdapter(locationArrayAdapter);
        addressDropdown.setThreshold(1);

        userViewModel.getGeocodingResults().observe(this, locations -> {
            locationArrayAdapter.clear();
            locationArrayAdapter.addAll(locations);
            locationArrayAdapter.notifyDataSetChanged();
            addressDropdown.showDropDown();
        });

        addressDropdown.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int st, int b, int c) {
                if (s.length() >= 1) {
                    userViewModel.searchAddress(s.toString());
                }
            }
        });

        addressDropdown.setOnItemClickListener((parent, view, position, id) -> {
            Location selected = locationArrayAdapter.getItem(position);
            if (selected != null) {
                userData.setAddress(selected.getPlace());
                userData.setLocationLat(selected.getLatitude());
                userData.setLocationLong(selected.getLongitude());
            }
        });


        // ─── Sign-up Button Clicking Event ──────────────────────────────
        signUpBtn.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            String userId = prefs.getString(KEY_USER_ID, null);
            userData.setUserId(userId);

            String username = usernameTxt.getText().toString().trim();
            String h = heightTxt.getText().toString().trim();
            String w = weightTxt.getText().toString().trim();

            if (username.isEmpty() || h.isEmpty() || w.isEmpty()) {
                Toast.makeText(this, "Fill all fields!", Toast.LENGTH_SHORT).show();
                return;
            }

            Double height = Double.parseDouble(h);
            Double weight = Double.parseDouble(w);
            userData.setUsername(username);
            userData.setHeight(height);
            userData.setWeight(weight);

            Integer result = userViewModel.completeSignUp(userData);
            if (result == 1) {
                Toast.makeText(this, "Some fields are still missing!", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
