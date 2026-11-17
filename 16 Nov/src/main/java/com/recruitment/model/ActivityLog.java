package com.recruitment.model;

import java.util.Objects;

/**
 * ActivityLog - model class (data only).
 * Maps to the matching table in database_schema.sql.
 */
public class ActivityLog {
    private long logId;
    private int userId;
    private String moduleName;
    private String actionName;
    private String recordId;
    private String description;
    private java.sql.Timestamp loggedAt;

    public ActivityLog() {}

    public ActivityLog(long logId, int userId, String moduleName, String actionName, String recordId, String description, java.sql.Timestamp loggedAt) {
        this.logId = logId;
        this.userId = userId;
        this.moduleName = moduleName;
        this.actionName = actionName;
        this.recordId = recordId;
        this.description = description;
        this.loggedAt = loggedAt;
    }
    public long getLogId() { return logId; }
    public void setLogId(long logId) { this.logId = logId; }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getModuleName() { return moduleName; }
    public void setModuleName(String moduleName) { this.moduleName = moduleName; }
    public String getActionName() { return actionName; }
    public void setActionName(String actionName) { this.actionName = actionName; }
    public String getRecordId() { return recordId; }
    public void setRecordId(String recordId) { this.recordId = recordId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public java.sql.Timestamp getLoggedAt() { return loggedAt; }
    public void setLoggedAt(java.sql.Timestamp loggedAt) { this.loggedAt = loggedAt; }

    @Override
    public String toString() {
        return "ActivityLog{" + "logId=" + logId + ", " + "userId=" + userId + ", " + "moduleName=" + moduleName + ", " + "actionName=" + actionName + ", " + "recordId=" + recordId + ", " + "description=" + description + ", " + "loggedAt=" + loggedAt + "}";
    }
}
