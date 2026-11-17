package com.recruitment.model;

import java.util.Objects;

/**
 * Interview - model class (data only).
 * Maps to the matching table in database_schema.sql.
 */
public class Interview {
    private int interviewId;
    private int applicationId;
    private int roundNumber;
    private String roundType;
    private java.sql.Date scheduledDate;
    private java.sql.Time scheduledTime;
    private String mode;
    private String interviewerName;
    private String meetingOrLocation;
    private String status;
    private java.sql.Timestamp createdAt;

    public Interview() {}

    public Interview(int interviewId, int applicationId, int roundNumber, String roundType, java.sql.Date scheduledDate, java.sql.Time scheduledTime, String mode, String interviewerName, String meetingOrLocation, String status, java.sql.Timestamp createdAt) {
        this.interviewId = interviewId;
        this.applicationId = applicationId;
        this.roundNumber = roundNumber;
        this.roundType = roundType;
        this.scheduledDate = scheduledDate;
        this.scheduledTime = scheduledTime;
        this.mode = mode;
        this.interviewerName = interviewerName;
        this.meetingOrLocation = meetingOrLocation;
        this.status = status;
        this.createdAt = createdAt;
    }
    public int getInterviewId() { return interviewId; }
    public void setInterviewId(int interviewId) { this.interviewId = interviewId; }
    public int getApplicationId() { return applicationId; }
    public void setApplicationId(int applicationId) { this.applicationId = applicationId; }
    public int getRoundNumber() { return roundNumber; }
    public void setRoundNumber(int roundNumber) { this.roundNumber = roundNumber; }
    public String getRoundType() { return roundType; }
    public void setRoundType(String roundType) { this.roundType = roundType; }
    public java.sql.Date getScheduledDate() { return scheduledDate; }
    public void setScheduledDate(java.sql.Date scheduledDate) { this.scheduledDate = scheduledDate; }
    public java.sql.Time getScheduledTime() { return scheduledTime; }
    public void setScheduledTime(java.sql.Time scheduledTime) { this.scheduledTime = scheduledTime; }
    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }
    public String getInterviewerName() { return interviewerName; }
    public void setInterviewerName(String interviewerName) { this.interviewerName = interviewerName; }
    public String getMeetingOrLocation() { return meetingOrLocation; }
    public void setMeetingOrLocation(String meetingOrLocation) { this.meetingOrLocation = meetingOrLocation; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public java.sql.Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(java.sql.Timestamp createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Interview{" + "interviewId=" + interviewId + ", " + "applicationId=" + applicationId + ", " + "roundNumber=" + roundNumber + ", " + "roundType=" + roundType + ", " + "scheduledDate=" + scheduledDate + ", " + "scheduledTime=" + scheduledTime + ", " + "mode=" + mode + ", " + "interviewerName=" + interviewerName + ", " + "meetingOrLocation=" + meetingOrLocation + ", " + "status=" + status + ", " + "createdAt=" + createdAt + "}";
    }
}
