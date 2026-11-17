package com.recruitment.model;

import java.util.Objects;

/**
 * Role - model class (data only).
 * Maps to the matching table in database_schema.sql.
 */
public class Role {
    private int roleId;
    private String roleCode;
    private String roleName;
    private boolean active;
    private java.sql.Timestamp createdAt;

    public Role() {}

    public Role(int roleId, String roleCode, String roleName, boolean active, java.sql.Timestamp createdAt) {
        this.roleId = roleId;
        this.roleCode = roleCode;
        this.roleName = roleName;
        this.active = active;
        this.createdAt = createdAt;
    }
    public int getRoleId() { return roleId; }
    public void setRoleId(int roleId) { this.roleId = roleId; }
    public String getRoleCode() { return roleCode; }
    public void setRoleCode(String roleCode) { this.roleCode = roleCode; }
    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }
    public boolean getActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public java.sql.Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(java.sql.Timestamp createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return "Role{" + "roleId=" + roleId + ", " + "roleCode=" + roleCode + ", " + "roleName=" + roleName + ", " + "active=" + active + ", " + "createdAt=" + createdAt + "}";
    }
}
