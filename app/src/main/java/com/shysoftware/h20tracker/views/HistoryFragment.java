package com.shysoftware.h20tracker.views;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.Entry;
import com.shysoftware.h20tracker.R;
import com.shysoftware.h20tracker.model.WaterIntake;
import com.shysoftware.h20tracker.viewmodel.WaterIntakeViewModel;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    private LineChart chart;
    private RecyclerView historyRecyclerView;
    TextView historyDateToday;
    private HistoryAdapter adapter;
    private List<WaterIntake> waterHistory;
    private WaterIntakeViewModel waterIntakeViewModel;

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

        waterIntakeViewModel = new ViewModelProvider(requireActivity()).get(WaterIntakeViewModel.class);

        historyDateToday = view.findViewById(R.id.history_date_today);
        chart = view.findViewById(R.id.chart);
        historyRecyclerView = view.findViewById(R.id.historyRecyclerView);

        // Set up history date
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
        historyDateToday.setText(LocalDate.now().format(formatter));

        // Set up history and chart
        setupHistory();
        setupChart();
    }

    private void setupHistory() {
        // Sample water intake history
        waterHistory = new ArrayList<>();
        adapter = new HistoryAdapter(waterHistory);
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        historyRecyclerView.setAdapter(adapter);

        waterIntakeViewModel.getIntakeList().observe(getViewLifecycleOwner(), entries -> {
            waterHistory.clear();
            waterHistory.addAll(entries);
            adapter.notifyDataSetChanged();

            setupChart();
        });
    }

    private void setupChart() {
        List<Entry> lineEntries = new ArrayList<>();

        // Prepare data for chart
        for (int i = 0; i < waterHistory.size(); i++) {
            WaterIntake entry = waterHistory.get(i);
            Double amount = entry.getAmount();
            lineEntries.add(new Entry(i, amount.floatValue()));
        }

        // Set up LineData
        LineDataSet lineDataSet = new LineDataSet(lineEntries, "Water Intake");
        lineDataSet.setColor(ContextCompat.getColor(requireContext(), android.R.color.holo_blue_dark));
        lineDataSet.setLineWidth(2f);
        lineDataSet.setCircleRadius(3f);
        lineDataSet.setDrawCircles(true);
        lineDataSet.setDrawValues(false);

        LineData lineData = new LineData(lineDataSet);
        chart.setData(lineData);
        chart.invalidate(); // Refresh the chart
    }
}
