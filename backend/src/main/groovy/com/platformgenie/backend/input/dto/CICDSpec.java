package com.platformgenie.backend.input.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CICDSpec {
    private String tool;
    private String buildCommand;
    private String deployCommand;
}
