// HistoryAdapter.java
package com.shysoftware.h20tracker.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shysoftware.h20tracker.R;
import com.shysoftware.h20tracker.model.WaterIntake;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<WaterIntake> entries;

    public HistoryAdapter(List<WaterIntake> entries) {
        this.entries = entries;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView amountText, dateText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            amountText = itemView.findViewById(R.id.amountText);
            dateText = itemView.findViewById(R.id.dateText);
        }
    }

    @NonNull
    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.ViewHolder holder, int position) {
        WaterIntake entry = entries.get(position);
        holder.amountText.setText(entry.getAmount().toString());
        holder.dateText.setText(entry.getDate().toString());
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }
}
