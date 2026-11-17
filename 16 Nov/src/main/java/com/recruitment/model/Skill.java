package com.recruitment.model;

import java.util.Objects;

/**
 * Skill - model class (data only).
 * Maps to the matching table in database_schema.sql.
 */
public class Skill {
    private int skillId;
    private String skillName;
    private String category;
    private boolean active;

    public Skill() {}

    public Skill(int skillId, String skillName, String category, boolean active) {
        this.skillId = skillId;
        this.skillName = skillName;
        this.category = category;
        this.active = active;
    }
    public int getSkillId() { return skillId; }
    public void setSkillId(int skillId) { this.skillId = skillId; }
    public String getSkillName() { return skillName; }
    public void setSkillName(String skillName) { this.skillName = skillName; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public boolean getActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    @Override
    public String toString() {
        return "Skill{" + "skillId=" + skillId + ", " + "skillName=" + skillName + ", " + "category=" + category + ", " + "active=" + active + "}";
    }
}
