package com.platformgenie.backend.ai.config;

import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAiConfig {

    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    @Bean
    @ConditionalOnProperty(prefix = "spring.ai.openai", name = "enabled", havingValue = "true")
    public OpenAiChatModel openAiChatModel() {
        // create OpenAiApi instance with API key
        org.springframework.ai.openai.api.OpenAiApi api = org.springframework.ai.openai.api.OpenAiApi.builder()
                .apiKey(apiKey)
                .build();

        return OpenAiChatModel.builder()
                .openAiApi(api) // <-- inject API
                .defaultOptions(OpenAiChatOptions.builder()
                        .model("gpt-4o")
                        .build())
                .build();
    }
}
