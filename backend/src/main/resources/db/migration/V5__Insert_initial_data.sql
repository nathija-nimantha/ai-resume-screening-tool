-- Insert default admin user
-- Password is 'admin123' hashed with BCrypt (you should change this in production)
INSERT INTO users (username, email, password, first_name, last_name, role, company_name, is_active)
VALUES (
    'admin',
    'admin@airesume.com',
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM.lbESZaa8ge6Sz7XmO', -- admin123
    'System',
    'Administrator',
    'ADMIN',
    'AI Resume Screening Tool',
    true
);

-- Insert sample HR Manager
INSERT INTO users (username, email, password, first_name, last_name, role, company_name, is_active)
VALUES (
    'hr_manager',
    'hr@company.com',
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM.lbESZaa8ge6Sz7XmO', -- admin123
    'John',
    'Smith',
    'HR_MANAGER',
    'TechCorp Inc.',
    true
);

-- Insert sample Recruiter
INSERT INTO users (username, email, password, first_name, last_name, role, company_name, is_active)
VALUES (
    'recruiter',
    'recruiter@company.com',
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM.lbESZaa8ge6Sz7XmO', -- admin123
    'Jane',
    'Doe',
    'RECRUITER',
    'TechCorp Inc.',
    true
);

-- Insert sample job postings
INSERT INTO job_postings (title, description, requirements, experience_level, job_type, salary_range, location, company_name, department, status, application_deadline, created_by)
VALUES (
    'Senior Java Developer',
    'We are looking for an experienced Java developer to join our team. The ideal candidate will have strong experience in Spring Boot, microservices, and cloud technologies.',
    'Bachelor''s degree in Computer Science or related field, 5+ years of Java development experience, Experience with Spring Boot, Spring Security, REST APIs, Knowledge of microservices architecture, Experience with cloud platforms (AWS, Azure, GCP), Strong problem-solving skills',
    'Senior',
    'Full-time',
    '$80,000 - $120,000',
    'New York, NY',
    'TechCorp Inc.',
    'Engineering',
    'ACTIVE',
    CURRENT_TIMESTAMP + INTERVAL '30 days',
    (SELECT id FROM users WHERE username = 'hr_manager')
);

INSERT INTO job_postings (title, description, requirements, experience_level, job_type, salary_range, location, company_name, department, status, application_deadline, created_by)
VALUES (
    'Frontend React Developer',
    'Join our frontend team to build amazing user experiences using React and modern JavaScript technologies.',
    'Bachelor''s degree in Computer Science or related field, 3+ years of React development experience, Strong knowledge of JavaScript, HTML5, CSS3, Experience with Redux or Context API, Familiarity with modern build tools (Webpack, Vite), Understanding of responsive design principles',
    'Mid-level',
    'Full-time',
    '$60,000 - $90,000',
    'San Francisco, CA',
    'TechCorp Inc.',
    'Engineering',
    'ACTIVE',
    CURRENT_TIMESTAMP + INTERVAL '45 days',
    (SELECT id FROM users WHERE username = 'hr_manager')
);

INSERT INTO job_postings (title, description, requirements, experience_level, job_type, salary_range, location, company_name, department, status, application_deadline, created_by)
VALUES (
    'Data Scientist',
    'We are seeking a talented Data Scientist to help us derive insights from our data and build machine learning models.',
    'Master''s degree in Data Science, Statistics, or related field, 2+ years of data science experience, Strong Python and R programming skills, Experience with machine learning libraries (scikit-learn, TensorFlow, PyTorch), Knowledge of statistical analysis and data visualization, Experience with SQL and data warehousing',
    'Mid-level',
    'Full-time',
    '$90,000 - $130,000',
    'Remote',
    'TechCorp Inc.',
    'Data Science',
    'ACTIVE',
    CURRENT_TIMESTAMP + INTERVAL '60 days',
    (SELECT id FROM users WHERE username = 'recruiter')
);
