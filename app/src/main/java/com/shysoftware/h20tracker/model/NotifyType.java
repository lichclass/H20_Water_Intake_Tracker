package com.shysoftware.h20tracker.model;

public enum NotifyType {
    REMINDER("reminder"),
    STREAK("streak"),
    GOAL_STATUS("goal_status"),
    SYSTEM("system");

    private final String value;
    NotifyType(String value) { this.value = value; }
    public String getValue() {
        return value;
    }

    public static NotifyType fromValue(String value) {
        for (NotifyType notifyType : NotifyType.values()) {
            if (notifyType.value.equalsIgnoreCase(value)) {
                return notifyType;
            }
        }
        throw new IllegalArgumentException("Unknown notify_type: " + value);
    }
}
