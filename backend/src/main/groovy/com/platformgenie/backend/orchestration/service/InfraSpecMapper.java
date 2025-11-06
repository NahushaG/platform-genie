package com.platformgenie.backend.orchestration.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.platformgenie.backend.input.dto.InfraSpec;
import com.platformgenie.backend.input.exception.InvalidSpecException;
import com.platformgenie.backend.input.service.InputProcessingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InfraSpecMapper {

    private final InputProcessingService inputProcessingService;

    public InfraSpec fromYaml(String yaml) {
        try {
            ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
            InfraSpec infraSpec = yamlMapper.readValue(yaml, InfraSpec.class);
            return inputProcessingService.processInput(infraSpec);
        } catch ( JsonProcessingException e) {
            throw new InvalidSpecException("Invalid YAML format: " + e.getMessage());
        }
    }

    public InfraSpec fromJson(InfraSpec infraSpec) {
        return inputProcessingService.processInput(infraSpec);
    }
}

