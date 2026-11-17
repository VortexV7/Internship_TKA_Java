package com.recruitment.model;

import java.util.Objects;

/**
 * Placement - model class (data only).
 * Maps to the matching table in database_schema.sql.
 */
public class Placement {
    private int placementId;
    private int candidateId;
    private int companyId;
    private int jobId;
    private int offerId;
    private String designation;
    private double finalPackageLpa;
    private java.sql.Date expectedJoiningDate;
    private java.sql.Date actualJoiningDate;
    private String joiningStatus;
    private String placementStatus;
    private java.sql.Timestamp createdAt;

    public Placement() {}

    public Placement(int placementId, int candidateId, int companyId, int jobId, int offerId, String designation, double finalPackageLpa, java.sql.Date expectedJoiningDate, java.sql.Date actualJoiningDate, String joiningStatus, String placementStatus, java.sql.Timestamp createdAt) {
        this.placementId = placementId;
        this.candidateId = candidateId;
        this.companyId = companyId;
        this.jobId = jobId;
        this.offerId = offerId;
        this.designation = designation;
        this.finalPackageLpa = finalPackageLpa;
        this.expectedJoiningDate = expectedJoiningDate;
        this.actualJoiningDate = actualJoiningDate;
        this.joiningStatus = joiningStatus;
        this.placementStatus = placementStatus;
        this.createdAt = createdAt;
    }
    public int getPlacementId() { return placementId; }
    public void setPlacementId(int placementId) { this.placementId = placementId; }
    public int getCandidateId() { return candidateId; }
    public void setCandidateId(int candidateId) { this.candidateId = candidateId; }
    public int getCompanyId() { return companyId; }
    public void setCompanyId(int companyId) { this.companyId = companyId; }
    public int getJobId() { return jobId; }
    public void setJobId(int jobId) { this.jobId = jobId; }
    public int getOfferId() { return offerId; }
    public void setOfferId(int offerId) { this.offerId = offerId; }
    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }
    public double getFinalPackageLpa() { return finalPackageLpa; }
    public void setFinalPackageLpa(double finalPackageLpa) { this.finalPackageLpa = finalPackageLpa; }
    public java.sql.Date getExpectedJoiningDate() { return expectedJoiningDate; }
    public void setExpectedJoiningDate(java.sql.Date expectedJoiningDate) { this.expectedJoiningDate = expectedJoiningDate; }
    public java.sql.Date getActualJoiningDate() { return actualJoiningDate; }
    public void setActualJoiningDate(java.sql.Date actualJoiningDate) { this.actualJoiningDate = actualJoiningDate; }
    public String getJoiningStatus() { return joiningStatus; }
    public void setJoiningStatus(String joiningStatus) { this.joiningStatus = joiningStatus; }
    public String getPlacementStatus() { return placementStatus; }
    public void setPlacementStatus(String placementStatus) { this.placementStatus = placementStatus; }
    public java.sql.Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(java.sql.Timestamp createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Placement{" + "placementId=" + placementId + ", " + "candidateId=" + candidateId + ", " + "companyId=" + companyId + ", " + "jobId=" + jobId + ", " + "offerId=" + offerId + ", " + "designation=" + designation + ", " + "finalPackageLpa=" + finalPackageLpa + ", " + "expectedJoiningDate=" + expectedJoiningDate + ", " + "actualJoiningDate=" + actualJoiningDate + ", " + "joiningStatus=" + joiningStatus + ", " + "placementStatus=" + placementStatus + ", " + "createdAt=" + createdAt + "}";
    }
}
