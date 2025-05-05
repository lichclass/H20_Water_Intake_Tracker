package com.shysoftware.h20tracker.views;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.CombinedChart;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.github.mikephil.charting.data.*;

import com.github.mikephil.charting.formatter.ValueFormatter;
import com.shysoftware.h20tracker.R;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    private CombinedChart chart;
    private RecyclerView historyRecyclerView;
    private HistoryAdapter adapter;
    private List<WaterEntry> waterHistory;

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /* ------------------ Place All View Logic Here ------------------ */

        chart = view.findViewById(R.id.chart);
        historyRecyclerView = view.findViewById(R.id.historyRecyclerView);


        // Set up history and chart
        setupHistory();
        setupChart();
    }
    private void setupHistory() {
        // Sample water intake history
        waterHistory = new ArrayList<>();
        waterHistory.add(new WaterEntry("500 mL", "04/09/2025, 11:14 PM"));
        waterHistory.add(new WaterEntry("7000 mL", "04/09/2025, 10:17 PM"));
        waterHistory.add(new WaterEntry("250 mL", "04/09/2025, 9:45 PM"));
        waterHistory.add(new WaterEntry("1000 mL", "04/09/2025, 8:30 PM"));

        // Set up RecyclerView
        adapter = new HistoryAdapter(requireActivity(), waterHistory);
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        historyRecyclerView.setAdapter(adapter);
    }

    private void setupChart() {
        List<BarEntry> barEntries = new ArrayList<>();
        List<String> timeLabels = new ArrayList<>();

        // Prepare data for chart
        for (int i = 0; i < waterHistory.size(); i++) {
            WaterEntry entry = waterHistory.get(i);

            // Extract water amount and parse to float
            String amountStr = entry.amount.replaceAll("[^\\d.]", "");
            float amount = Float.parseFloat(amountStr);

            // Add bar entry to chart data
            barEntries.add(new BarEntry(i, amount));

            // Add time to labels for the x-axis
            String timePart = entry.dateTime.split(",")[1].trim(); // Get time from date string
            timeLabels.add(timePart);
        }

        // Set up BarData
        BarDataSet barDataSet = new BarDataSet(barEntries, "Water Intake");
        barDataSet.setColor(ContextCompat.getColor(requireContext(), android.R.color.holo_blue_light));  // Replaced getColor with ContextCompat.getColor
        barDataSet.setValueTextColor(ContextCompat.getColor(requireContext(), android.R.color.black));  // Replaced getColor with ContextCompat.getColor
        barDataSet.setValueTextSize(12f);

        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.45f);

        // Set up line (goal) data
        List<Entry> lineEntries = new ArrayList<>();
        float goal = 2000f;  // Daily goal
        lineEntries.add(new Entry(0, goal));
        lineEntries.add(new Entry(barEntries.size() - 1, goal));

        LineDataSet lineDataSet = new LineDataSet(lineEntries, "Daily Goal");
        lineDataSet.setColor(ContextCompat.getColor(requireContext(), android.R.color.holo_blue_dark));  // Replaced getColor with ContextCompat.getColor
        lineDataSet.setLineWidth(2f);
        lineDataSet.setCircleRadius(3f);
        lineDataSet.setDrawCircles(true);
        lineDataSet.setDrawValues(false);

        LineData lineData = new LineData(lineDataSet);

        // Combine Bar and Line data
        CombinedData combinedData = new CombinedData();
        combinedData.setData(barData);
        combinedData.setData(lineData);

        // Set x-axis labels using IndexAxisValueFormatter
        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(timeLabels));

        // Customize chart appearance
        chart.getAxisRight().setEnabled(false);
        chart.getAxisLeft().setAxisMinimum(0f);
        chart.getDescription().setEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setDrawBarShadow(false);
        chart.setDrawBorders(false);

        // Set combined data to chart and animate
        chart.setData(combinedData);
        chart.animateY(1000);
        chart.invalidate();
    }

    // Formatter for x-axis labels
    private class IndexAxisValueFormatter extends ValueFormatter {
        private List<String> labels;

        public IndexAxisValueFormatter(List<String> labels) {
            this.labels = labels;
        }

        @Override
        public String getFormattedValue(float value) {
            int index = (int) value;
            if (index < labels.size()) {
                return labels.get(index);
            }
            return "";
        }
    }
}