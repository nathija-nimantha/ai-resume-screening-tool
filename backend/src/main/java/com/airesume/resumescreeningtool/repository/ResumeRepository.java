package com.airesume.resumescreeningtool.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.airesume.resumescreeningtool.entity.JobPosting;
import com.airesume.resumescreeningtool.entity.Resume;
import com.airesume.resumescreeningtool.entity.ResumeStatus;

@Repository
public interface ResumeRepository extends JpaRepository<Resume, Long> {
    
    // Find resumes by status
    List<Resume> findByStatus(ResumeStatus status);
    
    // Find resumes by job posting
    List<Resume> findByJobPosting(JobPosting jobPosting);
    
    // Find resumes by job posting and status
    List<Resume> findByJobPostingAndStatus(JobPosting jobPosting, ResumeStatus status);
    
    // Find resumes by candidate email
    List<Resume> findByCandidateEmail(String candidateEmail);
    
    // Find resume by candidate email and job posting (to prevent duplicates)
    Optional<Resume> findByCandidateEmailAndJobPosting(String candidateEmail, JobPosting jobPosting);
    
    // Find resumes by candidate name
    List<Resume> findByCandidateNameContainingIgnoreCase(String candidateName);
    
    // Find resumes by years of experience
    List<Resume> findByYearsOfExperienceGreaterThanEqual(Integer minYears);
    
    // Find resumes by years of experience range
    List<Resume> findByYearsOfExperienceBetween(Integer minYears, Integer maxYears);
    
    // Find resumes submitted within date range
    List<Resume> findBySubmissionDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // Find recent resumes (last N days)
    @Query("SELECT r FROM Resume r WHERE r.submissionDate >= :sinceDate ORDER BY r.submissionDate DESC")
    List<Resume> findRecentResumes(@Param("sinceDate") LocalDateTime sinceDate);
    
    // Search resumes by skills containing keyword
    @Query("SELECT r FROM Resume r WHERE LOWER(r.skills) LIKE LOWER(CONCAT('%', :skill, '%'))")
    List<Resume> findBySkillsContaining(@Param("skill") String skill);
    
    // Search resumes by extracted text containing keyword
    @Query("SELECT r FROM Resume r WHERE LOWER(r.extractedText) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Resume> findByExtractedTextContaining(@Param("keyword") String keyword);
    
    // Find resumes by education containing keyword
    @Query("SELECT r FROM Resume r WHERE LOWER(r.education) LIKE LOWER(CONCAT('%', :education, '%'))")
    List<Resume> findByEducationContaining(@Param("education") String education);
    
    // Find resumes by work experience containing keyword
    @Query("SELECT r FROM Resume r WHERE LOWER(r.workExperience) LIKE LOWER(CONCAT('%', :experience, '%'))")
    List<Resume> findByWorkExperienceContaining(@Param("experience") String experience);
    
    // Find resumes by certifications containing keyword
    @Query("SELECT r FROM Resume r WHERE LOWER(r.certifications) LIKE LOWER(CONCAT('%', :certification, '%'))")
    List<Resume> findByCertificationsContaining(@Param("certification") String certification);
    
    // Find resumes for a specific job posting with multiple criteria
    @Query("SELECT r FROM Resume r WHERE r.jobPosting = :jobPosting AND " +
           "(:status IS NULL OR r.status = :status) AND " +
           "(:minYears IS NULL OR r.yearsOfExperience >= :minYears) AND " +
           "(:maxYears IS NULL OR r.yearsOfExperience <= :maxYears)")
    List<Resume> findByJobPostingWithCriteria(
            @Param("jobPosting") JobPosting jobPosting,
            @Param("status") ResumeStatus status,
            @Param("minYears") Integer minYears,
            @Param("maxYears") Integer maxYears);
    
    // Find resumes with screening results
    @Query("SELECT r FROM Resume r WHERE EXISTS (SELECT sr FROM ScreeningResult sr WHERE sr.resume = r)")
    List<Resume> findResumesWithScreeningResults();
    
    // Find resumes without screening results
    @Query("SELECT r FROM Resume r WHERE NOT EXISTS (SELECT sr FROM ScreeningResult sr WHERE sr.resume = r)")
    List<Resume> findResumesWithoutScreeningResults();
    
    // Find resumes by job posting without screening results
    @Query("SELECT r FROM Resume r WHERE r.jobPosting = :jobPosting AND NOT EXISTS (SELECT sr FROM ScreeningResult sr WHERE sr.resume = r)")
    List<Resume> findByJobPostingWithoutScreeningResults(@Param("jobPosting") JobPosting jobPosting);
    
    // Count resumes by status
    long countByStatus(ResumeStatus status);
    
    // Count resumes by job posting
    long countByJobPosting(JobPosting jobPosting);
    
    // Count resumes by job posting and status
    long countByJobPostingAndStatus(JobPosting jobPosting, ResumeStatus status);
    
    // Check if resume exists for candidate email and job posting
    boolean existsByCandidateEmailAndJobPosting(String candidateEmail, JobPosting jobPosting);
    
    // Find top candidates by years of experience for a job posting
    @Query("SELECT r FROM Resume r WHERE r.jobPosting = :jobPosting ORDER BY r.yearsOfExperience DESC")
    List<Resume> findTopCandidatesByExperience(@Param("jobPosting") JobPosting jobPosting);
}
