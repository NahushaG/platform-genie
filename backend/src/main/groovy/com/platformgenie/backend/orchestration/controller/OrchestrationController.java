package com.platformgenie.backend.orchestration.controller;

import com.platformgenie.backend.input.dto.InfraSpec;
import com.platformgenie.backend.orchestration.service.InfraSpecMapper;
import com.platformgenie.backend.orchestration.service.TerraformOrchestrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;

@RestController
@RequestMapping("/api/v1/orchestration")
@RequiredArgsConstructor
public class OrchestrationController {

    private final TerraformOrchestrationService orchestrationService;
    private final InfraSpecMapper infraSpecMapper;

    @PostMapping("/deploy")
    public ResponseEntity<String> deployJson(@RequestBody InfraSpec infraSpec,
                                             @RequestParam("outputDir") String outputDir,
                                             @RequestParam("environment") String environment) {
        InfraSpec spec = infraSpecMapper.fromJson(infraSpec);
        Path zipPath = orchestrationService.generateAndDeliverInfra(spec, outputDir, environment);
        return ResponseEntity.ok("Infrastructure delivered successfully: " + zipPath.toAbsolutePath());
    }

    @PostMapping(value = "/deploy/yaml", consumes = "application/x-yaml")
    public ResponseEntity<String> deployYaml(@RequestBody String yaml,
                                             @RequestParam("outputDir") String outputDir,
                                             @RequestParam("environment") String environment) {
        InfraSpec spec = infraSpecMapper.fromYaml(yaml);
        Path zipPath = orchestrationService.generateAndDeliverInfra(spec, outputDir, environment);
        return ResponseEntity.ok("Infrastructure delivered successfully: " + zipPath.toAbsolutePath());
    }
}
