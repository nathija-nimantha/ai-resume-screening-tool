package com.airesume.resumescreeningtool.llm;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.theokanning.openai.service.OpenAiService;

@Configuration
public class OpenApiClientConfig {

    private static final Logger logger = LoggerFactory.getLogger(OpenApiClientConfig.class);

    @Value("${openai.api.key}")
    private String openaiApiKey;

    @Value("${openai.api.base-url}")
    // private String openaiApiBaseUrl;

    @Bean
    public OpenAiService openAiService() {
        // Check if API key is properly configured
        if (openaiApiKey == null || openaiApiKey.trim().isEmpty() || openaiApiKey.equals("sk-YOUR_OPENAI_API_KEY")) {
            logger.warn("OpenAI API key is not properly configured. Please set 'openai.api.key' in application.properties");
            logger.warn("Some features requiring AI processing may not work correctly.");
        }
        
        // Set the timeout
        return new OpenAiService(openaiApiKey, Duration.ofSeconds(60));
    }
}