package com.shysoftware.h20tracker.model;

import java.time.ZonedDateTime;

public class ChallengesAI {
    Integer challengeId;
    String userId;
    Double targetAmountML;
    String title;
    String msg;
    Integer reward;
    Boolean isDone;
    ChallResult result;
    ZonedDateTime date;
    ZonedDateTime createdAt;
    ZonedDateTime updatedAt;
    ChallengeType challengeType;

    public ChallengesAI(Integer challengeId, String userId, Double targetAmountML, String title, String msg, Integer reward, Boolean isDone, ChallResult result, ZonedDateTime date, ZonedDateTime createdAt, ZonedDateTime updatedAt, ChallengeType challengeType) {
        this.challengeId = challengeId;
        this.userId = userId;
        this.targetAmountML = targetAmountML;
        this.title = title;
        this.msg = msg;
        this.reward = reward;
        this.isDone = isDone;
        this.result = result;
        this.date = date;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.challengeType = challengeType;
    }

    public Integer getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(Integer challengeId) {
        this.challengeId = challengeId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Double getTargetAmountML() {
        return targetAmountML;
    }

    public void setTargetAmountML(Double targetAmountML) {
        this.targetAmountML = targetAmountML;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Integer getReward() {
        return reward;
    }

    public void setReward(Integer reward) {
        this.reward = reward;
    }

    public Boolean getDone() {
        return isDone;
    }

    public void setDone(Boolean done) {
        isDone = done;
    }

    public ChallResult getResult() {
        return result;
    }

    public void setResult(ChallResult result) {
        this.result = result;
    }

    public ZonedDateTime getDate() {
        return date;
    }

    public void setDate(ZonedDateTime date) {
        this.date = date;
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

    public ChallengeType getChallengeType() {
        return challengeType;
    }

    public void setChallengeType(ChallengeType challengeType) {
        this.challengeType = challengeType;
    }
}
