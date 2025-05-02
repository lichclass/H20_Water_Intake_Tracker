package com.shysoftware.h20tracker.model;

import android.annotation.SuppressLint;

public class Location {
    String place;
    Double longitude;
    Double latitude;

    public Location(String place, Double longitude, Double latitude) {
        this.place = place;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public String toString() {
        return String.format("%s (%.5f, %.5f)", place, latitude, longitude);
    }
}
