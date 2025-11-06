package com.platformgenie.backend.orchestration.service;

import com.platformgenie.backend.ai.dto.InfrastructureSpec;
import com.platformgenie.backend.ai.service.AIGenerationService;
import com.platformgenie.backend.assembly.service.TerraformAssemblyService;
import com.platformgenie.backend.delivery.service.DeliveryService;
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

    public Path generateAndDeliverInfra(InfraSpec infraSpec, String outputDir, String environment) {
        try {
            log.info("Starting infrastructure generation for project {}", infraSpec.getProjectName());

            // Input process
            InfraSpec processedSpec = inputProcessingService.processInput(infraSpec);
            InfrastructureSpec aiSpec = mapToAiSpec(processedSpec);

            Path projectZip = assemblyService.assembleTerraformProject(aiSpec, outputDir);

            // 3️⃣ Deliver project
            deliveryService.deliverPackage(projectZip, environment, Path.of(outputDir));

            log.info("Infrastructure generation and delivery completed successfully for project {}", infraSpec.getProjectName());
            return projectZip;

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
        spec.setDatabaseRequired(infraSpec.getCicd() != null); // example
        return spec;
    }
}
