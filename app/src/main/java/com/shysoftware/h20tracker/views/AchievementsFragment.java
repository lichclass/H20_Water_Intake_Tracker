package com.shysoftware.h20tracker.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.shysoftware.h20tracker.R;
import com.shysoftware.h20tracker.databinding.FragmentAchievementsBinding;
import com.shysoftware.h20tracker.model.Rank;
import com.shysoftware.h20tracker.model.User;
import com.shysoftware.h20tracker.viewmodel.UserViewModel;

public class AchievementsFragment extends Fragment {

    private FragmentAchievementsBinding binding;
    private UserViewModel userViewModel;

    public AchievementsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment with View Binding
        binding = FragmentAchievementsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /* ------------------ Place All View Logic Here ------------------ */
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        setupAchievementsUI();
    }

    private void setupAchievementsUI() {
        // Observe the current user from ViewModel
        userViewModel.getCurrentUser().observe(getViewLifecycleOwner(), user -> {
            if (user != null) {
                // Get rank data
                RankData rankData = getRankData(user);

                // Update UI
                TextView currentBadgeTitle = binding.currentBadgeTitle;
                ImageView pastAchievementBadge = binding.pastAchievementBadge;
                ImageView currentAchievementBadge = binding.currentAchievementBadge;
                ImageView nextAchievementBadge = binding.nextAchievementBadge;
                TextView xpStatus = binding.getRoot().findViewById(R.id.xpStatus);
                ProgressBar progressBar = binding.progressBar;
                TextView progressText = binding.progressText;
                TextView currentAchievementTitle = binding.currentAchievementTitle;
                TextView nextAchievementTitle = binding.nextAchievementTitle;

                // Current rank
                currentBadgeTitle.setText(rankData.currentRank.name());
                currentAchievementTitle.setText(rankData.currentRank.name());
                currentAchievementBadge.setImageResource(getBadgeDrawable(rankData.currentRank));

                // XP display
                xpStatus.setText(String.format("%d XP", rankData.currentXP));

                // Progress bar and text
                progressBar.setMax(rankData.nextLevelXPThreshold);
                progressBar.setProgress(rankData.currentXP);
                progressText.setText(String.format("%d XP / %d XP", rankData.currentXP, rankData.nextLevelXPThreshold));

                // Previous rank (if exists)
                if (rankData.previousRank != null) {
                    pastAchievementBadge.setImageResource(getBadgeDrawable(rankData.previousRank));
                    pastAchievementBadge.setAlpha(0.5f);
                } else {
                    pastAchievementBadge.setVisibility(View.GONE);
                }

                // Next rank (if exists)
                if (rankData.nextRank != null) {
                    nextAchievementBadge.setImageResource(getLockedBadgeDrawable(rankData.nextRank));
                    nextAchievementBadge.setAlpha(0.3f);
                    nextAchievementTitle.setText(rankData.nextRank.name());
                } else {
                    nextAchievementBadge.setVisibility(View.GONE);
                    nextAchievementTitle.setVisibility(View.GONE);
                }
            }
        });
    }

    private RankData getRankData(User user) {
        Rank currentRank = userViewModel.getUserRank(user);
        int currentXP = user.getXp().intValue();
        Rank previousRank = null;
        Rank nextRank = null;
        int nextLevelXPThreshold = 0;

        // Define XP thresholds and rank progression (based on getUserRank logic)
        Rank[] ranks = Rank.values();
        int[] xpThresholds = {50, 150, 300, 500, 750, 1050, 1400, 1800, 2300, Integer.MAX_VALUE};
        int currentLevel = currentRank.ordinal();

        // Determine previous and next ranks
        if (currentLevel > 0) {
            previousRank = ranks[currentLevel - 1];
        }
        if (currentLevel < ranks.length - 1) {
            nextRank = ranks[currentLevel + 1];
            nextLevelXPThreshold = xpThresholds[currentLevel];
        } else {
            nextLevelXPThreshold = currentXP; // Max rank, no progress
        }

        return new RankData(currentRank, previousRank, nextRank, currentXP, nextLevelXPThreshold);
    }

    private int getBadgeDrawable(Rank rank) {
        switch (rank) {
            case DRIPLET:
                return R.drawable.achievement_level0;
            case SIPPER:
                return R.drawable.achievement_level1;
            case GULPER:
                return R.drawable.achievement_level2;
            case HYDRATION_SEEKER:
                return R.drawable.achievement_level3;
            case WATER_WARRIOR:
                return R.drawable.achievement_level4;
            case AQUA_ACHIEVER:
                return R.drawable.achievement_level5;
            case HYDRO_HERO:
                return R.drawable.achievement_level6;
            case OCEAN_GUARDIAN:
                return R.drawable.achievement_level7;
            case LIQUID_LEGEND:
                return R.drawable.achievement_level8;
            case H2OVERLORD:
                return R.drawable.achievement_level9;
            default:
                return R.drawable.achievement_level1;
        }
    }

    private int getLockedBadgeDrawable(Rank rank) {
        switch (rank) {
            case SIPPER:
                return R.drawable.achievement_lock_level1;
            case GULPER:
                return R.drawable.achievement_lock_level2;
            case HYDRATION_SEEKER:
                return R.drawable.achievement_lock_level3;
            case WATER_WARRIOR:
                return R.drawable.achievement_lock_level4;
            case AQUA_ACHIEVER:
                return R.drawable.achievement_lock_level5;
            case HYDRO_HERO:
                return R.drawable.achievement_lock_level6;
            case OCEAN_GUARDIAN:
                return R.drawable.achievement_lock_level7;
            case LIQUID_LEGEND:
                return R.drawable.achievement_lock_level8;
            case H2OVERLORD:
                return R.drawable.achievement_lock_level9;
            default:
                return R.drawable.achievement_lock_level1;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // Data class to hold rank information
    private static class RankData {
        final Rank currentRank;
        final Rank previousRank;
        final Rank nextRank;
        final int currentXP;
        final int nextLevelXPThreshold;

        RankData(Rank currentRank, Rank previousRank, Rank nextRank, int currentXP, int nextLevelXPThreshold) {
            this.currentRank = currentRank;
            this.previousRank = previousRank;
            this.nextRank = nextRank;
            this.currentXP = currentXP;
            this.nextLevelXPThreshold = nextLevelXPThreshold;
        }
    }
}