package com.shysoftware.h20tracker.views;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.shysoftware.h20tracker.R;
import com.shysoftware.h20tracker.model.Gender;
import com.shysoftware.h20tracker.model.Location;
import com.shysoftware.h20tracker.model.User;
import com.shysoftware.h20tracker.viewmodel.UserViewModel;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class EditProfileActivity extends AppCompatActivity {

    private EditText usernameTxt, heightTxt, weightTxt, dateOfBirthTxt;
    private AutoCompleteTextView addressDropdown;
    private Spinner genderSpinner;
    private Button saveBtn, cancelBtn;

    private ArrayAdapter<Location> locationArrayAdapter;
    private UserViewModel userViewModel;
    private User userData = new User();

    private final String[] genders = {"Select Gender", "Male", "Female", "Other"};
    private static final String PREFS_NAME = "user_prefs";
    private static final String KEY_USER_ID = "user_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        bindViews();
        setupViewModel();
        setupGenderSpinner();
        setupDatePicker();
        setupAddressAutocomplete();
        setupButtons();
        observeUser();

        loadUserIdAndFetchData();
    }

    private void bindViews() {
        usernameTxt = findViewById(R.id.usernameEditText);
        heightTxt = findViewById(R.id.heightEditText);
        weightTxt = findViewById(R.id.weightEditText);
        dateOfBirthTxt = findViewById(R.id.dobTextView);
        addressDropdown = findViewById(R.id.addressDropdown);
        genderSpinner = findViewById(R.id.genderSpinner);
        saveBtn = findViewById(R.id.saveButton);
        cancelBtn = findViewById(R.id.cancelButton);
    }

    private void setupViewModel() {
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        userViewModel.getUpdateStatus().observe(this, success -> {
            if (success != null && success) {
                Toast.makeText(this, "Profile updated!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Failed to update profile.", Toast.LENGTH_SHORT).show();
            }
        });

        userViewModel.getGeocodingResults().observe(this, locations -> {
            locationArrayAdapter.clear();
            locationArrayAdapter.addAll(locations);
            locationArrayAdapter.notifyDataSetChanged();
            addressDropdown.showDropDown();
        });
    }

    private void setupGenderSpinner() {
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, genders
        );
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(genderAdapter);

        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                userData.setGender(pos == 0 ? null : Gender.valueOf(genders[pos].toUpperCase()));
            }
            @Override public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void setupDatePicker() {
        dateOfBirthTxt.setOnClickListener(v -> {
            Calendar now = Calendar.getInstance();
            int y = now.get(Calendar.YEAR), m = now.get(Calendar.MONTH), d = now.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dp = new DatePickerDialog(
                    EditProfileActivity.this,
                    (DatePicker view, int year, int month, int day) -> {
                        String formatted = String.format("%02d/%02d/%04d", day, month + 1, year);
                        dateOfBirthTxt.setText(formatted);
                        userData.setDateOfBirth(LocalDate.parse(formatted, DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                    },
                    y, m, d
            );

            dp.getDatePicker().setMaxDate(System.currentTimeMillis());
            dp.show();
        });
    }

    private void setupAddressAutocomplete() {
        locationArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line);
        addressDropdown.setAdapter(locationArrayAdapter);
        addressDropdown.setThreshold(1);

        addressDropdown.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {
                if (s.length() >= 1) userViewModel.searchAddress(s.toString());
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
    }

    private void setupButtons() {
        saveBtn.setOnClickListener(v -> {
            if (!validateInputs()) return;

            userData.setUsername(usernameTxt.getText().toString().trim());
            userData.setHeight(Double.parseDouble(heightTxt.getText().toString().trim()));
            userData.setWeight(Double.parseDouble(weightTxt.getText().toString().trim()));

            userViewModel.updateUserProfile(userData);
            setResult(RESULT_OK);
        });

        cancelBtn.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
    }

    private void observeUser() {
        userViewModel.getCurrentUser().observe(this, user -> {
            if (user == null) return;
            userData = user;
            populateFields(user);
        });
    }

    private void loadUserIdAndFetchData() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String userId = prefs.getString(KEY_USER_ID, null);
        if (userId != null) userViewModel.getUser(userId);
    }

    private void populateFields(User user) {
        usernameTxt.setText(user.getUsername());
        heightTxt.setText(String.format("%.2f", user.getHeight()));
        weightTxt.setText(String.format("%.2f", user.getWeight()));
        dateOfBirthTxt.setText(user.getDateOfBirth().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        addressDropdown.setText(user.getAddress());

        if (user.getGender() != null) {
            for (int i = 0; i < genders.length; i++) {
                if (genders[i].equalsIgnoreCase(user.getGender().toString())) {
                    genderSpinner.setSelection(i);
                    break;
                }
            }
        } else {
            genderSpinner.setSelection(0);
        }
    }

    private boolean validateInputs() {
        if (usernameTxt.getText().toString().trim().isEmpty() ||
                heightTxt.getText().toString().trim().isEmpty() ||
                weightTxt.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Fill all fields!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
