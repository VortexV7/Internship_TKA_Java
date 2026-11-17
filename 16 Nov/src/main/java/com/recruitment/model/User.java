package com.recruitment.model;

import java.util.Objects;

/**
 * User - model class (data only).
 * Maps to the matching table in database_schema.sql.
 */
public class User {
    private int userId;
    private String username;
    private String passwordHash;
    private String fullName;
    private String email;
    private int roleId;
    private String status;
    private java.sql.Timestamp lastLoginAt;
    private java.sql.Timestamp createdAt;

    public User() {}

    public User(int userId, String username, String passwordHash, String fullName, String email, int roleId, String status, java.sql.Timestamp lastLoginAt, java.sql.Timestamp createdAt) {
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.email = email;
        this.roleId = roleId;
        this.status = status;
        this.lastLoginAt = lastLoginAt;
        this.createdAt = createdAt;
    }
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public int getRoleId() { return roleId; }
    public void setRoleId(int roleId) { this.roleId = roleId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public java.sql.Timestamp getLastLoginAt() { return lastLoginAt; }
    public void setLastLoginAt(java.sql.Timestamp lastLoginAt) { this.lastLoginAt = lastLoginAt; }
    public java.sql.Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(java.sql.Timestamp createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "User{" + "userId=" + userId + ", " + "username=" + username + ", " + "passwordHash=" + passwordHash + ", " + "fullName=" + fullName + ", " + "email=" + email + ", " + "roleId=" + roleId + ", " + "status=" + status + ", " + "lastLoginAt=" + lastLoginAt + ", " + "createdAt=" + createdAt + "}";
    }
}
