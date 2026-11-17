package com.recruitment.model;

import java.util.Objects;

/**
 * Company - model class (data only).
 * Maps to the matching table in database_schema.sql.
 */
public class Company {
    private int companyId;
    private String companyCode;
    private String companyName;
    private String industry;
    private String website;
    private String location;
    private String companyType;
    private String hrName;
    private String hrEmail;
    private String hrMobile;
    private String status;
    private java.sql.Timestamp createdAt;

    public Company() {}

    public Company(int companyId, String companyCode, String companyName, String industry, String website, String location, String companyType, String hrName, String hrEmail, String hrMobile, String status, java.sql.Timestamp createdAt) {
        this.companyId = companyId;
        this.companyCode = companyCode;
        this.companyName = companyName;
        this.industry = industry;
        this.website = website;
        this.location = location;
        this.companyType = companyType;
        this.hrName = hrName;
        this.hrEmail = hrEmail;
        this.hrMobile = hrMobile;
        this.status = status;
        this.createdAt = createdAt;
    }
    public int getCompanyId() { return companyId; }
    public void setCompanyId(int companyId) { this.companyId = companyId; }
    public String getCompanyCode() { return companyCode; }
    public void setCompanyCode(String companyCode) { this.companyCode = companyCode; }
    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }
    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getCompanyType() { return companyType; }
    public void setCompanyType(String companyType) { this.companyType = companyType; }
    public String getHrName() { return hrName; }
    public void setHrName(String hrName) { this.hrName = hrName; }
    public String getHrEmail() { return hrEmail; }
    public void setHrEmail(String hrEmail) { this.hrEmail = hrEmail; }
    public String getHrMobile() { return hrMobile; }
    public void setHrMobile(String hrMobile) { this.hrMobile = hrMobile; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public java.sql.Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(java.sql.Timestamp createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Company{" + "companyId=" + companyId + ", " + "companyCode=" + companyCode + ", " + "companyName=" + companyName + ", " + "industry=" + industry + ", " + "website=" + website + ", " + "location=" + location + ", " + "companyType=" + companyType + ", " + "hrName=" + hrName + ", " + "hrEmail=" + hrEmail + ", " + "hrMobile=" + hrMobile + ", " + "status=" + status + ", " + "createdAt=" + createdAt + "}";
    }
}
