package com.shysoftware.h20tracker.views;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.shysoftware.h20tracker.R;
import com.shysoftware.h20tracker.model.Location;
import com.shysoftware.h20tracker.viewmodel.UserViewModel;

public class TestActivity extends AppCompatActivity {

    private UserViewModel userViewModel;
    private AutoCompleteTextView locationInput;
    private ArrayAdapter<Location> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        // 1) set up the adapter for Location objects
        locationInput = findViewById(R.id.location_input);
        adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line
        );
        locationInput.setAdapter(adapter);
        locationInput.setThreshold(1);

        // 2) obtain VM
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // 3) observe the List<Location> LiveData
        userViewModel.getGeocodingResults().observe(this, locations -> {
            adapter.clear();
            adapter.addAll(locations);
            adapter.notifyDataSetChanged();
            locationInput.showDropDown();
        });

        // 4) trigger geocoding on text change
        locationInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int st, int b, int c) {
                if (s.length() >= 1) {
                    userViewModel.searchAddress(s.toString());
                }
            }
        });

        // 5) when the user picks one, you get the full Location object
        locationInput.setOnItemClickListener((parent, view, position, id) -> {
            Location selected = adapter.getItem(position);
            if (selected != null) {
                Toast.makeText(
                        this,
                        "Picked: " + selected.getPlace() +
                                "\nLat: " + selected.getLatitude() +
                                "\nLon: " + selected.getLongitude(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }
}
