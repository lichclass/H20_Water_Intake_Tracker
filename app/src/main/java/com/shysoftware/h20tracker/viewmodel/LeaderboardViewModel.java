package com.shysoftware.h20tracker.viewmodel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.shysoftware.h20tracker.R;
import com.shysoftware.h20tracker.model.LeaderboardEntry;

import java.util.ArrayList;
import java.util.List;


// ------------------------- RECYCLER VIEW TEST -------------------------
public class LeaderboardViewModel extends ViewModel {
    private MutableLiveData<List<LeaderboardEntry>> leaderboard;

    public LeaderboardViewModel() {
        leaderboard = new MutableLiveData<>();
        loadLeaderboard(); // Mock or real data
    }

    public LiveData<List<LeaderboardEntry>> getLeaderboard() {
        return leaderboard;
    }

    private void loadLeaderboard() {
        // Fake data for now
        List<LeaderboardEntry> entries = new ArrayList<>();
        entries.add(new LeaderboardEntry("Alice", 1500, 4, R.drawable.sample_pfp));
        entries.add(new LeaderboardEntry("Bob", 1400, 5, R.drawable.sample_pfp));
        entries.add(new LeaderboardEntry("Charlie", 1300, 6, R.drawable.sample_pfp));
        leaderboard.setValue(entries);
    }
}

