package com.shysoftware.h20tracker.views;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.shysoftware.h20tracker.R;
import com.shysoftware.h20tracker.viewmodel.WeatherDataViewModel;

public class MainActivity extends AppCompatActivity {

    private WeatherDataViewModel weatherDataViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weatherDataViewModel = new ViewModelProvider(this).get(WeatherDataViewModel.class);

        weatherDataViewModel.getForecast(10.263963691064777, 123.85518645823632);

    }
}
