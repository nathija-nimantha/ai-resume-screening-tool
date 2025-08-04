package com.airesume.resumescreeningtool.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "screening_results")
public class ScreeningResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "overall_score", precision = 5, scale = 2)
    private BigDecimal overallScore; // Overall matching score (0-100)

    @Column(name = "skills_score", precision = 5, scale = 2)
    private BigDecimal skillsScore; // Skills matching score (0-100)

    @Column(name = "experience_score", precision = 5, scale = 2)
    private BigDecimal experienceScore; // Experience matching score (0-100)

    @Column(name = "education_score", precision = 5, scale = 2)
    private BigDecimal educationScore; // Education matching score (0-100)

    @Column(name = "ai_feedback", columnDefinition = "TEXT")
    private String aiFeedback; // Detailed AI analysis and feedback

    @Column(name = "strengths", columnDefinition = "TEXT")
    private String strengths; // Candidate's strengths

    @Column(name = "weaknesses", columnDefinition = "TEXT")
    private String weaknesses; // Candidate's weaknesses or gaps

    @Column(name = "recommendation", columnDefinition = "TEXT")
    private String recommendation; // AI recommendation

    @Enumerated(EnumType.STRING)
    @Column(name = "recommendation_status", nullable = false)
    private RecommendationStatus recommendationStatus;

    @Column(name = "matching_keywords", columnDefinition = "TEXT")
    private String matchingKeywords; // Keywords that matched from job requirements

    @Column(name = "missing_keywords", columnDefinition = "TEXT")
    private String missingKeywords; // Keywords missing from resume

    @Column(name = "screening_version")
    private String screeningVersion; // Version of the AI model used

    @Column(name = "processing_time")
    private Long processingTime; // Time taken to process in milliseconds

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Many screening results can belong to one job posting
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_posting_id", nullable = false)
    private JobPosting jobPosting;

    // Many screening results can belong to one resume
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;

    // Many screening results can be created by one user (who initiated the screening)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screened_by")
    private User screenedBy;

    // Constructors
    public ScreeningResult() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public ScreeningResult(JobPosting jobPosting, Resume resume, User screenedBy) {
        this();
        this.jobPosting = jobPosting;
        this.resume = resume;
        this.screenedBy = screenedBy;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getOverallScore() {
        return overallScore;
    }

    public void setOverallScore(BigDecimal overallScore) {
        this.overallScore = overallScore;
    }

    public BigDecimal getSkillsScore() {
        return skillsScore;
    }

    public void setSkillsScore(BigDecimal skillsScore) {
        this.skillsScore = skillsScore;
    }

    public BigDecimal getExperienceScore() {
        return experienceScore;
    }

    public void setExperienceScore(BigDecimal experienceScore) {
        this.experienceScore = experienceScore;
    }

    public BigDecimal getEducationScore() {
        return educationScore;
    }

    public void setEducationScore(BigDecimal educationScore) {
        this.educationScore = educationScore;
    }

    public String getAiFeedback() {
        return aiFeedback;
    }

    public void setAiFeedback(String aiFeedback) {
        this.aiFeedback = aiFeedback;
    }

    public String getStrengths() {
        return strengths;
    }

    public void setStrengths(String strengths) {
        this.strengths = strengths;
    }

    public String getWeaknesses() {
        return weaknesses;
    }

    public void setWeaknesses(String weaknesses) {
        this.weaknesses = weaknesses;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public RecommendationStatus getRecommendationStatus() {
        return recommendationStatus;
    }

    public void setRecommendationStatus(RecommendationStatus recommendationStatus) {
        this.recommendationStatus = recommendationStatus;
    }

    public String getMatchingKeywords() {
        return matchingKeywords;
    }

    public void setMatchingKeywords(String matchingKeywords) {
        this.matchingKeywords = matchingKeywords;
    }

    public String getMissingKeywords() {
        return missingKeywords;
    }

    public void setMissingKeywords(String missingKeywords) {
        this.missingKeywords = missingKeywords;
    }

    public String getScreeningVersion() {
        return screeningVersion;
    }

    public void setScreeningVersion(String screeningVersion) {
        this.screeningVersion = screeningVersion;
    }

    public Long getProcessingTime() {
        return processingTime;
    }

    public void setProcessingTime(Long processingTime) {
        this.processingTime = processingTime;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public JobPosting getJobPosting() {
        return jobPosting;
    }

    public void setJobPosting(JobPosting jobPosting) {
        this.jobPosting = jobPosting;
    }

    public Resume getResume() {
        return resume;
    }

    public void setResume(Resume resume) {
        this.resume = resume;
    }

    public User getScreenedBy() {
        return screenedBy;
    }

    public void setScreenedBy(User screenedBy) {
        this.screenedBy = screenedBy;
    }

    // Lifecycle callbacks
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // toString method
    @Override
    public String toString() {
        return "ScreeningResult{" +
                "id=" + id +
                ", overallScore=" + overallScore +
                ", recommendationStatus=" + recommendationStatus +
                ", createdAt=" + createdAt +
                '}';
    }
}
