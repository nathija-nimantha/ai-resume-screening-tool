# AI Resume Screening Tool - Entity Documentation

## Overview
This document describes the entity model for the AI Resume Screening Tool application.

## Entities

### 1. User
**Table:** `users`
**Description:** Represents users of the system (HR managers, recruiters, hiring managers, administrators)

**Key Fields:**
- `id` (Primary Key)
- `username` (Unique)
- `email` (Unique)
- `password`
- `firstName`, `lastName`
- `role` (UserRole enum)
- `companyName`
- `isActive`
- `createdAt`, `updatedAt`

**Relationships:**
- One-to-Many with JobPosting (createdBy)

### 2. JobPosting
**Table:** `job_postings`
**Description:** Represents job openings posted by users

**Key Fields:**
- `id` (Primary Key)
- `title`
- `description` (TEXT)
- `requirements` (TEXT)
- `experienceLevel`
- `jobType` (Full-time, Part-time, Contract, etc.)
- `salaryRange`
- `location`
- `companyName`
- `department`
- `status` (JobStatus enum)
- `applicationDeadline`
- `createdAt`, `updatedAt`

**Relationships:**
- Many-to-One with User (createdBy)
- One-to-Many with Resume
- One-to-Many with ScreeningResult

### 3. Resume
**Table:** `resumes`
**Description:** Represents candidate resumes submitted for job postings

**Key Fields:**
- `id` (Primary Key)
- `candidateName`
- `candidateEmail`
- `candidatePhone`
- `fileName`, `filePath`, `fileSize`, `contentType`
- `extractedText` (TEXT)
- `yearsOfExperience`
- `skills` (TEXT)
- `education` (TEXT)
- `workExperience` (TEXT)
- `certifications` (TEXT)
- `status` (ResumeStatus enum)
- `submissionDate`
- `createdAt`, `updatedAt`

**Relationships:**
- Many-to-One with JobPosting
- One-to-Many with ScreeningResult

### 4. ScreeningResult
**Table:** `screening_results`
**Description:** Represents AI-generated screening results for resume-job posting pairs

**Key Fields:**
- `id` (Primary Key)
- `overallScore` (BigDecimal, 0-100)
- `skillsScore` (BigDecimal, 0-100)
- `experienceScore` (BigDecimal, 0-100)
- `educationScore` (BigDecimal, 0-100)
- `aiFeedback` (TEXT)
- `strengths` (TEXT)
- `weaknesses` (TEXT)
- `recommendation` (TEXT)
- `recommendationStatus` (RecommendationStatus enum)
- `matchingKeywords` (TEXT)
- `missingKeywords` (TEXT)
- `screeningVersion`
- `processingTime` (milliseconds)
- `createdAt`, `updatedAt`

**Relationships:**
- Many-to-One with JobPosting
- Many-to-One with Resume
- Many-to-One with User (screenedBy)

## Enums

### UserRole
- HR_MANAGER
- RECRUITER
- HIRING_MANAGER
- ADMIN

### JobStatus
- ACTIVE
- CLOSED
- DRAFT
- PAUSED
- EXPIRED

### ResumeStatus
- SUBMITTED
- UNDER_REVIEW
- SCREENED
- SHORTLISTED
- REJECTED
- INTERVIEW_SCHEDULED
- HIRED
- WITHDRAWN

### RecommendationStatus
- STRONGLY_RECOMMENDED
- RECOMMENDED
- CONSIDER
- NOT_RECOMMENDED
- REJECTED

## Entity Relationships Diagram

```
User (1) ──────────── (M) JobPosting
                           │
                           │ (1)
                           │
                           ▼
                       (M) Resume ──── (M) ScreeningResult (M) ──── (1) User
                           │                    │
                           │ (1)              │ (1)
                           └──────────────────┘
                               JobPosting
```

## Key Features

1. **Audit Trail**: All entities include `createdAt` and `updatedAt` timestamps
2. **Soft References**: Relationships use lazy loading for performance
3. **Cascade Operations**: Appropriate cascade settings for data integrity
4. **Validation**: Non-nullable fields for required data
5. **Flexible Text Storage**: TEXT columns for large content (descriptions, extracted text, feedback)
6. **Precision Scoring**: BigDecimal for accurate scoring (5,2 precision)
7. **Comprehensive Status Tracking**: Enums for status management across all entities

## Database Configuration

The application is configured to use PostgreSQL with JPA/Hibernate:
- DDL auto-update enabled
- SQL logging enabled for development
- Proper indexing on foreign keys and frequently queried fields
