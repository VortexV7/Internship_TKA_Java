package com.recruitment.model;

import java.util.Objects;

/**
 * ApplicationHistory - model class (data only).
 * Maps to the matching table in database_schema.sql.
 */
public class ApplicationHistory {
    private long historyId;
    private int applicationId;
    private String oldStatus;
    private String newStatus;
    private String remarks;
    private int changedBy;
    private java.sql.Timestamp changedAt;

    public ApplicationHistory() {}

    public ApplicationHistory(long historyId, int applicationId, String oldStatus, String newStatus, String remarks, int changedBy, java.sql.Timestamp changedAt) {
        this.historyId = historyId;
        this.applicationId = applicationId;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.remarks = remarks;
        this.changedBy = changedBy;
        this.changedAt = changedAt;
    }
    public long getHistoryId() { return historyId; }
    public void setHistoryId(long historyId) { this.historyId = historyId; }
    public int getApplicationId() { return applicationId; }
    public void setApplicationId(int applicationId) { this.applicationId = applicationId; }
    public String getOldStatus() { return oldStatus; }
    public void setOldStatus(String oldStatus) { this.oldStatus = oldStatus; }
    public String getNewStatus() { return newStatus; }
    public void setNewStatus(String newStatus) { this.newStatus = newStatus; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public int getChangedBy() { return changedBy; }
    public void setChangedBy(int changedBy) { this.changedBy = changedBy; }
    public java.sql.Timestamp getChangedAt() { return changedAt; }
    public void setChangedAt(java.sql.Timestamp changedAt) { this.changedAt = changedAt; }

    @Override
    public String toString() {
        return "ApplicationHistory{" + "historyId=" + historyId + ", " + "applicationId=" + applicationId + ", " + "oldStatus=" + oldStatus + ", " + "newStatus=" + newStatus + ", " + "remarks=" + remarks + ", " + "changedBy=" + changedBy + ", " + "changedAt=" + changedAt + "}";
    }
}
