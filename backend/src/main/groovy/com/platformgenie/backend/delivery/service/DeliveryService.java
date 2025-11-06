package com.platformgenie.backend.delivery.service;

import com.platformgenie.backend.delivery.exception.DeliveryException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryService {
    public void deliverPackage(Path sourcePackage, String environment, Path deliveryRoot) {
        try {
            // Prepare environment folder
            Path envDir = deliveryRoot.resolve(environment);
            if (!Files.exists(envDir)) {
                Files.createDirectories(envDir);
            }

            // Create versioned folder with timestamp
            String timestamp = LocalDateTime.now().toString().replace(":", "-");
            Path targetDir = envDir.resolve("package_" + timestamp);
            Files.createDirectories(targetDir);

            // Use try-with-resources to automatically close the stream
            try (Stream<Path> paths = Files.walk(sourcePackage)) {
                paths.forEach(source -> {
                    try {
                        Path destination = targetDir.resolve(sourcePackage.relativize(source));
                        if (Files.isDirectory(source)) {
                            Files.createDirectories(destination);
                        } else {
                            Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
                        }
                    } catch (IOException e) {
                        throw new DeliveryException("Failed to copy files to delivery target", e);
                    }
                });
            }

            log.info("Terraform package delivered successfully to environment {} at {}", environment, targetDir);

        } catch (IOException e) {
            throw new DeliveryException("Failed to deliver Terraform package", e);
        }
    }
}
