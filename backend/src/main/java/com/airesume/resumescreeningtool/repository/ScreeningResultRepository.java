package com.airesume.resumescreeningtool.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.airesume.resumescreeningtool.entity.JobPosting;
import com.airesume.resumescreeningtool.entity.RecommendationStatus;
import com.airesume.resumescreeningtool.entity.Resume;
import com.airesume.resumescreeningtool.entity.ScreeningResult;
import com.airesume.resumescreeningtool.entity.User;

@Repository
public interface ScreeningResultRepository extends JpaRepository<ScreeningResult, Long> {
    
    // Find screening results by job posting
    List<ScreeningResult> findByJobPosting(JobPosting jobPosting);
    
    // Find screening results by resume
    List<ScreeningResult> findByResume(Resume resume);
    
    // Find screening result by job posting and resume (should be unique)
    Optional<ScreeningResult> findByJobPostingAndResume(JobPosting jobPosting, Resume resume);
    
    // Find screening results by recommendation status
    List<ScreeningResult> findByRecommendationStatus(RecommendationStatus recommendationStatus);
    
    // Find screening results by job posting and recommendation status
    List<ScreeningResult> findByJobPostingAndRecommendationStatus(JobPosting jobPosting, RecommendationStatus recommendationStatus);
    
    // Find screening results by screened by user
    List<ScreeningResult> findByScreenedBy(User screenedBy);
    
    // Find screening results with overall score greater than or equal to threshold
    List<ScreeningResult> findByOverallScoreGreaterThanEqual(BigDecimal minScore);
    
    // Find screening results with overall score between range
    List<ScreeningResult> findByOverallScoreBetween(BigDecimal minScore, BigDecimal maxScore);
    
    // Find screening results by job posting ordered by overall score descending
    List<ScreeningResult> findByJobPostingOrderByOverallScoreDesc(JobPosting jobPosting);
    
    // Find top N screening results for a job posting by overall score
    @Query("SELECT sr FROM ScreeningResult sr WHERE sr.jobPosting = :jobPosting ORDER BY sr.overallScore DESC")
    List<ScreeningResult> findTopCandidatesByJobPosting(@Param("jobPosting") JobPosting jobPosting);
    
    // Find screening results by skills score threshold
    List<ScreeningResult> findBySkillsScoreGreaterThanEqual(BigDecimal minSkillsScore);
    
    // Find screening results by experience score threshold
    List<ScreeningResult> findByExperienceScoreGreaterThanEqual(BigDecimal minExperienceScore);
    
    // Find screening results by education score threshold
    List<ScreeningResult> findByEducationScoreGreaterThanEqual(BigDecimal minEducationScore);
    
    // Find screening results created within date range
    List<ScreeningResult> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Find recent screening results (last N days)
    @Query("SELECT sr FROM ScreeningResult sr WHERE sr.createdAt >= :sinceDate ORDER BY sr.createdAt DESC")
    List<ScreeningResult> findRecentScreeningResults(@Param("sinceDate") LocalDateTime sinceDate);
    
    // Find screening results by screening version
    List<ScreeningResult> findByScreeningVersion(String screeningVersion);
    
    // Search screening results by AI feedback containing keyword
    @Query("SELECT sr FROM ScreeningResult sr WHERE LOWER(sr.aiFeedback) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<ScreeningResult> findByAiFeedbackContaining(@Param("keyword") String keyword);
    
    // Search screening results by strengths containing keyword
    @Query("SELECT sr FROM ScreeningResult sr WHERE LOWER(sr.strengths) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<ScreeningResult> findByStrengthsContaining(@Param("keyword") String keyword);
    
    // Search screening results by weaknesses containing keyword
    @Query("SELECT sr FROM ScreeningResult sr WHERE LOWER(sr.weaknesses) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<ScreeningResult> findByWeaknessesContaining(@Param("keyword") String keyword);
    
    // Find screening results by matching keywords containing specific keyword
    @Query("SELECT sr FROM ScreeningResult sr WHERE LOWER(sr.matchingKeywords) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<ScreeningResult> findByMatchingKeywordsContaining(@Param("keyword") String keyword);
    
    // Find screening results by missing keywords containing specific keyword
    @Query("SELECT sr FROM ScreeningResult sr WHERE LOWER(sr.missingKeywords) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<ScreeningResult> findByMissingKeywordsContaining(@Param("keyword") String keyword);
    
    // Find screening results by multiple score criteria
    @Query("SELECT sr FROM ScreeningResult sr WHERE " +
           "(:minOverallScore IS NULL OR sr.overallScore >= :minOverallScore) AND " +
           "(:minSkillsScore IS NULL OR sr.skillsScore >= :minSkillsScore) AND " +
           "(:minExperienceScore IS NULL OR sr.experienceScore >= :minExperienceScore) AND " +
           "(:minEducationScore IS NULL OR sr.educationScore >= :minEducationScore) AND " +
           "(:recommendationStatus IS NULL OR sr.recommendationStatus = :recommendationStatus)")
    List<ScreeningResult> findByMultipleScoreCriteria(
            @Param("minOverallScore") BigDecimal minOverallScore,
            @Param("minSkillsScore") BigDecimal minSkillsScore,
            @Param("minExperienceScore") BigDecimal minExperienceScore,
            @Param("minEducationScore") BigDecimal minEducationScore,
            @Param("recommendationStatus") RecommendationStatus recommendationStatus);
    
    // Get average scores for a job posting
    @Query("SELECT AVG(sr.overallScore) FROM ScreeningResult sr WHERE sr.jobPosting = :jobPosting")
    BigDecimal getAverageOverallScoreByJobPosting(@Param("jobPosting") JobPosting jobPosting);
    
    // Get average skills score for a job posting
    @Query("SELECT AVG(sr.skillsScore) FROM ScreeningResult sr WHERE sr.jobPosting = :jobPosting")
    BigDecimal getAverageSkillsScoreByJobPosting(@Param("jobPosting") JobPosting jobPosting);
    
    // Get screening results statistics by recommendation status
    @Query("SELECT sr.recommendationStatus, COUNT(sr) FROM ScreeningResult sr WHERE sr.jobPosting = :jobPosting GROUP BY sr.recommendationStatus")
    List<Object[]> getRecommendationStatusDistribution(@Param("jobPosting") JobPosting jobPosting);
    
    // Find high-performance candidates (top percentage)
    @Query("SELECT sr FROM ScreeningResult sr WHERE sr.jobPosting = :jobPosting AND sr.overallScore >= :threshold ORDER BY sr.overallScore DESC")
    List<ScreeningResult> findHighPerformanceCandidates(@Param("jobPosting") JobPosting jobPosting, @Param("threshold") BigDecimal threshold);
    
    // Count screening results by recommendation status
    long countByRecommendationStatus(RecommendationStatus recommendationStatus);
    
    // Count screening results by job posting
    long countByJobPosting(JobPosting jobPosting);
    
    // Count screening results by job posting and recommendation status
    long countByJobPostingAndRecommendationStatus(JobPosting jobPosting, RecommendationStatus recommendationStatus);
    
    // Check if screening result exists for job posting and resume
    boolean existsByJobPostingAndResume(JobPosting jobPosting, Resume resume);
    
    // Find fastest processing results (performance monitoring)
    @Query("SELECT sr FROM ScreeningResult sr ORDER BY sr.processingTime ASC")
    List<ScreeningResult> findFastestProcessingResults();
    
    // Find slowest processing results (performance monitoring)
    @Query("SELECT sr FROM ScreeningResult sr ORDER BY sr.processingTime DESC")
    List<ScreeningResult> findSlowestProcessingResults();
}
