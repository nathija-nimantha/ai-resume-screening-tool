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
    
    -- Foreign key constraint
    CONSTRAINT fk_job_postings_created_by FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE CASCADE
);

-- Create indexes for better query performance
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

-- Create trigger for job_postings table
CREATE TRIGGER update_job_postings_updated_at 
    BEFORE UPDATE ON job_postings 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
