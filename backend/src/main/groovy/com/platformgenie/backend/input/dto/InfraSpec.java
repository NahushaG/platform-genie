package com.platformgenie.backend.input.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InfraSpec {
    private String projectName;
    private String cloudProvider;
    private String region;
    private List<ServiceSpec> services;
    private CICDSpec cicd;
}
