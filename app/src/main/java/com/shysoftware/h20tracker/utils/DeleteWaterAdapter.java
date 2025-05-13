package com.shysoftware.h20tracker.utils;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shysoftware.h20tracker.R;
import com.shysoftware.h20tracker.model.WaterIntake;

import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;

public class DeleteWaterAdapter extends RecyclerView.Adapter<DeleteWaterAdapter.ViewHolder>{

    public interface OnDeleteListener {
        void deleteIntakeEntry(WaterIntake intake);
    }

    private final Context ctx;
    private final List<WaterIntake> waterIntakes;
    private final OnDeleteListener deleteListener;

    public DeleteWaterAdapter(Context ctx, List<WaterIntake> waterIntakes, OnDeleteListener deleteListener) {
        this.ctx = ctx;
        this.waterIntakes = waterIntakes;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public DeleteWaterAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_water_record, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    @Override
    public void onBindViewHolder(@NonNull DeleteWaterAdapter.ViewHolder holder, int position) {
        WaterIntake intake = waterIntakes.get(position);
        holder.amountText.setText(String.format("You drank %.0fmL", intake.getAmount()));
        holder.timeText.setText(getRelativeTime(intake.getCreatedAt()));
    }

    @Override
    public int getItemCount() {
        if (waterIntakes == null) return 0;
        return waterIntakes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView amountText, timeText;
        ImageButton deleteBtn;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            amountText = itemView.findViewById(R.id.record_water_amount);
            timeText = itemView.findViewById(R.id.record_time);
            deleteBtn = itemView.findViewById(R.id.delete_record_btn);

            deleteBtn.setOnClickListener(v -> {
                    Dialog confirm = new Dialog(ctx);
                    confirm.setContentView(R.layout.modal_confirm_delete);

                    Button cancelBtn = confirm.findViewById(R.id.cancelButton);
                    Button deleteBtn = confirm.findViewById(R.id.deleteButton);

                    cancelBtn.setOnClickListener(v1 -> {
                        confirm.dismiss();
                    });

                    deleteBtn.setOnClickListener(v1 -> {
                        int pos = getAdapterPosition();
                        WaterIntake toDelete = waterIntakes.get(pos);

                        deleteListener.deleteIntakeEntry(toDelete);

                        waterIntakes.remove(pos);
                        notifyItemRemoved(pos);

                        confirm.dismiss();
                    });

                    confirm.show();
            });
        }
    }

    private String getRelativeTime(ZonedDateTime time) {
        Instant then = time.toInstant();
        Instant now  = Instant.now();
        long seconds = Duration.between(then, now).getSeconds();

        if (seconds < 60) {
            return "few seconds ago";
        } else if (seconds < 3_600) {
            long mins = seconds / 60;
            return mins == 1 ? "1 minute ago" : mins + " minutes ago";
        } else if (seconds < 86_400) {
            long hrs = seconds / 3_600;
            return hrs == 1 ? "1 hour ago" : hrs + " hours ago";
        } else {
            long days = seconds / 86_400;
            return days == 1 ? "1 day ago" : days + " days ago";
        }
    }
}
