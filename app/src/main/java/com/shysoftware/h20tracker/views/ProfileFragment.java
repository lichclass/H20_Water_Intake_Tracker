package com.shysoftware.h20tracker.views;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

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

public class ProfileFragment extends Fragment {

    TextView profileUsername, profileHeight, profileBMI, profileWeight, profileTotalWater, profileAvgWater, profileDateJoined;
    Button editProfileBtn;
    UserViewModel userViewModel;
    WaterIntakeViewModel waterIntakeViewModel;
    User user;

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
            if(user != null){
                profileUsername.setText(user.getUsername());
                profileHeight.setText(String.format("%.1f", user.getHeight()));

                double height_m = user.getHeight() / 100;
                double bmi = user.getWeight() / (height_m * height_m);

                profileBMI.setText(String.format("%.1f", bmi));
                profileWeight.setText(String.format("%.1f", user.getWeight()));
            }
        });

        waterIntakeViewModel.getTotalWaterIntake().observe(getViewLifecycleOwner(), total -> {
           if(total != null){
               profileTotalWater.setText(String.format("%.0f mL", total));
           }
        });

        editProfileBtn.setOnClickListener(v -> {
            startActivity(new Intent(requireActivity(), EditProfileActivity.class));
        });

    }
}