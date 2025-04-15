package com.shysoftware.h20tracker.model;

import java.time.ZonedDateTime;

public class WeatherData {
    Integer weatherId;
    String userId;
    Double temperatureC;
    Double humidityPercent;
    WeatherCondition weatherCondition;
    ZonedDateTime createdAt;
    ZonedDateTime updatedAt;

    public WeatherData(Integer weatherId, String userId, Double temperatureC, Double humidityPercent, WeatherCondition weatherCondition, ZonedDateTime createdAt, ZonedDateTime updatedAt) {
        this.weatherId = weatherId;
        this.userId = userId;
        this.temperatureC = temperatureC;
        this.humidityPercent = humidityPercent;
        this.weatherCondition = weatherCondition;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public Double getHumidityPercent() {
        return humidityPercent;
    }

    public void setHumidityPercent(Double humidityPercent) {
        this.humidityPercent = humidityPercent;
    }

    public WeatherCondition getWeatherCondition() {
        return weatherCondition;
    }

    public void setWeatherCondition(WeatherCondition weatherCondition) {
        this.weatherCondition = weatherCondition;
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
