package com.platformgenie.backend.assembly.service;

import com.platformgenie.backend.ai.dto.InfrastructureSpec;
import com.platformgenie.backend.ai.service.AIGenerationService;
import com.platformgenie.backend.assembly.exception.TerraformFileGenerationException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@RequiredArgsConstructor
public class TerraformAssemblyService {

    private static final Logger log = LoggerFactory.getLogger(TerraformAssemblyService.class);

    private final AIGenerationService aiGenerationService;

    /**
     * Generate and assemble Terraform files into a structured project directory.
     *
     * @param spec      User infrastructure specification
     * @param outputDir Base output directory
     * @return Path to generated ZIP file
     * @throws IOException if file operations fail
     */
    public Path assembleTerraformProject(InfrastructureSpec spec, String outputDir) throws IOException {
        // Generate AI Terraform snippets
        Map<String, String> terraformFiles = aiGenerationService.generateTerraformFiles(spec);

        // Create project folder
        String projectFolderName = "terraform-" + spec.getId();
        Path projectDir = Paths.get(outputDir, projectFolderName);
        Files.createDirectories(projectDir);

        // Write each snippet to file
        for (Map.Entry<String, String> entry : terraformFiles.entrySet()) {
            Path filePath = projectDir.resolve(entry.getKey());
            try (FileWriter writer = new FileWriter(filePath.toFile())) {
                writer.write(entry.getValue());
            }
        }

        // Generate .tfvars file based on user input
        Path tfvarsFile = projectDir.resolve("terraform.tfvars");
        try (FileWriter writer = new FileWriter(tfvarsFile.toFile())) {
            spec.getResource().forEach(resource -> {
                String type = resource.getType() != null ? resource.getType().replaceAll("[^a-zA-Z0-9_]", "_") : "unknown";
                String name = resource.getName() != null ? resource.getName() : "unknown";
                try {
                    writer.write(String.format("%s_name = \"%s\"\n", type, name));
                } catch (IOException e) {
                    throw new TerraformFileGenerationException("Failed to write resource to tfvars file", e);
                }
            });

            if (spec.isDatabaseRequired()) {
                String dbType = spec.getDatabaseType() != null ? spec.getDatabaseType() : "default_db";
                writer.write(String.format("database_type = \"%s\"\n", dbType));
            }
        }

        // Optionally validate Terraform syntax (requires terraform CLI installed)
        validateTerraform(projectDir);

        // Package project into ZIP
        Path zipPath = Paths.get(outputDir, projectFolderName + ".zip");
        zipDirectory(projectDir, zipPath);

        log.info("Terraform project assembled and packaged at {}", zipPath);
        return zipPath;
    }

    private void validateTerraform(Path projectDir) {
        // Placeholder for terraform fmt / validate commands
        log.info("Validating Terraform files at {}", projectDir);
        // Could execute: "terraform fmt -check", "terraform validate"
    }

    private void zipDirectory(Path sourceDir, Path zipFile) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(zipFile))) {
            Files.walk(sourceDir)
                    .filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        ZipEntry zipEntry = new ZipEntry(sourceDir.relativize(path).toString());
                        try {
                            zos.putNextEntry(zipEntry);
                            Files.copy(path, zos);
                            zos.closeEntry();
                        } catch (IOException e) {
                            throw new RuntimeException("Failed to zip file: " + path, e);
                        }
                    });
        }
    }
}
