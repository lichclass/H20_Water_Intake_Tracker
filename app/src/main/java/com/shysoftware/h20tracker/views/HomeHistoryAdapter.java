package com.shysoftware.h20tracker.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.shysoftware.h20tracker.R;
import com.shysoftware.h20tracker.model.WaterIntake;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class HomeHistoryAdapter extends ArrayAdapter<WaterIntake> {
    private final LayoutInflater inflater;

    public HomeHistoryAdapter(@NonNull Context ctx, @NonNull List<WaterIntake> data) {
        super(ctx, 0, data);
        inflater = LayoutInflater.from(ctx);
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.home_item_history, parent, false);
        }

        TextView amount    = convertView.findViewById(R.id.amount_txt);
        TextView timestamp = convertView.findViewById(R.id.date_txt);

        WaterIntake entry = getItem(position);
        if (entry != null) {
            amount.setText(String.format(Locale.getDefault(), "%.0f mL", entry.getAmount()));

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy, hh:mm a");
            ZonedDateTime manilaTime = entry.getCreatedAt().withZoneSameInstant(ZoneId.of("Asia/Manila"));

            timestamp.setText(manilaTime.format(formatter));
        }

        return convertView;
    }
}

