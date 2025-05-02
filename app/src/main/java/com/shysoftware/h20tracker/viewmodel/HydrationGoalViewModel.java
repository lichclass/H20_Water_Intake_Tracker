package com.shysoftware.h20tracker.viewmodel;

import androidx.lifecycle.ViewModel;

import com.shysoftware.h20tracker.model.User;
import com.shysoftware.h20tracker.repository.WeatherDataRepository;

public class HydrationGoalViewModel extends ViewModel {

    private final WeatherDataRepository weatherDataRepository = new WeatherDataRepository();

    public Boolean computeAndStoreGoal(User user){
        Boolean result = false;

        Double baselineWaterIntake = user.getWeight() * 35;

        // Adjustment
        Double tempAdj = 0.00;
        Double humAdj = 0.00;

        // Fetch Tomorrow's weather

        return result;
    }

    public void fetchCurrentWeatherData(){

    }
}
