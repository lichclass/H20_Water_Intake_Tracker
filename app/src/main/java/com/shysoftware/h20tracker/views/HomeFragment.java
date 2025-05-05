package com.shysoftware.h20tracker.views;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.shysoftware.h20tracker.R;
import com.shysoftware.h20tracker.model.HydrationGoal;
import com.shysoftware.h20tracker.model.User;
import com.shysoftware.h20tracker.model.WeatherData;
import com.shysoftware.h20tracker.viewmodel.HydrationGoalViewModel;
import com.shysoftware.h20tracker.viewmodel.UserViewModel;
import com.shysoftware.h20tracker.viewmodel.WaterIntakeViewModel;
import com.shysoftware.h20tracker.viewmodel.WeatherDataViewModel;

import java.time.format.DateTimeFormatter;

public class HomeFragment extends Fragment {
    private UserViewModel userViewModel;
    private WeatherDataViewModel weatherDataViewModel;
    private HydrationGoalViewModel hydrationGoalViewModel;
    private WaterIntakeViewModel waterIntakeViewModel;
    private User user;
    private WeatherData weatherData;
    private HydrationGoal hydrationGoal;
    private Double todayIntake;

    TextView waterGoalTxt, progressTxt, dateTxt, dateDayTxt, tempTxt, weatherCondTxt, humidityTxt, waterAmountInput;
    ProgressBar progressBar;
    Dialog addWaterModal;
    ImageButton imageButton;
    Button cancelBtn, addBtn, incrementBtn, decrementBtn;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /* ------------------ Place All View Logic Here ------------------ */

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        weatherDataViewModel = new ViewModelProvider(requireActivity()).get(WeatherDataViewModel.class);
        hydrationGoalViewModel = new ViewModelProvider(requireActivity()).get(HydrationGoalViewModel.class);
        waterIntakeViewModel = new ViewModelProvider(requireActivity()).get(WaterIntakeViewModel.class);
        addWaterModal = new Dialog(requireActivity());

        waterGoalTxt = view.findViewById(R.id.waterGoal);
        progressTxt = view.findViewById(R.id.progressText);
        progressBar = view.findViewById(R.id.progressBar);
        dateTxt = view.findViewById(R.id.date_txt);
        dateDayTxt = view.findViewById(R.id.date_day_txt);
        tempTxt = view.findViewById(R.id.temp_txt);
        weatherCondTxt = view.findViewById(R.id.weather_cond_txt);
        humidityTxt = view.findViewById(R.id.humidity_txt);

        imageButton = view.findViewById(R.id.addWaterButton);
        addWaterModal.setContentView(R.layout.modal_add_water);
        cancelBtn = addWaterModal.findViewById(R.id.cancelButton);
        addBtn = addWaterModal.findViewById(R.id.addButton);
        incrementBtn = addWaterModal.findViewById(R.id.incrementButton);
        decrementBtn = addWaterModal.findViewById(R.id.decrementButton);
        waterAmountInput = addWaterModal.findViewById(R.id.waterAmountInput);

        loadData();

        imageButton.setOnClickListener(v -> {
            addWaterModal.show();
            waterAmountInput.setText("0");
        });

        // For Add Water Intake Interaction
        incrementBtn.setOnClickListener(v -> {
            String newVal = incrementIntakeToStr(waterAmountInput.getText().toString());
            waterAmountInput.setText(newVal);
        });
        decrementBtn.setOnClickListener(v -> {
            String newVal = decrementIntakeToStr(waterAmountInput.getText().toString());
            waterAmountInput.setText(newVal);
        });

        cancelBtn.setOnClickListener(v -> {
            addWaterModal.cancel();
        });
        addBtn.setOnClickListener(v -> {
            double amount = Double.parseDouble(waterAmountInput.getText().toString());
            addWaterModal.cancel();
        });



    }

    private void trySetGoal() {
        if (user != null && weatherData != null) {
            hydrationGoalViewModel.setTodayGoal(user, weatherData);
        }
    }

    @SuppressLint("DefaultLocale")
    private void loadData() {
        userViewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                this.user = user;
                trySetGoal();
            }
        });
        weatherDataViewModel.getTodayWeather().observe(getViewLifecycleOwner(), weather -> {
            if (weather != null) {
                this.weatherData = weather;

                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
                String dateFormatted = weather.getDate().format(dateFormatter);
                dateTxt.setText(dateFormatted);

                DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("EEEE");
                String dayFormatted = weather.getDate().format(dayFormatter);
                dateDayTxt.setText(dayFormatted);

                tempTxt.setText(String.format("%.0f°C", weather.getTemperatureC()));

                String conditionCapitalized = weather.getWeatherCondition().getValue();
                conditionCapitalized = conditionCapitalized.substring(0, 1).toUpperCase() + conditionCapitalized.substring(1);
                weatherCondTxt.setText(conditionCapitalized);

                humidityTxt.setText(String.format("%d%%", weather.getHumidityPercent()));
                trySetGoal();
            }
        });
        hydrationGoalViewModel.getTodayGoal().observe(getViewLifecycleOwner(), goal -> {
            if (goal != null) {
                this.hydrationGoal = goal;

                Log.d("HOME_FRAGMENT", "Hydration goal received: " + this.hydrationGoal.getTargetAmountMl());
                waterGoalTxt.setText(String.format("%.0f ml", this.hydrationGoal.getTargetAmountMl()));

                waterIntakeViewModel.getTodayIntake().observe(getViewLifecycleOwner(), progress -> {
                    if(progress != null) {
                        this.todayIntake = progress;
                        double progPercent = (progress / hydrationGoal.getTargetAmountMl()) * 100;
                        progressTxt.setText(String.format("%.0f mL / %.0f mL", this.todayIntake, this.hydrationGoal.getTargetAmountMl()));
                        progressBar.setProgress((int)progPercent);
                    }
                });
            }
        });
    }

    @SuppressLint("DefaultLocale")
    public String incrementIntakeToStr(String ogVal){
        double val = Double.parseDouble(ogVal);
        val += 1.00;
        return String.format("%.0f", val);
    }

    @SuppressLint("DefaultLocale")
    public String decrementIntakeToStr(String ogVal){
        double val = Double.parseDouble(ogVal);
        val -= 1.00;
        if (val < 0) { val = 0.00; }
        return String.format("%.0f", val);
    }
}