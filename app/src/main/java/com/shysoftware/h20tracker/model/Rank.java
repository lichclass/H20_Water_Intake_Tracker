package com.shysoftware.h20tracker.model;

public enum Rank {
    DRIPLET("driplet"),
    SIPPER("sipper"),
    GULPER("gulper"),
    HYDRATION_SEEKER("hydration_seeker"),
    WATER_WARRIOR("water_warrior"),
    AQUA_ACHIEVER("aqua_achiever"),
    HYDRO_HERO("hydro_hero"),
    OCEAN_GUARDIAN("ocean_guardian"),
    LIQUID_LEGEND("liquid_legend"),
    H2OVERLORD("h2overlord");

    private final String value;
    Rank(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
