package com.shysoftware.h20tracker.views;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.shysoftware.h20tracker.R;
import com.shysoftware.h20tracker.model.Gender;

import java.util.ArrayList;

public class InputDetailsActivity extends AppCompatActivity {

    EditText usernameTxt, heightTxt, weightTxt, dateOfBirthTxt;
    AutoCompleteTextView addressDropdown;
    Spinner genderSpinner;

    String[] genders = {"Male", "Female", "Other"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_details);

        usernameTxt = findViewById(R.id.usernameEditText);
        heightTxt = findViewById(R.id.heightEditText);
        weightTxt = findViewById(R.id.weightEditText);
        dateOfBirthTxt = findViewById(R.id.dobTextView);
        addressDropdown = findViewById(R.id.addressDropdown);
        genderSpinner = findViewById(R.id.genderSpinner);

//        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(
//                this,
//                android.R.layout.simple_dropdown_item_1line,
//                genders);
//
//        genderAdapter.setDropDownViewResource(
//                android.R.layout.simple_dropdown_item_1line
//        );
//
//        genderSpinner.setAdapter(genderAdapter);

    }
}