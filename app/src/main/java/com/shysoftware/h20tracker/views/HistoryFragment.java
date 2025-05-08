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
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.Entry;
import com.shysoftware.h20tracker.R;
import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {

    private LineChart chart;
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
        adapter = new HistoryAdapter(waterHistory);
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        historyRecyclerView.setAdapter(adapter);
    }

    private void setupChart() {
        List<Entry> lineEntries = new ArrayList<>();

        // Prepare data for chart
        for (int i = 0; i < waterHistory.size(); i++) {
            WaterEntry entry = waterHistory.get(i);
            String amountStr = entry.amount.replaceAll("[^\\d.]", "");
            float amount = Float.parseFloat(amountStr);
            lineEntries.add(new Entry(i, amount));
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
