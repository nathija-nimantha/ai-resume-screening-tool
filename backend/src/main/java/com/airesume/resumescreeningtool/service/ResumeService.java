package com.airesume.resumescreeningtool.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.airesume.resumescreeningtool.entity.JobPosting;
import com.airesume.resumescreeningtool.entity.Resume;
import com.airesume.resumescreeningtool.entity.ResumeStatus;
import com.airesume.resumescreeningtool.repository.JobPostingRepository;
import com.airesume.resumescreeningtool.repository.ResumeRepository;

@Service
@Transactional
public class ResumeService {

    private static final Logger logger = LoggerFactory.getLogger(ResumeService.class);

    @Autowired
    private ResumeRepository resumeRepository;

    @Autowired
    private JobPostingRepository jobPostingRepository;

    @Autowired
    private FileUploadService fileUploadService;

    /**
     * Uploads and processes a resume for a specific job posting
     */
    public Resume uploadResume(Long jobPostingId, String candidateName, String candidateEmail, 
                              String candidatePhone, MultipartFile file) throws IOException {
        
        logger.info("Starting resume upload for job posting ID: {}, candidate: {}", jobPostingId, candidateEmail);

        // Validate job posting exists
        JobPosting jobPosting = jobPostingRepository.findById(jobPostingId)
                .orElseThrow(() -> new IllegalArgumentException("Job posting not found with ID: " + jobPostingId));

        // Check if candidate has already applied for this job
        Optional<Resume> existingResume = resumeRepository.findByCandidateEmailAndJobPosting(candidateEmail, jobPosting);
        if (existingResume.isPresent()) {
            throw new IllegalStateException("Candidate has already applied for this job posting");
        }

        // Upload and process the file
        FileUploadService.FileUploadResult uploadResult = fileUploadService.storeFile(file);

        // Create resume entity
        Resume resume = new Resume();
        resume.setCandidateName(candidateName);
        resume.setCandidateEmail(candidateEmail);
        resume.setCandidatePhone(candidatePhone);
        resume.setFileName(uploadResult.getOriginalFilename());
        resume.setFilePath(uploadResult.getFilePath());
        resume.setFileSize(uploadResult.getFileSize());
        resume.setContentType(uploadResult.getContentType());
        resume.setExtractedText(uploadResult.getExtractedText());
        resume.setJobPosting(jobPosting);
        resume.setStatus(ResumeStatus.SUBMITTED);

        // Extract additional information from resume text
        extractResumeInformation(resume, uploadResult.getExtractedText());

        // Save resume
        Resume savedResume = resumeRepository.save(resume);

        logger.info("Resume uploaded successfully with ID: {}", savedResume.getId());
        return savedResume;
    }

    /**
     * Extracts structured information from resume text
     */
    private void extractResumeInformation(Resume resume, String extractedText) {
        if (extractedText == null || extractedText.trim().isEmpty()) {
            return;
        }

        String text = extractedText.toLowerCase();

        // Extract years of experience
        Integer yearsOfExperience = extractYearsOfExperience(text);
        resume.setYearsOfExperience(yearsOfExperience);

        // Extract skills (basic pattern matching)
        String skills = extractSkills(extractedText);
        resume.setSkills(skills);

        // Extract education
        String education = extractEducation(extractedText);
        resume.setEducation(education);

        // Extract work experience
        String workExperience = extractWorkExperience(extractedText);
        resume.setWorkExperience(workExperience);

        // Extract certifications
        String certifications = extractCertifications(extractedText);
        resume.setCertifications(certifications);
    }

    /**
     * Extracts years of experience from resume text
     */
    private Integer extractYearsOfExperience(String text) {
        try {
            // Look for patterns like "5 years experience", "3+ years", etc.
            Pattern pattern = Pattern.compile("(\\d+)\\s*\\+?\\s*year[s]?\\s*(of\\s*)?(experience|exp)", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(text);
            
            int maxYears = 0;
            while (matcher.find()) {
                int years = Integer.parseInt(matcher.group(1));
                maxYears = Math.max(maxYears, years);
            }
            
            return maxYears > 0 ? maxYears : null;
        } catch (NumberFormatException e) {
            logger.warn("Error extracting years of experience: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Extracts skills from resume text
     */
    private String extractSkills(String text) {
        try {
            // Look for skills section
            Pattern skillsPattern = Pattern.compile("(?:skills|technical skills|core competencies|technologies)[:\\s]*([^\\n\\r]*(?:\\n[^\\n\\r]*){0,10})", Pattern.CASE_INSENSITIVE);
            Matcher matcher = skillsPattern.matcher(text);
            
            if (matcher.find()) {
                String skillsSection = matcher.group(1);
                // Clean up and return first 500 characters
                skillsSection = skillsSection.replaceAll("\\s+", " ").trim();
                return skillsSection.length() > 500 ? skillsSection.substring(0, 500) + "..." : skillsSection;
            }
            
            return null;
        } catch (Exception e) {
            logger.warn("Error extracting skills: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Extracts education from resume text
     */
    private String extractEducation(String text) {
        try {
            // Look for education section
            Pattern educationPattern = Pattern.compile("(?:education|academic|qualification)[:\\s]*([^\\n\\r]*(?:\\n[^\\n\\r]*){0,5})", Pattern.CASE_INSENSITIVE);
            Matcher matcher = educationPattern.matcher(text);
            
            if (matcher.find()) {
                String educationSection = matcher.group(1);
                educationSection = educationSection.replaceAll("\\s+", " ").trim();
                return educationSection.length() > 300 ? educationSection.substring(0, 300) + "..." : educationSection;
            }
            
            return null;
        } catch (Exception e) {
            logger.warn("Error extracting education: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Extracts work experience from resume text
     */
    private String extractWorkExperience(String text) {
        try {
            // Look for work experience section
            Pattern experiencePattern = Pattern.compile("(?:experience|work experience|employment|professional experience)[:\\s]*([^\\n\\r]*(?:\\n[^\\n\\r]*){0,15})", Pattern.CASE_INSENSITIVE);
            Matcher matcher = experiencePattern.matcher(text);
            
            if (matcher.find()) {
                String experienceSection = matcher.group(1);
                experienceSection = experienceSection.replaceAll("\\s+", " ").trim();
                return experienceSection.length() > 1000 ? experienceSection.substring(0, 1000) + "..." : experienceSection;
            }
            
            return null;
        } catch (Exception e) {
            logger.warn("Error extracting work experience: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Extracts certifications from resume text
     */
    private String extractCertifications(String text) {
        try {
            // Look for certifications section
            Pattern certificationPattern = Pattern.compile("(?:certification[s]?|certificate[s]?|license[s]?)[:\\s]*([^\\n\\r]*(?:\\n[^\\n\\r]*){0,8})", Pattern.CASE_INSENSITIVE);
            Matcher matcher = certificationPattern.matcher(text);
            
            if (matcher.find()) {
                String certificationSection = matcher.group(1);
                certificationSection = certificationSection.replaceAll("\\s+", " ").trim();
                return certificationSection.length() > 500 ? certificationSection.substring(0, 500) + "..." : certificationSection;
            }
            
            return null;
        } catch (Exception e) {
            logger.warn("Error extracting certifications: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Gets all resumes for a specific job posting
     */
    public List<Resume> getResumesByJobPosting(Long jobPostingId) {
        JobPosting jobPosting = jobPostingRepository.findById(jobPostingId)
                .orElseThrow(() -> new IllegalArgumentException("Job posting not found with ID: " + jobPostingId));
        return resumeRepository.findByJobPosting(jobPosting);
    }

    /**
     * Gets resumes by status for a specific job posting
     */
    public List<Resume> getResumesByJobPostingAndStatus(Long jobPostingId, ResumeStatus status) {
        JobPosting jobPosting = jobPostingRepository.findById(jobPostingId)
                .orElseThrow(() -> new IllegalArgumentException("Job posting not found with ID: " + jobPostingId));
        return resumeRepository.findByJobPostingAndStatus(jobPosting, status);
    }

    /**
     * Updates resume status
     */
    public Resume updateResumeStatus(Long resumeId, ResumeStatus newStatus) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new IllegalArgumentException("Resume not found with ID: " + resumeId));
        
        resume.setStatus(newStatus);
        return resumeRepository.save(resume);
    }

    /**
     * Gets resume by ID
     */
    public Resume getResumeById(Long resumeId) {
        return resumeRepository.findById(resumeId)
                .orElseThrow(() -> new IllegalArgumentException("Resume not found with ID: " + resumeId));
    }

    /**
     * Deletes a resume and its associated file
     */
    public void deleteResume(Long resumeId) {
        Resume resume = getResumeById(resumeId);
        
        // Delete the file from storage
        String filename = resume.getFilePath().substring(resume.getFilePath().lastIndexOf('/') + 1);
        fileUploadService.deleteFile(filename);
        
        // Delete from database
        resumeRepository.delete(resume);
        
        logger.info("Resume deleted successfully with ID: {}", resumeId);
    }

    /**
     * Gets unscreened resumes for a job posting
     */
    public List<Resume> getUnscreenedResumes(Long jobPostingId) {
        JobPosting jobPosting = jobPostingRepository.findById(jobPostingId)
                .orElseThrow(() -> new IllegalArgumentException("Job posting not found with ID: " + jobPostingId));
        return resumeRepository.findByJobPostingWithoutScreeningResults(jobPosting);
    }

    /**
     * Counts resumes by status for a job posting
     */
    public long countResumesByStatus(Long jobPostingId, ResumeStatus status) {
        JobPosting jobPosting = jobPostingRepository.findById(jobPostingId)
                .orElseThrow(() -> new IllegalArgumentException("Job posting not found with ID: " + jobPostingId));
        return resumeRepository.countByJobPostingAndStatus(jobPosting, status);
    }

    /**
     * Gets all resumes (admin function)
     */
    public List<Resume> getAllResumes() {
        return resumeRepository.findAll();
    }
}
