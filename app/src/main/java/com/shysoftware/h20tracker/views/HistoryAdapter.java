package com.shysoftware.h20tracker.views;

import com.shysoftware.h20tracker.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    List<WaterEntry> entries;

    public HistoryAdapter(Context ctx, List<WaterEntry> entries) {
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
                .inflate(R.layout.item_history, parent, false); // <- Use the right layout here
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull HistoryAdapter.ViewHolder holder, int position) {
        WaterEntry entry = entries.get(position);
        holder.amountText.setText(entry.amount);
        holder.dateText.setText(entry.dateTime);
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }
}