package com.shysoftware.h20tracker.views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.shysoftware.h20tracker.R;
import com.shysoftware.h20tracker.model.User;
import com.shysoftware.h20tracker.viewmodel.UserViewModel;
import com.shysoftware.h20tracker.viewmodel.WaterIntakeViewModel;

import java.time.format.DateTimeFormatter;

public class ProfileFragment extends Fragment {

    TextView profileUsername, profileHeight, profileBMI, profileWeight, profileTotalWater, profileAvgWater, profileDateJoined;
    Button editProfileBtn;
    UserViewModel userViewModel;
    WaterIntakeViewModel waterIntakeViewModel;

    private ActivityResultLauncher<Intent> editProfileLauncher;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /* ------------------ Place All View Logic Here ------------------ */

        profileUsername = view.findViewById(R.id.profile_username);
        profileHeight = view.findViewById(R.id.profile_height);
        profileBMI = view.findViewById(R.id.profile_bmi);
        profileWeight = view.findViewById(R.id.profile_weight);
        profileTotalWater = view.findViewById(R.id.profile_total_water);
        profileAvgWater = view.findViewById(R.id.profile_avg_water);
        profileDateJoined = view.findViewById(R.id.profile_date_joined);
        editProfileBtn = view.findViewById(R.id.edit_profile_btn);

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        waterIntakeViewModel = new ViewModelProvider(requireActivity()).get(WaterIntakeViewModel.class);

        userViewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            if (user == null) return;
            bindUserData(user);
            loadIntakeMetrics(user);
        });

        waterIntakeViewModel.getTotalWaterIntake().observe(getViewLifecycleOwner(), total -> {
           if(total != null){
               profileTotalWater.setText(String.format("%.0f mL", total));
           }
        });
        waterIntakeViewModel.getWeeklyAverageIntake().observe(getViewLifecycleOwner(), avg -> {
            if(avg != null){
                profileAvgWater.setText(String.format("%.0f mL", avg));
            }
        });

        editProfileBtn.setOnClickListener(v -> editProfileLauncher.launch(new Intent(requireActivity(), EditProfileActivity.class)));

        editProfileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        SharedPreferences prefs = requireActivity().getSharedPreferences("user_prefs", requireActivity().MODE_PRIVATE);
                        String userId = prefs.getString("user_id", null);
                        if (userId != null) {
                            userViewModel.getUser(userId);
                        }
                    }
                }
        );
    }

    private void bindUserData(User user) {
        profileUsername.setText(user.getUsername());
        profileHeight.setText(String.format("%.1f", user.getHeight()));
        double hM = user.getHeight() / 100;
        profileBMI.setText(String.format("%.1f", user.getWeight() / (hM * hM)));
        profileWeight.setText(String.format("%.1f", user.getWeight()));
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MMMM dd, yyyy");
        profileDateJoined.setText("Hydrated since " + user.getCreatedAt().format(fmt));
    }

    // ➌ Trigger all three fetches
    private void loadIntakeMetrics(User user) {
        waterIntakeViewModel.setWaterIntakeList(user);
        waterIntakeViewModel.computeWeeklyAverage(user);
    }
}