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
    processing_time BIGINT, -- Processing time in milliseconds
    job_posting_id BIGINT NOT NULL,
    resume_id BIGINT NOT NULL,
    screened_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Foreign key constraints
    CONSTRAINT fk_screening_results_job_posting FOREIGN KEY (job_posting_id) REFERENCES job_postings(id) ON DELETE CASCADE,
    CONSTRAINT fk_screening_results_resume FOREIGN KEY (resume_id) REFERENCES resumes(id) ON DELETE CASCADE,
    CONSTRAINT fk_screening_results_screened_by FOREIGN KEY (screened_by) REFERENCES users(id) ON DELETE SET NULL,
    
    -- Unique constraint to prevent duplicate screening results
    CONSTRAINT uk_screening_results_job_resume UNIQUE (job_posting_id, resume_id)
);

-- Create indexes for better query performance
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

-- Composite indexes for common query patterns
CREATE INDEX idx_screening_results_job_status ON screening_results(job_posting_id, recommendation_status);
CREATE INDEX idx_screening_results_job_score ON screening_results(job_posting_id, overall_score DESC);

-- Full-text search indexes for AI feedback and analysis
CREATE INDEX idx_screening_results_ai_feedback_gin ON screening_results USING gin(to_tsvector('english', ai_feedback));
CREATE INDEX idx_screening_results_strengths_gin ON screening_results USING gin(to_tsvector('english', strengths));
CREATE INDEX idx_screening_results_weaknesses_gin ON screening_results USING gin(to_tsvector('english', weaknesses));

-- Create trigger for screening_results table
CREATE TRIGGER update_screening_results_updated_at 
    BEFORE UPDATE ON screening_results 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
