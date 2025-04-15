package com.shysoftware.h20tracker.model;

import com.google.gson.annotations.SerializedName;

import java.time.ZonedDateTime;

public class AuthUser {
    private String id;
    private String email;
    @SerializedName("createdAt")
    private ZonedDateTime createdAt;
    @SerializedName("lastSignInAt")
    private ZonedDateTime lastSignInAt;

    public AuthUser(String id, String email, ZonedDateTime createdAt, ZonedDateTime lastSignInAt) {
        this.id = id;
        this.email = email;
        this.createdAt = createdAt;
        this.lastSignInAt = lastSignInAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ZonedDateTime getLastSignInAt() {
        return lastSignInAt;
    }

    public void setLastSignInAt(ZonedDateTime lastSignInAt) {
        this.lastSignInAt = lastSignInAt;
    }
}
