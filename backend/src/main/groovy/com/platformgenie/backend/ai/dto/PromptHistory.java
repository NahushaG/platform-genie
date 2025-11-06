package com.platformgenie.backend.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
public class PromptHistory {
    private final String prompt;
    private final LocalDateTime createdAt;
}
