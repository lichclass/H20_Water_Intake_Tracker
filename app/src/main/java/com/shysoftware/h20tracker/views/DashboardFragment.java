package com.shysoftware.h20tracker.views;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.material.tabs.TabLayout;
import com.shysoftware.h20tracker.R;

public class DashboardFragment extends Fragment {

    private FrameLayout frameLayout;
    private TabLayout tabLayout;

    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /* ------------------ Place All View Logic Here ------------------ */
        frameLayout = view.findViewById(R.id.dashboard_fragment_container);
        tabLayout = view.findViewById(R.id.dashboard_tab_layout);

        loadDashboardTab(new HomeFragment());

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Fragment selected = null;

                switch (tab.getPosition()) {
                    case 0:
                        selected = new HomeFragment();
                        break;
                    case 1:
                        selected = new LeaderboardsFragment();
                        break;
                }

                if(selected != null){
                    loadDashboardTab(selected);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
    }

    private void loadDashboardTab(Fragment fragment){
        getChildFragmentManager().beginTransaction()
                .replace(R.id.dashboard_fragment_container, fragment)
                .commit();
    }
}