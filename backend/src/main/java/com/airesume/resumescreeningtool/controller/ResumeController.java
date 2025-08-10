package com.airesume.resumescreeningtool.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.airesume.resumescreeningtool.dto.ResumeUploadResponseDto;
import com.airesume.resumescreeningtool.entity.Resume;
import com.airesume.resumescreeningtool.entity.ResumeStatus;
import com.airesume.resumescreeningtool.service.ResumeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/resumes")
@CrossOrigin(origins = "http://127.0.0.1:5500") // Allow requests from this origin
@RequiredArgsConstructor // Generates a constructor for final fields
public class ResumeController {

    private static final Logger logger = LoggerFactory.getLogger(ResumeController.class);

    private final ResumeService resumeService;

    /**
     * Upload a resume for a specific job posting
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadResume(
            @RequestParam("jobPostingId") Long jobPostingId,
            @RequestParam("candidateName") String candidateName,
            @RequestParam("candidateEmail") String candidateEmail,
            @RequestParam(value = "candidatePhone", required = false) String candidatePhone,
            @RequestParam("file") MultipartFile file) {

        try {
            logger.info("Received resume upload request for job posting: {}, candidate: {}", 
                       jobPostingId, candidateEmail);

            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("File is empty", "EMPTY_FILE"));
            }

            // Upload and process resume
            Resume resume = resumeService.uploadResume(jobPostingId, candidateName, 
                                                     candidateEmail, candidatePhone, file);

            // Convert to DTO
            ResumeUploadResponseDto responseDto = convertToDto(resume);

            logger.info("Resume uploaded successfully with ID: {}", resume.getId());
            return ResponseEntity.ok(createSuccessResponse("Resume uploaded successfully", responseDto));

        } catch (IllegalArgumentException e) {
            logger.warn("Invalid request: {}", e.getMessage());
            return ResponseEntity.badRequest()
                .body(createErrorResponse(e.getMessage(), "INVALID_REQUEST"));
        } catch (IllegalStateException e) {
            logger.warn("Duplicate application: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(createErrorResponse(e.getMessage(), "DUPLICATE_APPLICATION"));
        } catch (IOException e) {
            logger.error("File processing error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("Failed to process file", "FILE_PROCESSING_ERROR"));
        } catch (Exception e) {
            logger.error("Unexpected error during resume upload: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("An unexpected error occurred", "INTERNAL_ERROR"));
        }
    }

    /**
     * Get all resumes for a specific job posting
     */
    @GetMapping("/job/{jobPostingId}")
    public ResponseEntity<?> getResumesByJobPosting(@PathVariable Long jobPostingId) {
        try {
            List<Resume> resumes = resumeService.getResumesByJobPosting(jobPostingId);
            List<ResumeUploadResponseDto> responseDtos = resumes.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

            return ResponseEntity.ok(createSuccessResponse("Resumes retrieved successfully", responseDtos));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(createErrorResponse(e.getMessage(), "INVALID_REQUEST"));
        }
    }

    /**
     * Get resumes by status for a specific job posting
     */
    @GetMapping("/job/{jobPostingId}/status/{status}")
    public ResponseEntity<?> getResumesByJobPostingAndStatus(
            @PathVariable Long jobPostingId, 
            @PathVariable ResumeStatus status) {
        try {
            List<Resume> resumes = resumeService.getResumesByJobPostingAndStatus(jobPostingId, status);
            List<ResumeUploadResponseDto> responseDtos = resumes.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

            return ResponseEntity.ok(createSuccessResponse("Resumes retrieved successfully", responseDtos));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(createErrorResponse(e.getMessage(), "INVALID_REQUEST"));
        }
    }

    /**
     * Get a specific resume by ID
     */
    @GetMapping("/{resumeId}")
    public ResponseEntity<?> getResumeById(@PathVariable Long resumeId) {
        try {
            Resume resume = resumeService.getResumeById(resumeId);
            ResumeUploadResponseDto responseDto = convertToDto(resume);
            return ResponseEntity.ok(createSuccessResponse("Resume retrieved successfully", responseDto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Update resume status
     */
    @PutMapping("/{resumeId}/status")
    public ResponseEntity<?> updateResumeStatus(
            @PathVariable Long resumeId,
            @RequestParam ResumeStatus status) {
        try {
            Resume updatedResume = resumeService.updateResumeStatus(resumeId, status);
            ResumeUploadResponseDto responseDto = convertToDto(updatedResume);
            return ResponseEntity.ok(createSuccessResponse("Resume status updated successfully", responseDto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(createErrorResponse(e.getMessage(), "INVALID_REQUEST"));
        }
    }

    /**
     * Delete a resume
     */
    @DeleteMapping("/{resumeId}")
    public ResponseEntity<?> deleteResume(@PathVariable Long resumeId) {
        try {
            resumeService.deleteResume(resumeId);
            return ResponseEntity.ok(createSuccessResponse("Resume deleted successfully", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get unscreened resumes for a job posting
     */
    @GetMapping("/job/{jobPostingId}/unscreened")
    public ResponseEntity<?> getUnscreenedResumes(@PathVariable Long jobPostingId) {
        try {
            List<Resume> resumes = resumeService.getUnscreenedResumes(jobPostingId);
            List<ResumeUploadResponseDto> responseDtos = resumes.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

            return ResponseEntity.ok(createSuccessResponse("Unscreened resumes retrieved successfully", responseDtos));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(createErrorResponse(e.getMessage(), "INVALID_REQUEST"));
        }
    }

    /**
     * Get resume count by status for a job posting
     */
    @GetMapping("/job/{jobPostingId}/count")
    public ResponseEntity<?> getResumeCountByStatus(@PathVariable Long jobPostingId) {
        try {
            Map<String, Long> counts = new HashMap<>();
            for (ResumeStatus status : ResumeStatus.values()) {
                long count = resumeService.countResumesByStatus(jobPostingId, status);
                counts.put(status.name(), count);
            }
            
            return ResponseEntity.ok(createSuccessResponse("Resume counts retrieved successfully", counts));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(createErrorResponse(e.getMessage(), "INVALID_REQUEST"));
        }
    }

    /**
     * Check file upload limitations
     */
    @GetMapping("/upload-info")
    public ResponseEntity<?> getUploadInfo() {
        Map<String, Object> uploadInfo = new HashMap<>();
        uploadInfo.put("maxFileSize", "10MB");
        uploadInfo.put("allowedTypes", new String[]{"PDF", "DOC", "DOCX", "TXT"});
        uploadInfo.put("allowedMimeTypes", new String[]{
            "application/pdf", 
            "application/msword", 
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "text/plain"
        });
        
        return ResponseEntity.ok(createSuccessResponse("Upload information retrieved successfully", uploadInfo));
    }

    /**
     * Converts Resume entity to DTO
     */
    private ResumeUploadResponseDto convertToDto(Resume resume) {
        ResumeUploadResponseDto dto = new ResumeUploadResponseDto();
        dto.setId(resume.getId());
        dto.setCandidateName(resume.getCandidateName());
        dto.setCandidateEmail(resume.getCandidateEmail());
        dto.setCandidatePhone(resume.getCandidatePhone());
        dto.setFileName(resume.getFileName());
        dto.setFileSize(resume.getFileSize());
        dto.setContentType(resume.getContentType());
        dto.setYearsOfExperience(resume.getYearsOfExperience());
        dto.setSkills(resume.getSkills());
        dto.setEducation(resume.getEducation());
        dto.setStatus(resume.getStatus());
        dto.setJobPostingId(resume.getJobPosting().getId());
        dto.setJobPostingTitle(resume.getJobPosting().getTitle());
        dto.setSubmissionDate(resume.getSubmissionDate());
        dto.setCreatedAt(resume.getCreatedAt());
        return dto;
    }

    /**
     * Creates a success response
     */
    private Map<String, Object> createSuccessResponse(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("data", data);
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }

    /**
     * Creates an error response
     */
    private Map<String, Object> createErrorResponse(String message, String errorCode) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        response.put("errorCode", errorCode);
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
}
