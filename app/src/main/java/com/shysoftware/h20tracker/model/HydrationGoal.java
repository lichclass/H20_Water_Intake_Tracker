package com.shysoftware.h20tracker.model;

import java.time.ZonedDateTime;

public class HydrationGoal {
    Integer goalId;
    String userId;
    Double targetAmountMl;
    Double basisWeight;
    Double basisHumidity;
    Double basisTemp;
    ZonedDateTime forecastDate;
    ZonedDateTime createdAt;

    public HydrationGoal(Integer goalId, String userId, Double targetAmountMl, Double basisWeight, Double basisHumidity, Double basisTemp, ZonedDateTime forecastDate, ZonedDateTime createdAt) {
        this.goalId = goalId;
        this.userId = userId;
        this.targetAmountMl = targetAmountMl;
        this.basisWeight = basisWeight;
        this.basisHumidity = basisHumidity;
        this.basisTemp = basisTemp;
        this.forecastDate = forecastDate;
        this.createdAt = createdAt;
    }

    public Integer getGoalId() {
        return goalId;
    }

    public void setGoalId(Integer goalId) {
        this.goalId = goalId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Double getTargetAmountMl() {
        return targetAmountMl;
    }

    public void setTargetAmountMl(Double targetAmountMl) {
        this.targetAmountMl = targetAmountMl;
    }

    public Double getBasisWeight() {
        return basisWeight;
    }

    public void setBasisWeight(Double basisWeight) {
        this.basisWeight = basisWeight;
    }

    public Double getBasisHumidity() {
        return basisHumidity;
    }

    public void setBasisHumidity(Double basisHumidity) {
        this.basisHumidity = basisHumidity;
    }

    public Double getBasisTemp() {
        return basisTemp;
    }

    public void setBasisTemp(Double basisTemp) {
        this.basisTemp = basisTemp;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
