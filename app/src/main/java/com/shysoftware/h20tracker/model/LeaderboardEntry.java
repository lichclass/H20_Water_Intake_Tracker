package com.shysoftware.h20tracker.model;

// ------------------------- RECYCLER VIEW TEST -------------------------
public class LeaderboardEntry {

    private String name;
    private int score;
    private int rank;
    private int imageResId;


    public LeaderboardEntry(String name, int score, int rank, int pfp) {
        this.name = name;
        this.score = score;
        this.rank = rank;
        this.imageResId = pfp;
    }

    public String getName() { return name; }
    public int getScore() { return score; }
    public int getRank() { return rank; }
    public int getImageResId() { return imageResId; }
}

