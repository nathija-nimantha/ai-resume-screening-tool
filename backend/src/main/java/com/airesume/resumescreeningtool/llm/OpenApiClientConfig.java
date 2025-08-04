package com.airesume.resumescreeningtool.llm;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.theokanning.openai.service.OpenAiService;

@Configuration
public class OpenApiClientConfig {

    @Value("${openai.api.key}")
    private String openaiApiKey;

    @Value("${openai.api.base-url}")
    // private String openaiApiBaseUrl;

    @Bean
    public OpenAiService openAiService() {
        // set the timeout
        return new OpenAiService(openaiApiKey, Duration.ofSeconds(60));
    }
}