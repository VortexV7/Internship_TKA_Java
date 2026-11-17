package com.recruitment.model;

import java.util.Objects;

/**
 * Application - model class (data only).
 * Maps to the matching table in database_schema.sql.
 */
public class Application {
    private int applicationId;
    private String applicationCode;
    private int candidateId;
    private int jobId;
    private java.sql.Timestamp appliedAt;
    private String eligibilityStatus;
    private String eligibilityReason;
    private String applicationStatus;
    private String source;
    private int assignedRecruiterId;
    private java.sql.Timestamp updatedAt;

    public Application() {}

    public Application(int applicationId, String applicationCode, int candidateId, int jobId, java.sql.Timestamp appliedAt, String eligibilityStatus, String eligibilityReason, String applicationStatus, String source, int assignedRecruiterId, java.sql.Timestamp updatedAt) {
        this.applicationId = applicationId;
        this.applicationCode = applicationCode;
        this.candidateId = candidateId;
        this.jobId = jobId;
        this.appliedAt = appliedAt;
        this.eligibilityStatus = eligibilityStatus;
        this.eligibilityReason = eligibilityReason;
        this.applicationStatus = applicationStatus;
        this.source = source;
        this.assignedRecruiterId = assignedRecruiterId;
        this.updatedAt = updatedAt;
    }
    public int getApplicationId() { return applicationId; }
    public void setApplicationId(int applicationId) { this.applicationId = applicationId; }
    public String getApplicationCode() { return applicationCode; }
    public void setApplicationCode(String applicationCode) { this.applicationCode = applicationCode; }
    public int getCandidateId() { return candidateId; }
    public void setCandidateId(int candidateId) { this.candidateId = candidateId; }
    public int getJobId() { return jobId; }
    public void setJobId(int jobId) { this.jobId = jobId; }
    public java.sql.Timestamp getAppliedAt() { return appliedAt; }
    public void setAppliedAt(java.sql.Timestamp appliedAt) { this.appliedAt = appliedAt; }
    public String getEligibilityStatus() { return eligibilityStatus; }
    public void setEligibilityStatus(String eligibilityStatus) { this.eligibilityStatus = eligibilityStatus; }
    public String getEligibilityReason() { return eligibilityReason; }
    public void setEligibilityReason(String eligibilityReason) { this.eligibilityReason = eligibilityReason; }
    public String getApplicationStatus() { return applicationStatus; }
    public void setApplicationStatus(String applicationStatus) { this.applicationStatus = applicationStatus; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public int getAssignedRecruiterId() { return assignedRecruiterId; }
    public void setAssignedRecruiterId(int assignedRecruiterId) { this.assignedRecruiterId = assignedRecruiterId; }
    public java.sql.Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(java.sql.Timestamp updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "Application{" + "applicationId=" + applicationId + ", " + "applicationCode=" + applicationCode + ", " + "candidateId=" + candidateId + ", " + "jobId=" + jobId + ", " + "appliedAt=" + appliedAt + ", " + "eligibilityStatus=" + eligibilityStatus + ", " + "eligibilityReason=" + eligibilityReason + ", " + "applicationStatus=" + applicationStatus + ", " + "source=" + source + ", " + "assignedRecruiterId=" + assignedRecruiterId + ", " + "updatedAt=" + updatedAt + "}";
    }
}
