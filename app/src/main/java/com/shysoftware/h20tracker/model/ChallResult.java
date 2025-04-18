package com.shysoftware.h20tracker.model;

public enum ChallResult {
    SUCCESS("success"),
    FAIL("fail"),
    PENDING("pending");

    private final String value;

    ChallResult(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
