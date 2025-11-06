package com.platformgenie.backend.ai.service;

import com.platformgenie.backend.ai.dto.InfrastructureSpec;
import com.platformgenie.backend.ai.dto.PromptHistory;
import com.platformgenie.backend.util.Constant;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PromptService {

    private static final Logger log = LoggerFactory.getLogger(PromptService.class);

    // Simple in-memory versioning map
    @Getter
    private final Map<Integer, PromptHistory> promptHistory = new HashMap<>();
    private int versionCounter = 1;

    public String buildPrompt(InfrastructureSpec spec) {
        StringBuilder prompt = new StringBuilder();

        // Header
        prompt.append(Constant.AI_PROMPT_HEADER);
        prompt.append(String.format(Constant.AI_CLOUD_LINE, spec.getCloudProvider()));
        prompt.append(Constant.AI_RESOURCES_HEADER);

        // Add resources
        spec.getResource().forEach(resource ->
                prompt.append(String.format(Constant.AI_RESOURCE_LINE, resource.getType(), resource.getName()))
        );

        // Optional database
        if (spec.isDatabaseRequired()) {
            prompt.append(String.format(Constant.AI_DATABASE_LINE, spec.getDatabaseType()));
        }

        // Optional environment
        if (spec.getEnvironment() != null) {
            prompt.append(String.format(Constant.AI_ENV_LINE, spec.getEnvironment()));
        }

        String promptStr = prompt.toString();

        // Validation
        validatePrompt(promptStr);

        // Logging & versioning
        log.info("Generated Prompt for spec {}: {}", spec.getId(), promptStr);
        promptHistory.put(versionCounter++, new PromptHistory(promptStr, LocalDateTime.now()));

        return promptStr;
    }

    private void validatePrompt(String prompt) {
        if (prompt.isEmpty()) {
            throw new IllegalArgumentException("Prompt cannot be empty");
        }
        if (!prompt.contains("Terraform")) {
            log.warn("Prompt does not mention Terraform explicitly");
        }
    }
}
