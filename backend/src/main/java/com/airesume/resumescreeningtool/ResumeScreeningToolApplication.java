package com.airesume.resumescreeningtool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class ResumeScreeningToolApplication {

    public static void main(String[] args) {
        SpringApplication.run(ResumeScreeningToolApplication.class, args);
        log.info("Resume Screening Tool Application has started successfully.");
    }
}
