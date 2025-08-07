# AI Resume Screening Tool - Troubleshooting Guide

## Common Issues and Solutions

### 1. Database Connection Errors

**Error**: `org.postgresql.util.PSQLException: Connection refused`

**Solutions**:

#### Option A: Set up PostgreSQL (Recommended for production)
1. **Install PostgreSQL**:
   ```cmd
   winget install PostgreSQL.PostgreSQL
   ```
   Or download from: https://www.postgresql.org/download/windows/

2. **Start PostgreSQL service**:
   ```cmd
   net start postgresql-x64-15
   ```

3. **Create database**:
   ```cmd
   psql -U postgres -c "CREATE DATABASE rst_db;"
   ```

4. **Run the quick setup script**:
   ```cmd
   quick-setup.bat
   ```

#### Option B: Use H2 In-Memory Database (For testing)
1. **Copy H2 configuration**:
   ```cmd
   copy application-h2.properties src\main\resources\application.properties
   ```

2. **Start the application**:
   ```cmd
   mvn spring-boot:run
   ```

3. **Access H2 Console** (optional):
   - URL: http://localhost:8080/h2-console
   - JDBC URL: `jdbc:h2:mem:testdb`
   - Username: `sa`
   - Password: (leave empty)

### 2. OpenAI API Key Error

**Error**: `Invalid API key` or OpenAI service errors

**Solution**:
1. **Get your OpenAI API key** from: https://platform.openai.com/api-keys
2. **Update application.properties**:
   ```properties
   openai.api.key=sk-your-actual-api-key-here
   ```

### 3. File Upload Directory Error

**Error**: `Could not create upload directory`

**Solution**:
The application will automatically create the `uploads/resumes` directory. If you get permission errors:

1. **Manually create directory**:
   ```cmd
   mkdir uploads\resumes
   ```

2. **Or change the upload directory** in `application.properties`:
   ```properties
   file.upload.dir=C:/temp/resumes
   ```

### 4. Maven Build Errors

**Error**: Compilation failures or dependency issues

**Solutions**:
1. **Clean and rebuild**:
   ```cmd
   mvn clean compile
   ```

2. **Update dependencies**:
   ```cmd
   mvn dependency:resolve
   ```

3. **Clear Maven cache**:
   ```cmd
   mvn dependency:purge-local-repository
   ```

### 5. Spring Boot Startup Errors

**Error**: Application fails to start

**Solutions**:
1. **Check Java version** (should be 17+):
   ```cmd
   java -version
   ```

2. **Run with debug info**:
   ```cmd
   mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug"
   ```

3. **Check application logs** for specific error messages.

## Running the Application

### Method 1: With PostgreSQL
1. Run `quick-setup.bat` to set up the database
2. Start the application:
   ```cmd
   mvn spring-boot:run
   ```

### Method 2: With H2 Database
1. Copy H2 configuration:
   ```cmd
   copy application-h2.properties src\main\resources\application.properties
   ```
2. Start the application:
   ```cmd
   mvn spring-boot:run
   ```

## API Endpoints

Once running, the application will be available at: `http://localhost:8080`

### Key endpoints:
- **Upload Resume**: `POST /api/resumes/upload`
- **Get Resumes**: `GET /api/resumes/job/{jobPostingId}`
- **Upload Info**: `GET /api/resumes/upload-info`

## Testing the Application

### Upload a Resume
```bash
curl -X POST http://localhost:8080/api/resumes/upload \
  -F "jobPostingId=1" \
  -F "candidateName=John Doe" \
  -F "candidateEmail=john@example.com" \
  -F "file=@resume.pdf"
```

## Configuration Files

### For PostgreSQL:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/rst_db
spring.datasource.username=postgres
spring.datasource.password=1234
spring.jpa.hibernate.ddl-auto=update
```

### For H2:
```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
spring.flyway.enabled=false
```

## Need More Help?

If you're still experiencing issues:

1. **Check the application logs** for detailed error messages
2. **Verify all dependencies** are properly installed
3. **Ensure Java 17+** is installed and configured
4. **Check firewall settings** if using external databases

The application includes comprehensive error handling and logging to help diagnose issues.
