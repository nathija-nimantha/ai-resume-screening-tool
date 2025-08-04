-- Add additional constraints and performance optimizations

-- Add check constraints for score ranges (0-100)
ALTER TABLE screening_results 
ADD CONSTRAINT chk_overall_score_range CHECK (overall_score >= 0 AND overall_score <= 100);

ALTER TABLE screening_results 
ADD CONSTRAINT chk_skills_score_range CHECK (skills_score >= 0 AND skills_score <= 100);

ALTER TABLE screening_results 
ADD CONSTRAINT chk_experience_score_range CHECK (experience_score >= 0 AND experience_score <= 100);

ALTER TABLE screening_results 
ADD CONSTRAINT chk_education_score_range CHECK (education_score >= 0 AND education_score <= 100);

-- Add check constraint for years of experience (non-negative)
ALTER TABLE resumes 
ADD CONSTRAINT chk_years_of_experience_positive CHECK (years_of_experience >= 0);

-- Add check constraint for file size (positive)
ALTER TABLE resumes 
ADD CONSTRAINT chk_file_size_positive CHECK (file_size > 0);

-- Add check constraint for processing time (non-negative)
ALTER TABLE screening_results 
ADD CONSTRAINT chk_processing_time_positive CHECK (processing_time >= 0);

-- Create materialized view for job posting statistics
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

-- Create index on the materialized view
CREATE INDEX idx_job_posting_stats_id ON job_posting_stats(id);
CREATE INDEX idx_job_posting_stats_company ON job_posting_stats(company_name);
CREATE INDEX idx_job_posting_stats_status ON job_posting_stats(status);

-- Create function to refresh job posting statistics
CREATE OR REPLACE FUNCTION refresh_job_posting_stats()
RETURNS void AS $$
BEGIN
    REFRESH MATERIALIZED VIEW CONCURRENTLY job_posting_stats;
END;
$$ LANGUAGE plpgsql;

-- Create partial indexes for better performance on active data
CREATE INDEX idx_active_job_postings ON job_postings(id) WHERE status = 'ACTIVE';
CREATE INDEX idx_pending_resumes ON resumes(id, job_posting_id) WHERE status IN ('SUBMITTED', 'UNDER_REVIEW');
CREATE INDEX idx_unscreened_resumes ON resumes(id, job_posting_id) WHERE id NOT IN (SELECT resume_id FROM screening_results);

-- Add comments to tables for documentation
COMMENT ON TABLE users IS 'Stores user account information including HR managers, recruiters, and administrators';
COMMENT ON TABLE job_postings IS 'Stores job posting information created by users';
COMMENT ON TABLE resumes IS 'Stores resume files and extracted information submitted by candidates';
COMMENT ON TABLE screening_results IS 'Stores AI-generated screening results and analysis for resumes';

-- Add comments to important columns
COMMENT ON COLUMN screening_results.overall_score IS 'Overall matching score between resume and job requirements (0-100)';
COMMENT ON COLUMN screening_results.processing_time IS 'Time taken to process the screening in milliseconds';
COMMENT ON COLUMN resumes.extracted_text IS 'Text content extracted from uploaded resume file';
COMMENT ON COLUMN job_postings.application_deadline IS 'Deadline for accepting applications for this job posting';
