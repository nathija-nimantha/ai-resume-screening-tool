package com.airesume.resumescreeningtool.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.airesume.resumescreeningtool.entity.JobPosting;
import com.airesume.resumescreeningtool.entity.JobStatus;
import com.airesume.resumescreeningtool.entity.User;

@Repository
public interface JobPostingRepository extends JpaRepository<JobPosting, Long> {
    
    // Find job postings by status
    List<JobPosting> findByStatus(JobStatus status);
    
    // Find active job postings
    List<JobPosting> findByStatusOrderByCreatedAtDesc(JobStatus status);
    
    // Find job postings created by a specific user
    List<JobPosting> findByCreatedBy(User createdBy);
    
    // Find job postings created by user with specific status
    List<JobPosting> findByCreatedByAndStatus(User createdBy, JobStatus status);
    
    // Find job postings by company name
    List<JobPosting> findByCompanyName(String companyName);
    
    // Find job postings by company name and status
    List<JobPosting> findByCompanyNameAndStatus(String companyName, JobStatus status);
    
    // Find job postings by location
    List<JobPosting> findByLocation(String location);
    
    // Find job postings by location and status
    List<JobPosting> findByLocationAndStatus(String location, JobStatus status);
    
    // Find job postings by department
    List<JobPosting> findByDepartment(String department);
    
    // Find job postings by job type
    List<JobPosting> findByJobType(String jobType);
    
    // Find job postings by experience level
    List<JobPosting> findByExperienceLevel(String experienceLevel);
    
    // Find job postings created within date range
    List<JobPosting> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Find job postings with application deadline after given date
    List<JobPosting> findByApplicationDeadlineAfter(LocalDateTime date);
    
    // Find job postings with application deadline before given date (expired)
    List<JobPosting> findByApplicationDeadlineBefore(LocalDateTime date);
    
    // Search job postings by title containing keyword
    List<JobPosting> findByTitleContainingIgnoreCase(String keyword);
    
    // Search job postings by title or description containing keyword
    @Query("SELECT jp FROM JobPosting jp WHERE (LOWER(jp.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(jp.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND jp.status = :status")
    List<JobPosting> findByTitleOrDescriptionContainingAndStatus(@Param("keyword") String keyword, @Param("status") JobStatus status);
    
    // Find job postings with resume count
    @Query("SELECT jp FROM JobPosting jp LEFT JOIN jp.resumes r WHERE jp.status = :status GROUP BY jp HAVING COUNT(r) >= :minResumeCount ORDER BY COUNT(r) DESC")
    List<JobPosting> findByStatusWithMinimumResumes(@Param("status") JobStatus status, @Param("minResumeCount") long minResumeCount);
    
    // Find recent job postings (last N days)
    @Query("SELECT jp FROM JobPosting jp WHERE jp.createdAt >= :sinceDate AND jp.status = :status ORDER BY jp.createdAt DESC")
    List<JobPosting> findRecentJobPostings(@Param("sinceDate") LocalDateTime sinceDate, @Param("status") JobStatus status);
    
    // Find job postings by multiple criteria
    @Query("SELECT jp FROM JobPosting jp WHERE " +
           "(:companyName IS NULL OR jp.companyName = :companyName) AND " +
           "(:location IS NULL OR jp.location = :location) AND " +
           "(:department IS NULL OR jp.department = :department) AND " +
           "(:jobType IS NULL OR jp.jobType = :jobType) AND " +
           "(:status IS NULL OR jp.status = :status)")
    List<JobPosting> findByMultipleCriteria(
            @Param("companyName") String companyName,
            @Param("location") String location,
            @Param("department") String department,
            @Param("jobType") String jobType,
            @Param("status") JobStatus status);
    
    // Count job postings by status
    long countByStatus(JobStatus status);
    
    // Count job postings by created by user
    long countByCreatedBy(User createdBy);
}
