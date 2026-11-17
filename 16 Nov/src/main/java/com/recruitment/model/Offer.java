package com.recruitment.model;

import java.util.Objects;

/**
 * Offer - model class (data only).
 * Maps to the matching table in database_schema.sql.
 */
public class Offer {
    private int offerId;
    private String offerCode;
    private int applicationId;
    private String designation;
    private double offeredPackageLpa;
    private java.sql.Date offerDate;
    private java.sql.Date expectedJoiningDate;
    private String offerStatus;
    private String remarks;
    private java.sql.Timestamp createdAt;

    public Offer() {}

    public Offer(int offerId, String offerCode, int applicationId, String designation, double offeredPackageLpa, java.sql.Date offerDate, java.sql.Date expectedJoiningDate, String offerStatus, String remarks, java.sql.Timestamp createdAt) {
        this.offerId = offerId;
        this.offerCode = offerCode;
        this.applicationId = applicationId;
        this.designation = designation;
        this.offeredPackageLpa = offeredPackageLpa;
        this.offerDate = offerDate;
        this.expectedJoiningDate = expectedJoiningDate;
        this.offerStatus = offerStatus;
        this.remarks = remarks;
        this.createdAt = createdAt;
    }
    public int getOfferId() { return offerId; }
    public void setOfferId(int offerId) { this.offerId = offerId; }
    public String getOfferCode() { return offerCode; }
    public void setOfferCode(String offerCode) { this.offerCode = offerCode; }
    public int getApplicationId() { return applicationId; }
    public void setApplicationId(int applicationId) { this.applicationId = applicationId; }
    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }
    public double getOfferedPackageLpa() { return offeredPackageLpa; }
    public void setOfferedPackageLpa(double offeredPackageLpa) { this.offeredPackageLpa = offeredPackageLpa; }
    public java.sql.Date getOfferDate() { return offerDate; }
    public void setOfferDate(java.sql.Date offerDate) { this.offerDate = offerDate; }
    public java.sql.Date getExpectedJoiningDate() { return expectedJoiningDate; }
    public void setExpectedJoiningDate(java.sql.Date expectedJoiningDate) { this.expectedJoiningDate = expectedJoiningDate; }
    public String getOfferStatus() { return offerStatus; }
    public void setOfferStatus(String offerStatus) { this.offerStatus = offerStatus; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public java.sql.Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(java.sql.Timestamp createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Offer{" + "offerId=" + offerId + ", " + "offerCode=" + offerCode + ", " + "applicationId=" + applicationId + ", " + "designation=" + designation + ", " + "offeredPackageLpa=" + offeredPackageLpa + ", " + "offerDate=" + offerDate + ", " + "expectedJoiningDate=" + expectedJoiningDate + ", " + "offerStatus=" + offerStatus + ", " + "remarks=" + remarks + ", " + "createdAt=" + createdAt + "}";
    }
}
