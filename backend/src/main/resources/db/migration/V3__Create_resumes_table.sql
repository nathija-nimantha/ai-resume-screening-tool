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
    
    -- Foreign key constraint
    CONSTRAINT fk_resumes_job_posting FOREIGN KEY (job_posting_id) REFERENCES job_postings(id) ON DELETE CASCADE,
    
    -- Unique constraint to prevent duplicate applications
    CONSTRAINT uk_resumes_candidate_job UNIQUE (candidate_email, job_posting_id)
);

-- Create indexes for better query performance
CREATE INDEX idx_resumes_candidate_name ON resumes(candidate_name);
CREATE INDEX idx_resumes_candidate_email ON resumes(candidate_email);
CREATE INDEX idx_resumes_status ON resumes(status);
CREATE INDEX idx_resumes_job_posting_id ON resumes(job_posting_id);
CREATE INDEX idx_resumes_years_of_experience ON resumes(years_of_experience);
CREATE INDEX idx_resumes_submission_date ON resumes(submission_date);
CREATE INDEX idx_resumes_created_at ON resumes(created_at);

-- Full-text search indexes for better content search
CREATE INDEX idx_resumes_skills_gin ON resumes USING gin(to_tsvector('english', skills));
CREATE INDEX idx_resumes_extracted_text_gin ON resumes USING gin(to_tsvector('english', extracted_text));
CREATE INDEX idx_resumes_education_gin ON resumes USING gin(to_tsvector('english', education));
CREATE INDEX idx_resumes_work_experience_gin ON resumes USING gin(to_tsvector('english', work_experience));

-- Create trigger for resumes table
CREATE TRIGGER update_resumes_updated_at 
    BEFORE UPDATE ON resumes 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
