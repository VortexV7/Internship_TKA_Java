package com.recruitment.model;

import java.util.Objects;

/**
 * Job - model class (data only).
 * Maps to the matching table in database_schema.sql.
 */
public class Job {
    private int jobId;
    private String jobCode;
    private int companyId;
    private String jobTitle;
    private String employmentType;
    private String location;
    private int openings;
    private double minPackageLpa;
    private double maxPackageLpa;
    private double minPercentage;
    private int minPassingYear;
    private int maxPassingYear;
    private int maxBacklogs;
    private double experienceMinYears;
    private String qualificationRule;
    private java.sql.Date postedDate;
    private java.sql.Date applicationDeadline;
    private String status;
    private int createdBy;
    private java.sql.Timestamp createdAt;

    public Job() {}

    public Job(int jobId, String jobCode, int companyId, String jobTitle, String employmentType, String location, int openings, double minPackageLpa, double maxPackageLpa, double minPercentage, int minPassingYear, int maxPassingYear, int maxBacklogs, double experienceMinYears, String qualificationRule, java.sql.Date postedDate, java.sql.Date applicationDeadline, String status, int createdBy, java.sql.Timestamp createdAt) {
        this.jobId = jobId;
        this.jobCode = jobCode;
        this.companyId = companyId;
        this.jobTitle = jobTitle;
        this.employmentType = employmentType;
        this.location = location;
        this.openings = openings;
        this.minPackageLpa = minPackageLpa;
        this.maxPackageLpa = maxPackageLpa;
        this.minPercentage = minPercentage;
        this.minPassingYear = minPassingYear;
        this.maxPassingYear = maxPassingYear;
        this.maxBacklogs = maxBacklogs;
        this.experienceMinYears = experienceMinYears;
        this.qualificationRule = qualificationRule;
        this.postedDate = postedDate;
        this.applicationDeadline = applicationDeadline;
        this.status = status;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
    }
    public int getJobId() { return jobId; }
    public void setJobId(int jobId) { this.jobId = jobId; }
    public String getJobCode() { return jobCode; }
    public void setJobCode(String jobCode) { this.jobCode = jobCode; }
    public int getCompanyId() { return companyId; }
    public void setCompanyId(int companyId) { this.companyId = companyId; }
    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }
    public String getEmploymentType() { return employmentType; }
    public void setEmploymentType(String employmentType) { this.employmentType = employmentType; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public int getOpenings() { return openings; }
    public void setOpenings(int openings) { this.openings = openings; }
    public double getMinPackageLpa() { return minPackageLpa; }
    public void setMinPackageLpa(double minPackageLpa) { this.minPackageLpa = minPackageLpa; }
    public double getMaxPackageLpa() { return maxPackageLpa; }
    public void setMaxPackageLpa(double maxPackageLpa) { this.maxPackageLpa = maxPackageLpa; }
    public double getMinPercentage() { return minPercentage; }
    public void setMinPercentage(double minPercentage) { this.minPercentage = minPercentage; }
    public int getMinPassingYear() { return minPassingYear; }
    public void setMinPassingYear(int minPassingYear) { this.minPassingYear = minPassingYear; }
    public int getMaxPassingYear() { return maxPassingYear; }
    public void setMaxPassingYear(int maxPassingYear) { this.maxPassingYear = maxPassingYear; }
    public int getMaxBacklogs() { return maxBacklogs; }
    public void setMaxBacklogs(int maxBacklogs) { this.maxBacklogs = maxBacklogs; }
    public double getExperienceMinYears() { return experienceMinYears; }
    public void setExperienceMinYears(double experienceMinYears) { this.experienceMinYears = experienceMinYears; }
    public String getQualificationRule() { return qualificationRule; }
    public void setQualificationRule(String qualificationRule) { this.qualificationRule = qualificationRule; }
    public java.sql.Date getPostedDate() { return postedDate; }
    public void setPostedDate(java.sql.Date postedDate) { this.postedDate = postedDate; }
    public java.sql.Date getApplicationDeadline() { return applicationDeadline; }
    public void setApplicationDeadline(java.sql.Date applicationDeadline) { this.applicationDeadline = applicationDeadline; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getCreatedBy() { return createdBy; }
    public void setCreatedBy(int createdBy) { this.createdBy = createdBy; }
    public java.sql.Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(java.sql.Timestamp createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Job{" + "jobId=" + jobId + ", " + "jobCode=" + jobCode + ", " + "companyId=" + companyId + ", " + "jobTitle=" + jobTitle + ", " + "employmentType=" + employmentType + ", " + "location=" + location + ", " + "openings=" + openings + ", " + "minPackageLpa=" + minPackageLpa + ", " + "maxPackageLpa=" + maxPackageLpa + ", " + "minPercentage=" + minPercentage + ", " + "minPassingYear=" + minPassingYear + ", " + "maxPassingYear=" + maxPassingYear + ", " + "maxBacklogs=" + maxBacklogs + ", " + "experienceMinYears=" + experienceMinYears + ", " + "qualificationRule=" + qualificationRule + ", " + "postedDate=" + postedDate + ", " + "applicationDeadline=" + applicationDeadline + ", " + "status=" + status + ", " + "createdBy=" + createdBy + ", " + "createdAt=" + createdAt + "}";
    }
}
