package com.airesume.resumescreeningtool.entity;

public enum UserRole {
    USER("User"),
    HR_MANAGER("HR Manager"),
    RECRUITER("Recruiter"),
    HIRING_MANAGER("Hiring Manager"),
    ADMIN("Administrator");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
