package com.shysoftware.h20tracker.model;
// Test Comment
public enum Gender {
    MALE("male"),
    FEMALE("female"),
    NON_BINARY("non_binary"),
    OTHER("other");

    private final String value;
    Gender(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
