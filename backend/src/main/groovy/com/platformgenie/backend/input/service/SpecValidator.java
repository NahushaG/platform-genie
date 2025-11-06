package com.platformgenie.backend.input.service;

import com.platformgenie.backend.input.dto.InfraSpec;
import com.platformgenie.backend.input.exception.InvalidSpecException;
import org.springframework.stereotype.Service;

@Service
public class SpecValidator {
    public void validate(InfraSpec spec){
        if (spec.getProjectName() == null || spec.getProjectName().isEmpty()) {
            throw new InvalidSpecException("Project name is required");
        }

        if (spec.getCloudProvider() == null || spec.getCloudProvider().isEmpty()) {
            spec.setCloudProvider("AWS"); // default
        }

        if (spec.getRegion() == null || spec.getRegion().isEmpty()) {
            spec.setRegion("us-east-1"); // default
        }

        // Add more validation: check services list, CICD config, etc.
        if (spec.getServices() == null || spec.getServices().isEmpty()) {
            throw new InvalidSpecException("At least one service must be defined");
        }
    }
}
