package com.platformgenie.backend.deploy.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
@Slf4j
public class TerraformCICDService {

    /**
     * Deploys Terraform code to the target environment.
     * @param terraformDir The folder containing Terraform code.
     * @param environment Target environment (dev, staging, prod).
     * @param autoApprove If true, applies without confirmation.
     * @throws RuntimeException if deployment fails.
     */
    public void deploy(Path terraformDir, String environment, boolean autoApprove) {
        try {
            log.info("Starting Terraform deployment for {} environment at {}", environment, terraformDir);

            runTerraformCommand(terraformDir, "init");
            runTerraformCommand(terraformDir, "plan", "-out=tfplan");

            if (autoApprove) {
                runTerraformCommand(terraformDir, "apply", "-auto-approve", "tfplan");
            } else {
                log.info("Manual approval required to apply Terraform changes for {}", environment);
            }

            log.info("Terraform deployment completed successfully for {} environment", environment);

        } catch (IOException | InterruptedException e) {
            log.error("Terraform deployment failed for {} environment", environment, e);
            throw new RuntimeException("Terraform deployment failed: " + e.getMessage(), e);
        }
    }

    /**
     * Runs a Terraform CLI command in the given directory.
     */
    private void runTerraformCommand(Path workingDir, String... args) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder();
        pb.command(buildCommand(args));
        pb.directory(workingDir.toFile());
        pb.redirectErrorStream(true); // merge stdout and stderr

        Process process = pb.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                log.info("[terraform] {}", line);
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Terraform command failed with exit code " + exitCode);
        }
    }

    private String[] buildCommand(String... args) {
        String[] cmd = new String[args.length + 1];
        cmd[0] = "terraform"; // assumes terraform is on PATH
        System.arraycopy(args, 0, cmd, 1, args.length);
        return cmd;
    }
}
