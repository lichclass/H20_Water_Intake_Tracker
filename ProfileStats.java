package com.shysoftware.h20tracker.model;

public class ProfileStats {
    private double totalWaterLogged;
    private int longestStreak;
    private double averageDailyIntake;

    // Getters and setters
    public double getTotalWaterLogged() {
        return totalWaterLogged;
    }

    public void setTotalWaterLogged(double totalWaterLogged) {
        this.totalWaterLogged = totalWaterLogged;
    }

    public int getLongestStreak() {
        return longestStreak;
    }

    public void setLongestStreak(int longestStreak) {
        this.longestStreak = longestStreak;
    }

    public double getAverageDailyIntake() {
        return averageDailyIntake;
    }

    public void setAverageDailyIntake(double averageDailyIntake) {
        this.averageDailyIntake = averageDailyIntake;
    }
}
