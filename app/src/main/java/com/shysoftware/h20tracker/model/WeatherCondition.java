package com.shysoftware.h20tracker.model;

public enum WeatherCondition {
    THUNDERSTORM("thunderstorm"),
    DRIZZLE("drizzle"),
    RAIN("rain"),
    SNOW("snow"),
    ATMOSPHERE("atmosphere"),
    CLEAR("clear"),
    CLOUDS("clouds");

    private final String value;
    WeatherCondition(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static WeatherCondition fromValue(String value) {
        for (WeatherCondition weatherCondition : WeatherCondition.values()) {
            if (weatherCondition.value.equalsIgnoreCase(value)) {
                return weatherCondition;
            }
        }
        throw new IllegalArgumentException("Unknown gender: " + value);
    }
}
