# Spring Data JPA Repositories Documentation

## Overview
This document describes the Spring Data JPA repositories for the AI Resume Screening Tool application.

## Repository Structure

### 1. UserRepository
**Interface:** `UserRepository extends JpaRepository<User, Long>`
**Entity:** User

**Key Methods:**
- **Authentication & User Management:**
  - `findByUsername(String username)` - Find user by username
  - `findByEmail(String email)` - Find user by email
  - `findByUsernameOrEmail(String username, String email)` - Login lookup
  - `existsByUsername(String username)` - Check username availability
  - `existsByEmail(String email)` - Check email availability

- **User Status & Role Management:**
  - `findByIsActiveTrue()` - Get all active users
  - `findByRole(UserRole role)` - Find users by role
  - `findByRoleAndIsActiveTrue(UserRole role)` - Find active users by role

- **Company & Organization:**
  - `findByCompanyName(String companyName)` - Users by company
  - `findByCompanyNameAndIsActiveTrue(String companyName)` - Active users by company

- **Custom Queries:**
  - `findActiveUsersWithJobPostingsGreaterThan(long count)` - Users with multiple job postings
  - `findByRoleAndCompanyNameContaining(UserRole role, String keyword)` - Advanced search

### 2. JobPostingRepository
**Interface:** `JobPostingRepository extends JpaRepository<JobPosting, Long>`
**Entity:** JobPosting

**Key Methods:**
- **Status Management:**
  - `findByStatus(JobStatus status)` - Jobs by status
  - `findByStatusOrderByCreatedAtDesc(JobStatus status)` - Jobs by status, newest first

- **User & Company Filtering:**
  - `findByCreatedBy(User createdBy)` - Jobs created by specific user
  - `findByCompanyName(String companyName)` - Jobs by company
  - `findByCreatedByAndStatus(User createdBy, JobStatus status)` - User's jobs by status

- **Location & Department:**
  - `findByLocation(String location)` - Jobs by location
  - `findByDepartment(String department)` - Jobs by department
  - `findByJobType(String jobType)` - Jobs by type (Full-time, Part-time, etc.)

- **Date & Deadline Management:**
  - `findByCreatedAtBetween(LocalDateTime start, LocalDateTime end)` - Jobs in date range
  - `findByApplicationDeadlineAfter(LocalDateTime date)` - Active applications
  - `findByApplicationDeadlineBefore(LocalDateTime date)` - Expired jobs

- **Search & Analytics:**
  - `findByTitleContainingIgnoreCase(String keyword)` - Title search
  - `findByTitleOrDescriptionContainingAndStatus(String keyword, JobStatus status)` - Full-text search
  - `findByStatusWithMinimumResumes(JobStatus status, long minCount)` - Popular jobs
  - `findRecentJobPostings(LocalDateTime sinceDate, JobStatus status)` - Recent postings

- **Multi-criteria Search:**
  - `findByMultipleCriteria(...)` - Advanced filtering with multiple parameters

- **Analytics:**
  - `countByStatus(JobStatus status)` - Job count by status
  - `countByCreatedBy(User createdBy)` - User's job count

### 3. ResumeRepository
**Interface:** `ResumeRepository extends JpaRepository<Resume, Long>`
**Entity:** Resume

**Key Methods:**
- **Basic Filtering:**
  - `findByStatus(ResumeStatus status)` - Resumes by status
  - `findByJobPosting(JobPosting jobPosting)` - Resumes for specific job
  - `findByJobPostingAndStatus(JobPosting jobPosting, ResumeStatus status)` - Filtered resumes

- **Candidate Management:**
  - `findByCandidateEmail(String email)` - All resumes by candidate
  - `findByCandidateEmailAndJobPosting(String email, JobPosting job)` - Prevent duplicates
  - `findByCandidateNameContainingIgnoreCase(String name)` - Name search

- **Experience Filtering:**
  - `findByYearsOfExperienceGreaterThanEqual(Integer minYears)` - Minimum experience
  - `findByYearsOfExperienceBetween(Integer min, Integer max)` - Experience range

- **Content Search:**
  - `findBySkillsContaining(String skill)` - Skills search
  - `findByExtractedTextContaining(String keyword)` - Full-text search
  - `findByEducationContaining(String education)` - Education search
  - `findByWorkExperienceContaining(String experience)` - Experience search
  - `findByCertificationsContaining(String certification)` - Certification search

- **Date & Time:**
  - `findBySubmissionDateBetween(LocalDateTime start, LocalDateTime end)` - Date range
  - `findRecentResumes(LocalDateTime sinceDate)` - Recent submissions

- **Screening Integration:**
  - `findResumesWithScreeningResults()` - Screened resumes
  - `findResumesWithoutScreeningResults()` - Unscreened resumes
  - `findByJobPostingWithoutScreeningResults(JobPosting job)` - Pending screening

- **Analytics:**
  - `countByStatus(ResumeStatus status)` - Status distribution
  - `countByJobPosting(JobPosting job)` - Application count
  - `findTopCandidatesByExperience(JobPosting job)` - Experience ranking

### 4. ScreeningResultRepository
**Interface:** `ScreeningResultRepository extends JpaRepository<ScreeningResult, Long>`
**Entity:** ScreeningResult

**Key Methods:**
- **Basic Filtering:**
  - `findByJobPosting(JobPosting jobPosting)` - Results for job
  - `findByResume(Resume resume)` - Results for resume
  - `findByJobPostingAndResume(JobPosting job, Resume resume)` - Specific result

- **Recommendation Management:**
  - `findByRecommendationStatus(RecommendationStatus status)` - By recommendation
  - `findByJobPostingAndRecommendationStatus(JobPosting job, RecommendationStatus status)` - Filtered recommendations

- **Score-based Filtering:**
  - `findByOverallScoreGreaterThanEqual(BigDecimal minScore)` - Minimum overall score
  - `findByOverallScoreBetween(BigDecimal min, BigDecimal max)` - Score range
  - `findBySkillsScoreGreaterThanEqual(BigDecimal minScore)` - Skills threshold
  - `findByExperienceScoreGreaterThanEqual(BigDecimal minScore)` - Experience threshold
  - `findByEducationScoreGreaterThanEqual(BigDecimal minScore)` - Education threshold

- **Ranking & Analysis:**
  - `findByJobPostingOrderByOverallScoreDesc(JobPosting job)` - Ranked results
  - `findTopCandidatesByJobPosting(JobPosting job)` - Top performers
  - `findHighPerformanceCandidates(JobPosting job, BigDecimal threshold)` - High performers

- **Content Analysis:**
  - `findByAiFeedbackContaining(String keyword)` - Feedback search
  - `findByStrengthsContaining(String keyword)` - Strengths search
  - `findByWeaknessesContaining(String keyword)` - Weaknesses search
  - `findByMatchingKeywordsContaining(String keyword)` - Matching skills
  - `findByMissingKeywordsContaining(String keyword)` - Missing skills

- **Multi-criteria Search:**
  - `findByMultipleScoreCriteria(...)` - Advanced score filtering

- **Analytics & Statistics:**
  - `getAverageOverallScoreByJobPosting(JobPosting job)` - Average scores
  - `getAverageSkillsScoreByJobPosting(JobPosting job)` - Skills averages
  - `getRecommendationStatusDistribution(JobPosting job)` - Status distribution
  - `countByRecommendationStatus(RecommendationStatus status)` - Count by status

- **Performance Monitoring:**
  - `findFastestProcessingResults()` - Performance analysis
  - `findSlowestProcessingResults()` - Performance analysis
  - `findByScreeningVersion(String version)` - Version tracking

## Usage Patterns

### 1. Authentication Flow
```java
// Login validation
Optional<User> user = userRepository.findByUsernameOrEmail(loginId, loginId);

// Registration validation
if (userRepository.existsByUsername(username) || userRepository.existsByEmail(email)) {
    // Handle duplicate user
}
```

### 2. Job Management
```java
// Get active jobs for a company
List<JobPosting> activeJobs = jobPostingRepository.findByCompanyNameAndStatus(
    companyName, JobStatus.ACTIVE);

// Search jobs by keyword
List<JobPosting> searchResults = jobPostingRepository.findByTitleOrDescriptionContainingAndStatus(
    keyword, JobStatus.ACTIVE);
```

### 3. Resume Processing
```java
// Check for duplicate applications
Optional<Resume> existing = resumeRepository.findByCandidateEmailAndJobPosting(
    email, jobPosting);

// Find unscreened resumes
List<Resume> toScreen = resumeRepository.findByJobPostingWithoutScreeningResults(jobPosting);
```

### 4. Screening Analysis
```java
// Get top candidates
List<ScreeningResult> topCandidates = screeningResultRepository
    .findByJobPostingOrderByOverallScoreDesc(jobPosting);

// Filter by recommendation
List<ScreeningResult> recommended = screeningResultRepository
    .findByJobPostingAndRecommendationStatus(jobPosting, RecommendationStatus.RECOMMENDED);
```

## Best Practices

1. **Use appropriate fetch strategies** - All relationships use LAZY loading by default
2. **Leverage query methods** - Spring Data JPA generates implementations automatically
3. **Custom queries for complex logic** - Use @Query for advanced filtering
4. **Pagination support** - All repositories extend JpaRepository with built-in pagination
5. **Transaction management** - Repository operations are transactional by default
6. **Performance optimization** - Use specific queries instead of loading full entities when possible

## Configuration

Repositories are automatically detected by Spring Boot's component scanning. Ensure:
- `@EnableJpaRepositories` is configured (usually automatic)
- Entity classes are properly annotated
- Database configuration is correct in `application.properties`
