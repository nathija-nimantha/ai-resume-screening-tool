package com.airesume.resumescreeningtool.entity;

public enum JobStatus {
    ACTIVE("Active"),
    CLOSED("Closed"),
    DRAFT("Draft"),
    PAUSED("Paused"),
    EXPIRED("Expired");

    private final String displayName;

    JobStatus(String displayName) {
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
