package com.shysoftware.h20tracker.model;

import java.time.LocalDate;
import java.time.ZonedDateTime;

public class WeatherData {
    Integer weatherId;
    String userId;
    Double temperatureC;
    Integer humidityPercent;
    WeatherCondition weatherCondition;
    LocalDate date;
    ZonedDateTime createdAt;
    ZonedDateTime updatedAt;

    public WeatherData(Integer weatherId, String userId, Double temperatureC, Integer humidityPercent, WeatherCondition weatherCondition, LocalDate date, ZonedDateTime createdAt, ZonedDateTime updatedAt) {
        this.weatherId = weatherId;
        this.userId = userId;
        this.temperatureC = temperatureC;
        this.humidityPercent = humidityPercent;
        this.weatherCondition = weatherCondition;
        this.date = date;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public WeatherData(){
        //
    }

    public Integer getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(Integer weatherId) {
        this.weatherId = weatherId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Double getTemperatureC() {
        return temperatureC;
    }

    public void setTemperatureC(Double temperatureC) {
        this.temperatureC = temperatureC;
    }

    public Integer getHumidityPercent() {
        return humidityPercent;
    }

    public void setHumidityPercent(Integer humidityPercent) {
        this.humidityPercent = humidityPercent;
    }

    public WeatherCondition getWeatherCondition() {
        return weatherCondition;
    }

    public void setWeatherCondition(WeatherCondition weatherCondition) {
        this.weatherCondition = weatherCondition;
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
