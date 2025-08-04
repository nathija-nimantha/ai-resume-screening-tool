-- Manual Database Schema Creation Script
-- This script can be run manually if Flyway migrations are not used
-- AI Resume Screening Tool Database Schema

-- Create database (run this separately if needed)
-- CREATE DATABASE rst_db;

-- Connect to the database
-- \c rst_db;

-- Drop existing tables if they exist (in reverse order due to foreign keys)
DROP TABLE IF EXISTS screening_results CASCADE;
DROP TABLE IF EXISTS resumes CASCADE;
DROP TABLE IF EXISTS job_postings CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- Drop materialized view if exists
DROP MATERIALIZED VIEW IF EXISTS job_posting_stats;

-- Drop functions if they exist
DROP FUNCTION IF EXISTS update_updated_at_column() CASCADE;
DROP FUNCTION IF EXISTS refresh_job_posting_stats() CASCADE;

-- Create function to automatically update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    role VARCHAR(50) NOT NULL CHECK (role IN ('HR_MANAGER', 'RECRUITER', 'HIRING_MANAGER', 'ADMIN')),
    company_name VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create job_postings table
CREATE TABLE job_postings (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    requirements TEXT,
    experience_level VARCHAR(100),
    job_type VARCHAR(100),
    salary_range VARCHAR(100),
    location VARCHAR(255),
    company_name VARCHAR(255),
    department VARCHAR(255),
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'CLOSED', 'DRAFT', 'PAUSED', 'EXPIRED')),
    application_deadline TIMESTAMP,
    created_by BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_job_postings_created_by FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE CASCADE
);

-- Create resumes table
CREATE TABLE resumes (
    id BIGSERIAL PRIMARY KEY,
    candidate_name VARCHAR(255) NOT NULL,
    candidate_email VARCHAR(255) NOT NULL,
    candidate_phone VARCHAR(50),
    file_name VARCHAR(255) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    file_size BIGINT,
    content_type VARCHAR(100),
    extracted_text TEXT,
    years_of_experience INTEGER,
    skills TEXT,
    education TEXT,
    work_experience TEXT,
    certifications TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'SUBMITTED' CHECK (status IN ('SUBMITTED', 'UNDER_REVIEW', 'SCREENED', 'SHORTLISTED', 'REJECTED', 'INTERVIEW_SCHEDULED', 'HIRED', 'WITHDRAWN')),
    job_posting_id BIGINT NOT NULL,
    submission_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_resumes_job_posting FOREIGN KEY (job_posting_id) REFERENCES job_postings(id) ON DELETE CASCADE,
    CONSTRAINT uk_resumes_candidate_job UNIQUE (candidate_email, job_posting_id),
    CONSTRAINT chk_years_of_experience_positive CHECK (years_of_experience >= 0),
    CONSTRAINT chk_file_size_positive CHECK (file_size > 0)
);

-- Create screening_results table
CREATE TABLE screening_results (
    id BIGSERIAL PRIMARY KEY,
    overall_score DECIMAL(5,2),
    skills_score DECIMAL(5,2),
    experience_score DECIMAL(5,2),
    education_score DECIMAL(5,2),
    ai_feedback TEXT,
    strengths TEXT,
    weaknesses TEXT,
    recommendation TEXT,
    recommendation_status VARCHAR(50) NOT NULL CHECK (recommendation_status IN ('STRONGLY_RECOMMENDED', 'RECOMMENDED', 'CONSIDER', 'NOT_RECOMMENDED', 'REJECTED')),
    matching_keywords TEXT,
    missing_keywords TEXT,
    screening_version VARCHAR(50),
    processing_time BIGINT,
    job_posting_id BIGINT NOT NULL,
    resume_id BIGINT NOT NULL,
    screened_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_screening_results_job_posting FOREIGN KEY (job_posting_id) REFERENCES job_postings(id) ON DELETE CASCADE,
    CONSTRAINT fk_screening_results_resume FOREIGN KEY (resume_id) REFERENCES resumes(id) ON DELETE CASCADE,
    CONSTRAINT fk_screening_results_screened_by FOREIGN KEY (screened_by) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT uk_screening_results_job_resume UNIQUE (job_posting_id, resume_id),
    CONSTRAINT chk_overall_score_range CHECK (overall_score >= 0 AND overall_score <= 100),
    CONSTRAINT chk_skills_score_range CHECK (skills_score >= 0 AND skills_score <= 100),
    CONSTRAINT chk_experience_score_range CHECK (experience_score >= 0 AND experience_score <= 100),
    CONSTRAINT chk_education_score_range CHECK (education_score >= 0 AND education_score <= 100),
    CONSTRAINT chk_processing_time_positive CHECK (processing_time >= 0)
);

-- Create all indexes
-- Users indexes
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_company_name ON users(company_name);
CREATE INDEX idx_users_is_active ON users(is_active);

-- Job postings indexes
CREATE INDEX idx_job_postings_title ON job_postings(title);
CREATE INDEX idx_job_postings_status ON job_postings(status);
CREATE INDEX idx_job_postings_created_by ON job_postings(created_by);
CREATE INDEX idx_job_postings_company_name ON job_postings(company_name);
CREATE INDEX idx_job_postings_location ON job_postings(location);
CREATE INDEX idx_job_postings_department ON job_postings(department);
CREATE INDEX idx_job_postings_job_type ON job_postings(job_type);
CREATE INDEX idx_job_postings_experience_level ON job_postings(experience_level);
CREATE INDEX idx_job_postings_created_at ON job_postings(created_at);
CREATE INDEX idx_job_postings_application_deadline ON job_postings(application_deadline);

-- Resumes indexes
CREATE INDEX idx_resumes_candidate_name ON resumes(candidate_name);
CREATE INDEX idx_resumes_candidate_email ON resumes(candidate_email);
CREATE INDEX idx_resumes_status ON resumes(status);
CREATE INDEX idx_resumes_job_posting_id ON resumes(job_posting_id);
CREATE INDEX idx_resumes_years_of_experience ON resumes(years_of_experience);
CREATE INDEX idx_resumes_submission_date ON resumes(submission_date);
CREATE INDEX idx_resumes_created_at ON resumes(created_at);

-- Full-text search indexes
CREATE INDEX idx_resumes_skills_gin ON resumes USING gin(to_tsvector('english', skills));
CREATE INDEX idx_resumes_extracted_text_gin ON resumes USING gin(to_tsvector('english', extracted_text));
CREATE INDEX idx_resumes_education_gin ON resumes USING gin(to_tsvector('english', education));
CREATE INDEX idx_resumes_work_experience_gin ON resumes USING gin(to_tsvector('english', work_experience));

-- Screening results indexes
CREATE INDEX idx_screening_results_overall_score ON screening_results(overall_score);
CREATE INDEX idx_screening_results_skills_score ON screening_results(skills_score);
CREATE INDEX idx_screening_results_experience_score ON screening_results(experience_score);
CREATE INDEX idx_screening_results_education_score ON screening_results(education_score);
CREATE INDEX idx_screening_results_recommendation_status ON screening_results(recommendation_status);
CREATE INDEX idx_screening_results_job_posting_id ON screening_results(job_posting_id);
CREATE INDEX idx_screening_results_resume_id ON screening_results(resume_id);
CREATE INDEX idx_screening_results_screened_by ON screening_results(screened_by);
CREATE INDEX idx_screening_results_created_at ON screening_results(created_at);
CREATE INDEX idx_screening_results_screening_version ON screening_results(screening_version);
CREATE INDEX idx_screening_results_processing_time ON screening_results(processing_time);

-- Composite indexes
CREATE INDEX idx_screening_results_job_status ON screening_results(job_posting_id, recommendation_status);
CREATE INDEX idx_screening_results_job_score ON screening_results(job_posting_id, overall_score DESC);

-- Full-text search indexes for screening results
CREATE INDEX idx_screening_results_ai_feedback_gin ON screening_results USING gin(to_tsvector('english', ai_feedback));
CREATE INDEX idx_screening_results_strengths_gin ON screening_results USING gin(to_tsvector('english', strengths));
CREATE INDEX idx_screening_results_weaknesses_gin ON screening_results USING gin(to_tsvector('english', weaknesses));

-- Partial indexes for performance
CREATE INDEX idx_active_job_postings ON job_postings(id) WHERE status = 'ACTIVE';
CREATE INDEX idx_pending_resumes ON resumes(id, job_posting_id) WHERE status IN ('SUBMITTED', 'UNDER_REVIEW');

-- Create triggers
CREATE TRIGGER update_users_updated_at 
    BEFORE UPDATE ON users 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_job_postings_updated_at 
    BEFORE UPDATE ON job_postings 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_resumes_updated_at 
    BEFORE UPDATE ON resumes 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_screening_results_updated_at 
    BEFORE UPDATE ON screening_results 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- Create materialized view for statistics
CREATE MATERIALIZED VIEW job_posting_stats AS
SELECT 
    jp.id,
    jp.title,
    jp.company_name,
    jp.status,
    COUNT(r.id) as total_applications,
    COUNT(CASE WHEN r.status = 'SUBMITTED' THEN 1 END) as submitted_count,
    COUNT(CASE WHEN r.status = 'UNDER_REVIEW' THEN 1 END) as under_review_count,
    COUNT(CASE WHEN r.status = 'SCREENED' THEN 1 END) as screened_count,
    COUNT(CASE WHEN r.status = 'SHORTLISTED' THEN 1 END) as shortlisted_count,
    COUNT(sr.id) as screening_results_count,
    AVG(sr.overall_score) as avg_overall_score,
    MAX(sr.overall_score) as max_overall_score,
    MIN(sr.overall_score) as min_overall_score
FROM job_postings jp
LEFT JOIN resumes r ON jp.id = r.job_posting_id
LEFT JOIN screening_results sr ON jp.id = sr.job_posting_id
GROUP BY jp.id, jp.title, jp.company_name, jp.status;

-- Create indexes on materialized view
CREATE INDEX idx_job_posting_stats_id ON job_posting_stats(id);
CREATE INDEX idx_job_posting_stats_company ON job_posting_stats(company_name);
CREATE INDEX idx_job_posting_stats_status ON job_posting_stats(status);

-- Create function to refresh statistics
CREATE OR REPLACE FUNCTION refresh_job_posting_stats()
RETURNS void AS $$
BEGIN
    REFRESH MATERIALIZED VIEW CONCURRENTLY job_posting_stats;
END;
$$ LANGUAGE plpgsql;

-- Add table comments
COMMENT ON TABLE users IS 'Stores user account information including HR managers, recruiters, and administrators';
COMMENT ON TABLE job_postings IS 'Stores job posting information created by users';
COMMENT ON TABLE resumes IS 'Stores resume files and extracted information submitted by candidates';
COMMENT ON TABLE screening_results IS 'Stores AI-generated screening results and analysis for resumes';

-- Add column comments
COMMENT ON COLUMN screening_results.overall_score IS 'Overall matching score between resume and job requirements (0-100)';
COMMENT ON COLUMN screening_results.processing_time IS 'Time taken to process the screening in milliseconds';
COMMENT ON COLUMN resumes.extracted_text IS 'Text content extracted from uploaded resume file';
COMMENT ON COLUMN job_postings.application_deadline IS 'Deadline for accepting applications for this job posting';

-- Schema creation completed
SELECT 'Database schema created successfully!' as status;
