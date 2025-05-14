package com.shysoftware.h20tracker.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shysoftware.h20tracker.R;

import java.util.List;

public class TipsAdapter extends RecyclerView.Adapter<TipsAdapter.TipsViewHolder> {

    private final List<String> tipList;
    private final List<String> tipDescList;
    private String tipType;

    public TipsAdapter(List<String> tipList, List<String> tipDescList, String tipType) {
        this.tipList = tipList;
        this.tipDescList = tipDescList;
        this.tipType = tipType;
    }

    @NonNull
    @Override
    public TipsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = R.layout.item_hydration_tip;
        if(tipType.equals("health")){
            layout = R.layout.item_health_tip;
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new TipsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TipsViewHolder holder, int position) {
        holder.tipText.setText(tipList.get(position));
        holder.tipDesc.setText(tipDescList.get(position));
    }

    @Override
    public int getItemCount() {
        return tipList.size();
    }

    static class TipsViewHolder extends RecyclerView.ViewHolder {
        TextView tipText, tipDesc;

        public TipsViewHolder(@NonNull View itemView) {
            super(itemView);
            tipText = itemView.findViewById(R.id.tipContent);
            tipDesc = itemView.findViewById(R.id.tipDescription);
        }
    }
}
