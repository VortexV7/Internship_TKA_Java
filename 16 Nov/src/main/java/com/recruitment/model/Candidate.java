package com.recruitment.model;

import java.util.Objects;

/**
 * Candidate - model class (data only).
 * Maps to the matching table in database_schema.sql.
 */
public class Candidate {
    private int candidateId;
    private String candidateCode;
    private String fullName;
    private String email;
    private String mobile;
    private java.sql.Date dateOfBirth;
    private String gender;
    private String qualification;
    private String college;
    private String university;
    private int passingYear;
    private double percentage;
    private double cgpa;
    private int backlogCount;
    private String primaryTechnology;
    private String currentLocation;
    private String preferredLocation;
    private String profileStatus;
    private String placementStatus;
    private java.sql.Timestamp createdAt;
    private java.sql.Timestamp updatedAt;

    public Candidate() {}

    public Candidate(int candidateId, String candidateCode, String fullName, String email, String mobile, java.sql.Date dateOfBirth, String gender, String qualification, String college, String university, int passingYear, double percentage, double cgpa, int backlogCount, String primaryTechnology, String currentLocation, String preferredLocation, String profileStatus, String placementStatus, java.sql.Timestamp createdAt, java.sql.Timestamp updatedAt) {
        this.candidateId = candidateId;
        this.candidateCode = candidateCode;
        this.fullName = fullName;
        this.email = email;
        this.mobile = mobile;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.qualification = qualification;
        this.college = college;
        this.university = university;
        this.passingYear = passingYear;
        this.percentage = percentage;
        this.cgpa = cgpa;
        this.backlogCount = backlogCount;
        this.primaryTechnology = primaryTechnology;
        this.currentLocation = currentLocation;
        this.preferredLocation = preferredLocation;
        this.profileStatus = profileStatus;
        this.placementStatus = placementStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    public int getCandidateId() { return candidateId; }
    public void setCandidateId(int candidateId) { this.candidateId = candidateId; }
    public String getCandidateCode() { return candidateCode; }
    public void setCandidateCode(String candidateCode) { this.candidateCode = candidateCode; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }
    public java.sql.Date getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(java.sql.Date dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getQualification() { return qualification; }
    public void setQualification(String qualification) { this.qualification = qualification; }
    public String getCollege() { return college; }
    public void setCollege(String college) { this.college = college; }
    public String getUniversity() { return university; }
    public void setUniversity(String university) { this.university = university; }
    public int getPassingYear() { return passingYear; }
    public void setPassingYear(int passingYear) { this.passingYear = passingYear; }
    public double getPercentage() { return percentage; }
    public void setPercentage(double percentage) { this.percentage = percentage; }
    public double getCgpa() { return cgpa; }
    public void setCgpa(double cgpa) { this.cgpa = cgpa; }
    public int getBacklogCount() { return backlogCount; }
    public void setBacklogCount(int backlogCount) { this.backlogCount = backlogCount; }
    public String getPrimaryTechnology() { return primaryTechnology; }
    public void setPrimaryTechnology(String primaryTechnology) { this.primaryTechnology = primaryTechnology; }
    public String getCurrentLocation() { return currentLocation; }
    public void setCurrentLocation(String currentLocation) { this.currentLocation = currentLocation; }
    public String getPreferredLocation() { return preferredLocation; }
    public void setPreferredLocation(String preferredLocation) { this.preferredLocation = preferredLocation; }
    public String getProfileStatus() { return profileStatus; }
    public void setProfileStatus(String profileStatus) { this.profileStatus = profileStatus; }
    public String getPlacementStatus() { return placementStatus; }
    public void setPlacementStatus(String placementStatus) { this.placementStatus = placementStatus; }
    public java.sql.Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(java.sql.Timestamp createdAt) { this.createdAt = createdAt; }
    public java.sql.Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(java.sql.Timestamp updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "Candidate{" + "candidateId=" + candidateId + ", " + "candidateCode=" + candidateCode + ", " + "fullName=" + fullName + ", " + "email=" + email + ", " + "mobile=" + mobile + ", " + "dateOfBirth=" + dateOfBirth + ", " + "gender=" + gender + ", " + "qualification=" + qualification + ", " + "college=" + college + ", " + "university=" + university + ", " + "passingYear=" + passingYear + ", " + "percentage=" + percentage + ", " + "cgpa=" + cgpa + ", " + "backlogCount=" + backlogCount + ", " + "primaryTechnology=" + primaryTechnology + ", " + "currentLocation=" + currentLocation + ", " + "preferredLocation=" + preferredLocation + ", " + "profileStatus=" + profileStatus + ", " + "placementStatus=" + placementStatus + ", " + "createdAt=" + createdAt + ", " + "updatedAt=" + updatedAt + "}";
    }
}
