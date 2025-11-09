package com.platformgenie.backend.orchestration.service;

import com.platformgenie.backend.ai.dto.InfrastructureSpec;
import com.platformgenie.backend.ai.service.AIGenerationService;
import com.platformgenie.backend.assembly.service.TerraformAssemblyService;
import com.platformgenie.backend.delivery.service.DeliveryService;
import com.platformgenie.backend.deploy.service.TerraformCICDService;
import com.platformgenie.backend.input.dto.InfraSpec;
import com.platformgenie.backend.input.service.InputProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TerraformOrchestrationService {

    private final InputProcessingService inputProcessingService;
    private final AIGenerationService aiGenerationService;
    private final TerraformAssemblyService assemblyService;
    private final DeliveryService deliveryService;
    private final TerraformCICDService terraformCICDService;

    public Path generateAndDeliverInfra(InfraSpec infraSpec, String outputDir, String environment) {
        try {
            log.info("Starting infrastructure generation for project {}", infraSpec.getProjectName());

            // Input process
            InfraSpec processedSpec = inputProcessingService.processInput(infraSpec);
            InfrastructureSpec aiSpec = mapToAiSpec(processedSpec);

            // Assemble Terraform project
            Path projectZip = assemblyService.assembleTerraformProject(aiSpec, outputDir);

            // Deliver project
            Path deliveredPath = deliveryService.deliverPackage(projectZip, environment, Path.of(outputDir));
            log.info("Infrastructure delivered to {}", deliveredPath);

            // Deploy automatically (optional)
            log.info("Starting Terraform deployment for environment {}", environment);
            terraformCICDService.deploy(deliveredPath, environment, true);
            log.info("Terraform deployment finished for environment {}", environment);

            return deliveredPath;

        } catch (Exception e) {
            log.error("Error during infrastructure orchestration", e);
            throw new RuntimeException("Failed to generate and deliver infrastructure: " + e.getMessage(), e);
        }
    }


    private InfrastructureSpec mapToAiSpec(InfraSpec infraSpec) {
        // Map your input DTO to the AI layer DTO
        InfrastructureSpec spec = new InfrastructureSpec();
        spec.setId(infraSpec.getProjectName()); // or generate unique ID
        spec.setCloudProvider(infraSpec.getCloudProvider());
        spec.setRegion(infraSpec.getRegion());
        // Map ServiceSpec -> Resource
        List<InfrastructureSpec.Resource> resources = infraSpec.getServices().stream()
                .map(service -> {
                    InfrastructureSpec.Resource r = new InfrastructureSpec.Resource();
                    r.setType(service.getType());
                    r.setName(service.getName());
                    return r;
                })
                .toList(); // adjust mapping as needed
        spec.setResource(resources);
        spec.setDatabaseRequired(infraSpec.getCicd() != null); // example
        return spec;
    }
}
