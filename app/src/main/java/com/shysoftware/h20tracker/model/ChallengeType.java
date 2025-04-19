package com.shysoftware.h20tracker.model;

public enum ChallengeType {
    DAILY("daily"),
    WEEKLY("weekly");

    private final String value;

    ChallengeType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
