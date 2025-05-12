package com.shysoftware.h20tracker.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.shysoftware.h20tracker.R;
import com.shysoftware.h20tracker.model.WaterIntake;
import com.shysoftware.h20tracker.viewmodel.HydrationGoalViewModel;
import com.shysoftware.h20tracker.viewmodel.WaterIntakeViewModel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class HistoryFragment extends Fragment {

    private CombinedChart chart;
    private RecyclerView historyRecyclerView;
    private TextView historyDateToday;
    private HistoryAdapter adapter;
    private List<WaterIntake> waterHistory;
    private WaterIntakeViewModel waterIntakeViewModel;
    private HydrationGoalViewModel hydrationGoalViewModel;

    public HistoryFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        chart = view.findViewById(R.id.chart);
        historyRecyclerView = view.findViewById(R.id.historyRecyclerView);
        historyDateToday = view.findViewById(R.id.history_date_today);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
        historyDateToday.setText(LocalDate.now().format(formatter));

        waterIntakeViewModel = new ViewModelProvider(requireActivity()).get(WaterIntakeViewModel.class);
        hydrationGoalViewModel = new ViewModelProvider(requireActivity()).get(HydrationGoalViewModel.class);

        setupHistoryList();
        observeAndRenderChart();
    }

    private void setupHistoryList() {
        waterHistory = new ArrayList<>();
        adapter = new HistoryAdapter(waterHistory);
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        historyRecyclerView.setAdapter(adapter);

        waterIntakeViewModel.getIntakeList().observe(getViewLifecycleOwner(), entries -> {
            waterHistory.clear();
            waterHistory.addAll(entries);
            adapter.notifyDataSetChanged();
        });
    }

    private void observeAndRenderChart() {
        waterIntakeViewModel.getDailyGroupedIntake().observe(getViewLifecycleOwner(), intakeMap -> {
            hydrationGoalViewModel.getAllHydrationGoals().observe(getViewLifecycleOwner(), goalMap -> {
                renderCombinedChart(intakeMap, goalMap);
            });
        });
    }

    private void renderCombinedChart(Map<LocalDate, Double> intakeMap, Map<LocalDate, Double> goalMap) {
        List<LocalDate> allDates = new ArrayList<>(new TreeSet<>(goalMap.keySet()));
        allDates.addAll(intakeMap.keySet());
        Set<LocalDate> uniqueSortedDates = new TreeSet<>(allDates);

        List<BarEntry> goalEntries = new ArrayList<>();
        List<Entry> intakeEntries = new ArrayList<>();
        List<String> dateLabels = new ArrayList<>();

        int index = 0;
        for (LocalDate date : uniqueSortedDates) {
            float goal = goalMap.getOrDefault(date, 0.0).floatValue();
            float intake = intakeMap.getOrDefault(date, 0.0).floatValue();

            goalEntries.add(new BarEntry(index, goal));
            intakeEntries.add(new Entry(index, intake));
            dateLabels.add(date.getMonthValue() + "/" + date.getDayOfMonth());
            index++;
        }

        // Bar dataset
        BarDataSet barDataSet = new BarDataSet(goalEntries, "Hydration Goal");
        barDataSet.setColor(ContextCompat.getColor(requireContext(), R.color.light_blue));
        barDataSet.setValueTextSize(10f);

        // Line dataset
        LineDataSet lineDataSet = new LineDataSet(intakeEntries, "Water Intake");
        lineDataSet.setColor(ContextCompat.getColor(requireContext(), R.color.dark_blue));
        lineDataSet.setLineWidth(2f);
        lineDataSet.setCircleRadius(4f);
        lineDataSet.setDrawValues(false);

        // Combine
        CombinedData data = new CombinedData();
        data.setData(new BarData(barDataSet));
        data.setData(new LineData(lineDataSet));

        chart.setData(data);

        // Enable horizontal scrolling
        chart.setDragEnabled(true);
        chart.setScaleXEnabled(true); // Optional: allow pinch zoom on X
        chart.setScaleYEnabled(false); // Disable vertical scaling (optional)
        chart.setVisibleXRangeMaximum(7); // Show only 7 entries at a time (adjust to your design)
        chart.moveViewToX(data.getXMax()); // Scroll to latest data

        chart.getDescription().setEnabled(false);
        chart.getLegend().setWordWrapEnabled(true);

        // X-Axis formatting
        XAxis xAxis = chart.getXAxis();
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new com.github.mikephil.charting.formatter.ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int idx = Math.round(value);
                if (idx >= 0 && idx < dateLabels.size()) {
                    return dateLabels.get(idx);
                } else {
                    return "";
                }
            }
        });


        chart.invalidate(); // refresh
    }
}