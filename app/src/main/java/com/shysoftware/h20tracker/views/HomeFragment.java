package com.shysoftware.h20tracker.views;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.shysoftware.h20tracker.R;
import com.shysoftware.h20tracker.model.HydrationGoal;
import com.shysoftware.h20tracker.model.User;
import com.shysoftware.h20tracker.model.WaterIntake;
import com.shysoftware.h20tracker.model.WeatherData;
import com.shysoftware.h20tracker.utils.DeleteWaterAdapter;
import com.shysoftware.h20tracker.utils.NotificationHelper;
import com.shysoftware.h20tracker.utils.TipsAdapter;
import com.shysoftware.h20tracker.viewmodel.HydrationGoalViewModel;
import com.shysoftware.h20tracker.viewmodel.NotificationViewModel;
import com.shysoftware.h20tracker.viewmodel.UserViewModel;
import com.shysoftware.h20tracker.viewmodel.WaterIntakeViewModel;
import com.shysoftware.h20tracker.viewmodel.WeatherDataViewModel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class HomeFragment extends Fragment {

    // View Models
    private UserViewModel userViewModel;
    private WeatherDataViewModel weatherDataViewModel;
    private HydrationGoalViewModel hydrationGoalViewModel;
    private WaterIntakeViewModel waterIntakeViewModel;
    private NotificationViewModel notificationViewModel;

    // Data Models
    private User user;
    private WeatherData weatherData;
    private HydrationGoal hydrationGoal;
    private Double todayIntake;

    // UI Elements
    TextView waterGoalTxt, progressTxt, dateTxt, dateDayTxt, tempTxt, weatherCondTxt, humidityTxt, waterAmountInput, additionalWaterGoalTxt;
    ProgressBar progressBar, dailyProgress, weeklyProgress, monthlyProgress;
    Dialog addWaterModal, deleteWaterModal;
    ImageButton imageButton, closeBtn, delIntakeBtn;
    Button cancelBtn, addBtn, incrementBtn, decrementBtn;
    ImageView weatherIcon, dailyProgIcon, weeklyProgIcon, monthlyProgIcon;
    ListView historyList;
    RecyclerView deleteWaterRecyclerView;
    ViewPager2 healthTipsViewPager, hydrationTipsViewPager;

    // Others
    DeleteWaterAdapter deleteWaterAdapter;
    TipsAdapter healthTipsAdapter, hydrationTipsAdapter;
    ArrayList<WaterIntake> historyData;


    List<String> healthTipsTitle = new ArrayList<>();
    List<String> healthTipsDesc = new ArrayList<>();
    List<String> hydrationTipsTitle = new ArrayList<>();
    List<String> hydrationTipsDesc = new ArrayList<>();

    public HomeFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }


    @SuppressLint("DefaultLocale")
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /* ------------------ Place All View Logic Here ------------------ */

        // Initializations
        initVM();
        initViews(view);
        setupObservers();

        // Add Water Intake Modal
        imageButton.setOnClickListener(v -> {
            addWaterModal.show();
            waterAmountInput.setText("0");
        });

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
            if(user != null && amount > 0.00){
                waterIntakeViewModel.logWater(user, amount);

                waterIntakeViewModel.computeWeeklyAverage(user);
                waterIntakeViewModel.getProgress(user);
                waterIntakeViewModel.setWeeklyProgress(user, LocalDate.now());
                waterIntakeViewModel.setMonthlyProgress(user, LocalDate.now());

                loadHistory();
            }
            addWaterModal.dismiss();
        });


        // Delete Water Intake Modal
        delIntakeBtn.setOnClickListener(v -> {
            deleteWaterModal.show();
        });

        closeBtn.setOnClickListener(v -> {
            deleteWaterModal.dismiss();
        });

    }

    private void initVM(){
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        weatherDataViewModel = new ViewModelProvider(requireActivity()).get(WeatherDataViewModel.class);
        hydrationGoalViewModel = new ViewModelProvider(requireActivity()).get(HydrationGoalViewModel.class);
        waterIntakeViewModel = new ViewModelProvider(requireActivity()).get(WaterIntakeViewModel.class);
        notificationViewModel = new ViewModelProvider(requireActivity()).get(NotificationViewModel.class);
        addWaterModal = new Dialog(requireActivity());
        deleteWaterModal = new Dialog(requireActivity());
    }

    private void initViews(View view){
        waterGoalTxt = view.findViewById(R.id.waterGoal);
        additionalWaterGoalTxt = view.findViewById(R.id.additionalWaterGoal);
        progressTxt = view.findViewById(R.id.progressText);
        progressBar = view.findViewById(R.id.progressBar);
        dailyProgress = view.findViewById(R.id.dailyProgress);
        weeklyProgress = view.findViewById(R.id.weeklyProgress);
        monthlyProgress = view.findViewById(R.id.monthlyProgress);
        dateTxt = view.findViewById(R.id.date_txt);
        dateDayTxt = view.findViewById(R.id.date_day_txt);
        tempTxt = view.findViewById(R.id.temp_txt);
        weatherCondTxt = view.findViewById(R.id.weather_cond_txt);
        humidityTxt = view.findViewById(R.id.humidity_txt);
        weatherIcon = view.findViewById(R.id.weather_icon);
        dailyProgIcon = view.findViewById(R.id.daily_prog_icon);
        weeklyProgIcon = view.findViewById(R.id.weekly_prog_icon);
        monthlyProgIcon = view.findViewById(R.id.monthly_prog_icon);
        healthTipsViewPager = view.findViewById(R.id.healthtipsViewPager);
        hydrationTipsViewPager = view.findViewById(R.id.hydrationtipsViewPager);

        // Add Intake Modal
        imageButton = view.findViewById(R.id.addWaterButton);
        addWaterModal.setContentView(R.layout.modal_add_water);
        cancelBtn = addWaterModal.findViewById(R.id.cancelButton);
        addBtn = addWaterModal.findViewById(R.id.addButton);
        incrementBtn = addWaterModal.findViewById(R.id.incrementButton);
        decrementBtn = addWaterModal.findViewById(R.id.decrementButton);
        waterAmountInput = addWaterModal.findViewById(R.id.waterAmountInput);

        // Delete Intake Modal
        delIntakeBtn = view.findViewById(R.id.modal_del_btn);
        deleteWaterModal.setContentView(R.layout.modal_delete_water);
        closeBtn = deleteWaterModal.findViewById(R.id.close_btn);
        deleteWaterRecyclerView = deleteWaterModal.findViewById(R.id.delete_recycler_view);

        historyData = new ArrayList<>();

        deleteWaterRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        deleteWaterAdapter = new DeleteWaterAdapter(requireActivity(), historyData, intake -> {waterIntakeViewModel.deleteIntakeEntry(intake, user);});
        deleteWaterRecyclerView.setAdapter(deleteWaterAdapter);

        // Notification
        NotificationHelper.createNotificationChannel(requireContext());
    }

    private void setWeatherDataUI(WeatherData weather){
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

        weatherIcon.setImageResource(weatherIcon(weather));
    }

    private void trySetGoal() {
        if (user != null && weatherData != null) {
            hydrationGoalViewModel.setTodayGoal(user, weatherData);
        }
    }

    @SuppressLint("DefaultLocale")
    private void setupObservers() {
        userViewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                this.user = user;
                hydrationGoalViewModel.getTodayGoal().observe(getViewLifecycleOwner(), this::onGoalUpdated);
                waterIntakeViewModel.getDailyIntakeGrouped(user);
                hydrationGoalViewModel.fetchAllGoals(user.getUserId());
                waterIntakeViewModel.computeWeeklyAverage(user);
                notificationViewModel.getNotification(user.getUserId());
                trySetGoal();
            }
        });
        weatherDataViewModel.getTodayWeather().observe(getViewLifecycleOwner(), weather -> {
            if (weather != null) {
                this.weatherData = weather;
                setWeatherDataUI(weather);
                trySetGoal();
            }
        });
        hydrationGoalViewModel.getTodayGoal().observe(getViewLifecycleOwner(), goal -> {
            if (goal != null) {
                this.hydrationGoal = goal;
                waterGoalTxt.setText(String.format("%.0f ml", this.hydrationGoal.getTargetAmountMl()));

                Double temp = weatherData.getTemperatureC();
                Integer humidity = weatherData.getHumidityPercent();

                additionalWaterGoalTxt.setText(String.format("%.0f mL", hydrationGoalViewModel.computeAdjustment(temp, humidity)));

                initHealthTipsData();
                initHydrationTipsData();

                healthTipsAdapter = new TipsAdapter(healthTipsTitle, healthTipsDesc, "health");
                hydrationTipsAdapter = new TipsAdapter(hydrationTipsTitle, hydrationTipsDesc, "hydration");
                healthTipsViewPager.setAdapter(healthTipsAdapter);
                hydrationTipsViewPager.setAdapter(hydrationTipsAdapter);
            }
        });
        waterIntakeViewModel.getDeleteStatus().observe(getViewLifecycleOwner(), success -> {
            if (Boolean.TRUE.equals(success)) {
                waterIntakeViewModel.getProgress(user);
                waterIntakeViewModel.setWeeklyProgress(user, LocalDate.now());
                waterIntakeViewModel.setMonthlyProgress(user, LocalDate.now());
                loadHistory();
            }
        });
        setProgress();
    }

    @SuppressLint("DefaultLocale")
    private void setProgress(){
        waterIntakeViewModel.getTodayIntake().observe(getViewLifecycleOwner(), progress -> {
            if(progress != null && hydrationGoal != null && user != null) {
                this.todayIntake = progress;

                double progPercent = (progress / hydrationGoal.getTargetAmountMl()) * 100;
                progressTxt.setText(String.format("%.0f mL / %.0f mL", this.todayIntake, this.hydrationGoal.getTargetAmountMl()));
                progressBar.setProgress((int)progPercent);

                double dailyGoalProgPercent = (progress / waterIntakeViewModel.computeDailyGoal(user)) * 100;
                dailyProgress.setProgress((int)dailyGoalProgPercent);
                dailyProgIcon.setImageResource(getProgIcon(dailyGoalProgPercent));
            }
        });
        waterIntakeViewModel.getWeeklyProgress().observe(getViewLifecycleOwner(), progress -> {
            if(progress != null && user != null) {
                double progPercent = (progress / waterIntakeViewModel.computeWeeklyGoal(user)) * 100;
                weeklyProgress.setProgress((int)progPercent);
                weeklyProgIcon.setImageResource(getProgIcon(progPercent));
            }
        });
        waterIntakeViewModel.getMonthlyProgress().observe(getViewLifecycleOwner(), progress -> {
            if(progress != null && user != null) {
                double progPercent = (progress / waterIntakeViewModel.computeMonthlyGoal(user)) * 100;
                monthlyProgress.setProgress((int)progPercent);
                monthlyProgIcon.setImageResource(getProgIcon(progPercent));
            }
        });
    }

    private void onGoalUpdated(HydrationGoal goal) {
        if (goal == null) return;
        this.hydrationGoal = goal;
        waterGoalTxt.setText(String.format("%.0f ml", goal.getTargetAmountMl()));

        setProgress();
        loadHistory();

        waterIntakeViewModel.getProgress(user);
        waterIntakeViewModel.setWeeklyProgress(user, LocalDate.now());
        waterIntakeViewModel.setMonthlyProgress(user, LocalDate.now());
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

    public Integer weatherIcon(WeatherData weather){
        switch (weather.getWeatherCondition()) {
            case THUNDERSTORM:
                return R.drawable.dayicon_thunderstorm;
            case DRIZZLE:
                return R.drawable.dayicon_shower_rain;
            case RAIN:
                return R.drawable.dayicon_rain;
            case SNOW:
                return R.drawable.dayicon_snow;
            case ATMOSPHERE:
                return R.drawable.dayicon_mist;
            case CLEAR:
                return R.drawable.dayicon_clear_sky;
            case CLOUDS:
                return R.drawable.dayicon_scattered_clouds;
            default:
                return null;
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void loadHistory() {
        waterIntakeViewModel.setWaterIntakeList(user);
        waterIntakeViewModel.getIntakeList().observe(getViewLifecycleOwner(), entries -> {
            historyData.clear();
            historyData.addAll(entries);
            deleteWaterAdapter.notifyDataSetChanged();
        });
    }

    private Integer getProgIcon(Double progPercent){
        if(progPercent == null){ return null; }
        if(progPercent < 33) return R.drawable.home_sad_icon;
        else if(progPercent < 66) return R.drawable.home_neutral_icon;
        else return R.drawable.home_happy_icon;
    }

    private void initHealthTipsData() {
        this.healthTipsTitle = Arrays.asList(
                "Move More, Sit Less",
                "Balanced Nutrition",
                "Quality Sleep"
        );

        this.healthTipsDesc = Arrays.asList(
                "Incorporate short activity breaks into your day—stand up, stretch, or take a quick walk every hour to boost circulation and energy levels.",
	            "Complement your hydration with nutrient-dense foods—eat fruits, vegetables, and lean proteins to help your body retain fluids and support overall health.",
	            "Aim for 7–9 hours of uninterrupted sleep each night. Proper rest helps regulate hormones that control thirst and fluid balance."
        );
    }

    private void initHydrationTipsData() {
        // 1) Start with a mutable copy of your static tips
        List<String> titles = new ArrayList<>(Arrays.asList(
                "Drink Before You’re Thirsty",
                "Infuse Your Water",
                "Set Hydration Goals"
        ));
        List<String> descs = new ArrayList<>(Arrays.asList(
                "Keep a reusable water bottle within reach and take small sips regularly instead of waiting until you feel parched.",
                "Add natural flavor enhancers like lemon, cucumber slices, or fresh mint to your water to make drinking more enjoyable.",
                "Use the app’s daily target feature to break down your intake into manageable portions and get reminders when you fall behind."
        ));

        // 2) Temperature‐based tip (guard against a null basisTemp)
        Double basisTemp = hydrationGoal.getBasisTemp();
        if (basisTemp != null && basisTemp >= 30.0) {
            titles.add("It’s Hot Outside");
            descs.add("High heat accelerates fluid loss—try drinking an extra 200 ml of water over the next hour to stay cool and hydrated.");
        }

        // 3) Progress‐and‐time tip (guard against null todayIntake / targetAmount)
        if (todayIntake != null && hydrationGoal.getTargetAmountMl() != null) {
            double progPercent = (todayIntake / hydrationGoal.getTargetAmountMl()) * 100.0;
            int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);  // 0–23
            if (progPercent < 50.0 && hour >= 12) {
                titles.add("Catch Up on Hydration");
                descs.add("You’re halfway through the day but behind on water—have two 250 ml glasses before lunch to get back on track.");
            }
        }

        // 4) Finally, update your fragment’s lists
        this.hydrationTipsTitle = titles;
        this.hydrationTipsDesc = descs;
    }


}