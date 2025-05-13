package com.shysoftware.h20tracker.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shysoftware.h20tracker.R;
import com.shysoftware.h20tracker.model.Notification;
import com.shysoftware.h20tracker.model.NotifyType;

import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    List<Notification> notifications;

    public NotificationAdapter(Context context, List<Notification> notifications) {
        this.notifications = notifications;
    }

    @Override
    public int getItemViewType(int position) {
        NotifyType type = notifications.get(position).getNotifyType();
        if (type == NotifyType.REMINDER) {
            return R.layout.notification_item_reminder;
        } else if (type == NotifyType.GOAL_STATUS) {
            return R.layout.notification_item_goal;
        } else {
            return R.layout.notification_item_system;
        }
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position){
        holder.notificationTitle.setText(notifications.get(position).getTitle());
        holder.notificationMessage.setText(notifications.get(position).getMessage());

        ZonedDateTime time = notifications.get(position).getCreatedAt();
        holder.notificationTime.setText(getRelativeTime(time));
    }

    @Override
    public int getItemCount() {
        if (notifications == null) return 0;
        return notifications.size();
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

    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView notificationTitle, notificationMessage, notificationTime;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            notificationTitle = itemView.findViewById(R.id.notificationTitle);
            notificationMessage = itemView.findViewById(R.id.notificationMessage);
            notificationTime = itemView.findViewById(R.id.notificationTime);
        }
    }
}
