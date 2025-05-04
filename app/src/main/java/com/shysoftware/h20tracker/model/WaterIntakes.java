package com.shysoftware.h20tracker.model;

import java.time.LocalDate;
import java.time.ZonedDateTime;

public class WaterIntakes {
    Integer intakeId;
    String userId;
    Double amount;
    LocalDate date;
    ZonedDateTime createdAt;
    ZonedDateTime updatedAt;

    public WaterIntakes(Integer intakeId, String userId, Double amount, LocalDate date, ZonedDateTime createdAt, ZonedDateTime updatedAt) {
        this.intakeId = intakeId;
        this.userId = userId;
        this.amount = amount;
        this.date = date;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Integer getIntakeId() {
        return intakeId;
    }

    public void setIntakeId(Integer intakeId) {
        this.intakeId = intakeId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
