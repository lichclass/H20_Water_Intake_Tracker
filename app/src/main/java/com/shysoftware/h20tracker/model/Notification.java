package com.shysoftware.h20tracker.model;

import java.time.ZonedDateTime;

public class Notification {
    Integer notificationId;
    String userId;
    NotifyType notifyType;
    String title;
    String message;
    Boolean isSeen;
    ZonedDateTime createdAt;
    ZonedDateTime updatedAt;

    public Notification(Integer notificationId, String userId, NotifyType notifyType, String title, String message, Boolean isSeen, ZonedDateTime createdAt, ZonedDateTime updatedAt) {
        this.notificationId = notificationId;
        this.userId = userId;
        this.notifyType = notifyType;
        this.title = title;
        this.message = message;
        this.isSeen = isSeen;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Notification() {

    }

    public ZonedDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(ZonedDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Boolean getSeen() {
        return isSeen;
    }

    public void setSeen(Boolean seen) {
        isSeen = seen;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public NotifyType getNotifyType() {
        return notifyType;
    }

    public void setNotifyType(NotifyType notifyType) {
        this.notifyType = notifyType;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Integer getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Integer notificationId) {
        this.notificationId = notificationId;
    }
}
