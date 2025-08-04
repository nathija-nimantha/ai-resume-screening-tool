package com.airesume.resumescreeningtool.entity;

public enum ResumeStatus {
    SUBMITTED("Submitted"),
    UNDER_REVIEW("Under Review"),
    SCREENED("Screened"),
    SHORTLISTED("Shortlisted"),
    REJECTED("Rejected"),
    INTERVIEW_SCHEDULED("Interview Scheduled"),
    HIRED("Hired"),
    WITHDRAWN("Withdrawn");

    private final String displayName;

    ResumeStatus(String displayName) {
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
