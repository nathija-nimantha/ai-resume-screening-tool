package com.airesume.resumescreeningtool.entity;

public enum RecommendationStatus {
    STRONGLY_RECOMMENDED("Strongly Recommended"),
    RECOMMENDED("Recommended"),
    CONSIDER("Consider"),
    NOT_RECOMMENDED("Not Recommended"),
    REJECTED("Rejected");

    private final String displayName;

    RecommendationStatus(String displayName) {
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
