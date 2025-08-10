package com.airesume.resumescreeningtool.dto;

import java.time.LocalDateTime;

import com.airesume.resumescreeningtool.entity.ResumeStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumeUploadResponseDto {
    private Long id;
    private String candidateName;
    private String candidateEmail;
    private String candidatePhone;
    private String fileName;
    private long fileSize;
    private String contentType;
    private Integer yearsOfExperience;
    private String skills;
    private String education;
    private ResumeStatus status;
    private Long jobPostingId;
    private String jobPostingTitle;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime submissionDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}
