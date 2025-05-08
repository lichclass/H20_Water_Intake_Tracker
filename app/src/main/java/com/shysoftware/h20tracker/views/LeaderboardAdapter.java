package com.shysoftware.h20tracker.views;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.shysoftware.h20tracker.model.LeaderboardEntry;
import com.shysoftware.h20tracker.R;

import java.util.List;

// ------------------------- RECYCLER VIEW TEST -------------------------
public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {
    private List<LeaderboardEntry> entries;

    public LeaderboardAdapter(List<LeaderboardEntry> entries) {
        this.entries = entries;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView rankText, usernameText, scoreText;
        ShapeableImageView pfpImg;

        public ViewHolder(View itemView) {
            super(itemView);
            rankText = itemView.findViewById(R.id.rank);
            usernameText = itemView.findViewById(R.id.username);
            scoreText = itemView.findViewById(R.id.score);
            pfpImg = itemView.findViewById(R.id.pfp);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.leaderboard_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        LeaderboardEntry entry = entries.get(position);
        holder.rankText.setText(String.valueOf(entry.getRank()));
        holder.usernameText.setText(entry.getName());
        holder.scoreText.setText(String.valueOf(entry.getScore()));
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }
}

