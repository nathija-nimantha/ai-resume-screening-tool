# Database Schema and Migration Documentation

## Overview
This document describes the database schema, migration setup, and configuration for the AI Resume Screening Tool.

## Database Configuration

### Database Details
- **Database Engine**: PostgreSQL 12+
- **Database Name**: `rst_db`
- **Default Username**: `postgres`
- **Default Password**: `1234`
- **Host**: `localhost`
- **Port**: `5432`

### Migration Tool
- **Tool**: Flyway
- **Version**: 9.22.3
- **Migration Location**: `src/main/resources/db/migration`
- **Baseline**: Enabled with version 0

## Migration Files

### V1__Create_users_table.sql
**Purpose**: Creates the users table with authentication and user management features

**Features**:
- User authentication fields (username, email, password)
- Role-based access control (HR_MANAGER, RECRUITER, HIRING_MANAGER, ADMIN)
- Company association
- Active/inactive status tracking
- Automatic timestamp management
- Performance indexes

### V2__Create_job_postings_table.sql
**Purpose**: Creates the job postings table for job management

**Features**:
- Complete job posting information
- Status tracking (ACTIVE, CLOSED, DRAFT, PAUSED, EXPIRED)
- Application deadline management
- Foreign key relationship to users
- Comprehensive indexing for search and filtering

### V3__Create_resumes_table.sql
**Purpose**: Creates the resumes table for candidate application management

**Features**:
- Candidate information storage
- File metadata (name, path, size, type)
- Text extraction and parsing
- Skills, education, and experience tracking
- Status management throughout hiring process
- Duplicate prevention (one application per candidate per job)
- Full-text search capabilities

### V4__Create_screening_results_table.sql
**Purpose**: Creates the screening results table for AI analysis storage

**Features**:
- Comprehensive scoring system (overall, skills, experience, education)
- AI feedback and analysis
- Recommendation status tracking
- Keyword matching analysis
- Performance monitoring (processing time, version tracking)
- Unique constraint to prevent duplicate screenings

### V5__Insert_initial_data.sql
**Purpose**: Inserts initial data for system bootstrapping

**Includes**:
- Default admin user (username: `admin`, password: `admin123`)
- Sample HR manager and recruiter accounts
- Three sample job postings across different departments
- All passwords are BCrypt hashed

### V6__Add_constraints_and_optimizations.sql
**Purpose**: Adds advanced constraints, optimizations, and analytics features

**Features**:
- Score range validation (0-100)
- Positive value constraints
- Materialized view for job statistics
- Partial indexes for performance
- Table and column documentation
- Performance monitoring functions

## Database Schema

### Entity Relationships
```
users (1) ────────────── (M) job_postings
                              │
                              │ (1)
                              ▼
                          (M) resumes ────── (M) screening_results (M) ────── (1) users
                              │                       │
                              │ (1)                 │ (1)
                              └─────────────────────┘
                                  job_postings
```

### Key Features

#### 1. Automatic Timestamp Management
- All tables have `created_at` and `updated_at` columns
- Triggers automatically update `updated_at` on record modification
- Default timestamps set to `CURRENT_TIMESTAMP`

#### 2. Performance Optimization
- **Regular Indexes**: On frequently queried columns
- **Composite Indexes**: For common multi-column queries
- **Partial Indexes**: For subset queries (active records, pending status)
- **GIN Indexes**: For full-text search capabilities
- **Materialized Views**: For complex analytics queries

#### 3. Data Integrity
- **Foreign Key Constraints**: Maintain referential integrity
- **Check Constraints**: Validate data ranges and values
- **Unique Constraints**: Prevent duplicate records
- **NOT NULL Constraints**: Ensure required data presence

#### 4. Search Capabilities
- **Full-text Search**: PostgreSQL GIN indexes on text fields
- **Case-insensitive Search**: LOWER() function usage
- **Keyword Matching**: Specialized fields for AI analysis
- **Multi-criteria Filtering**: Complex WHERE clause support

## Setup Instructions

### 1. Automatic Setup (Recommended)
```bash
# Windows
cd backend\scripts
setup-database.bat

# Linux/Mac
cd backend/scripts
chmod +x setup-database.sh
./setup-database.sh
```

### 2. Manual Setup
```sql
-- Create database
CREATE DATABASE rst_db;

-- Run the complete schema script
\i scripts/create-schema.sql
```

### 3. Maven/Flyway Setup
```bash
# Run migrations manually
mvn flyway:migrate

# Clean and rebuild (development only)
mvn flyway:clean flyway:migrate

# Get migration info
mvn flyway:info
```

### 4. Spring Boot Integration
The application automatically runs migrations on startup when:
- `spring.flyway.locations=classpath:db/migration` is configured
- Flyway is on the classpath
- Database connection is available

## Configuration Files

### application.properties
```properties
# Database connection
spring.datasource.url=jdbc:postgresql://localhost:5432/rst_db
spring.datasource.username=postgres
spring.datasource.password=1234
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true

# Flyway configuration
spring.flyway.locations=classpath:db/migration
spring.flyway.baseline-on-migrate=true
spring.flyway.baseline-version=0
spring.flyway.validate-on-migrate=true
```

### pom.xml Dependencies
```xml
<!-- Flyway for database migrations -->
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-database-postgresql</artifactId>
</dependency>
```

## Default Credentials

### Admin User
- **Username**: `admin`
- **Email**: `admin@airesume.com`
- **Password**: `admin123`
- **Role**: `ADMIN`

### Sample HR Manager
- **Username**: `hr_manager`
- **Email**: `hr@company.com`
- **Password**: `admin123`
- **Role**: `HR_MANAGER`

### Sample Recruiter
- **Username**: `recruiter`
- **Email**: `recruiter@company.com`
- **Password**: `admin123`
- **Role**: `RECRUITER`

## Performance Considerations

### 1. Indexing Strategy
- Primary keys automatically indexed
- Foreign keys indexed for join performance
- Frequently filtered columns indexed
- Full-text search fields have GIN indexes
- Composite indexes for multi-column queries

### 2. Query Optimization
- Use partial indexes for subset queries
- Materialized views for complex analytics
- Proper JOIN strategies with lazy loading
- LIMIT and OFFSET for pagination

### 3. Data Growth Management
- Regular VACUUM and ANALYZE operations
- Monitor index usage and effectiveness
- Consider partitioning for large tables
- Archive old data periodically

## Maintenance Tasks

### 1. Regular Maintenance
```sql
-- Refresh statistics materialized view
SELECT refresh_job_posting_stats();

-- Analyze tables for query optimization
ANALYZE users, job_postings, resumes, screening_results;

-- Check index usage
SELECT schemaname, tablename, indexname, idx_scan, idx_tup_read, idx_tup_fetch 
FROM pg_stat_user_indexes ORDER BY idx_scan DESC;
```

### 2. Backup Strategy
```bash
# Full database backup
pg_dump -h localhost -U postgres rst_db > backup_$(date +%Y%m%d).sql

# Schema-only backup
pg_dump -h localhost -U postgres --schema-only rst_db > schema_backup.sql

# Data-only backup
pg_dump -h localhost -U postgres --data-only rst_db > data_backup.sql
```

### 3. Migration Management
```bash
# Check migration status
mvn flyway:info

# Validate migrations
mvn flyway:validate

# Repair migration checksums (if needed)
mvn flyway:repair
```

## Troubleshooting

### Common Issues

1. **Migration Checksum Mismatch**
   - Run `mvn flyway:repair` to fix checksums
   - Ensure migration files haven't been modified

2. **Database Connection Issues**
   - Verify PostgreSQL is running
   - Check connection parameters in application.properties
   - Ensure database exists

3. **Permission Issues**
   - Verify user has sufficient database privileges
   - Check PostgreSQL authentication configuration

4. **Performance Issues**
   - Run ANALYZE on affected tables
   - Check index usage statistics
   - Monitor query execution plans

## Security Considerations

1. **Password Security**
   - All passwords are BCrypt hashed
   - Change default passwords in production
   - Use strong password policies

2. **Database Security**
   - Use dedicated database users for applications
   - Limit database privileges to necessary operations
   - Enable SSL connections for production

3. **Data Privacy**
   - Implement data retention policies
   - Consider encryption for sensitive data
   - Regular security audits

This database schema provides a robust foundation for the AI Resume Screening Tool with excellent performance, data integrity, and scalability characteristics.
