package com.platformgenie.backend.delivery.service;

import com.platformgenie.backend.delivery.exception.DeliveryException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryService {

    private static final int MAX_DELIVERED_PACKAGES = 3; // keep last 3 versions

    public Path deliverPackage(Path sourcePackage, String environment, Path deliveryRoot) {
        try {
            // Prepare environment folder
            environment = environment.replaceAll("[<>:\"/\\\\|?*]", "_");
            Path envDir = deliveryRoot.resolve(environment);
            if (!Files.exists(envDir)) {
                Files.createDirectories(envDir);
            }

            // Create versioned folder with clean timestamp
            String timestamp = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            Path targetDir = envDir.resolve("package_" + timestamp);
            Files.createDirectories(targetDir);

            // Handle zip files or directories
            if (Files.isRegularFile(sourcePackage) && sourcePackage.toString().endsWith(".zip")) {
                unzip(sourcePackage, targetDir);
            } else if (Files.isDirectory(sourcePackage)) {
                copyDirectory(sourcePackage, targetDir);
            } else {
                throw new DeliveryException("Source package must be a directory or a zip file.");
            }

            // Clean up old delivered packages
            cleanupOldPackages(envDir, MAX_DELIVERED_PACKAGES);

            log.info("Terraform package delivered successfully to environment {} at {}", environment, targetDir);
            return targetDir;

        } catch (IOException e) {
            throw new DeliveryException("Failed to deliver Terraform package", e);
        }
    }

    private void copyDirectory(Path source, Path target) throws IOException {
        try (Stream<Path> paths = Files.walk(source)) {
            for (Path path : (Iterable<Path>) paths::iterator) {
                Path destination = target.resolve(source.relativize(path));
                if (Files.isDirectory(path)) {
                    Files.createDirectories(destination);
                } else {
                    Files.copy(path, destination, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }

    private void unzip(Path zipFilePath, Path targetDir) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(zipFilePath))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                Path newPath = targetDir.resolve(entry.getName());
                if (entry.isDirectory()) {
                    Files.createDirectories(newPath);
                } else {
                    Files.createDirectories(newPath.getParent());
                    Files.copy(zis, newPath, StandardCopyOption.REPLACE_EXISTING);
                }
                zis.closeEntry();
            }
        }
    }

    private void cleanupOldPackages(Path envDir, int maxVersions) throws IOException {
        try (Stream<Path> oldPackages = Files.list(envDir)) {
            oldPackages
                    .filter(Files::isDirectory)
                    .sorted(Comparator.comparing(Path::getFileName).reversed())
                    .skip(maxVersions) // keep only latest N
                    .forEach(path -> {
                        try {
                            deleteRecursively(path);
                            log.info("Deleted old delivery folder: {}", path);
                        } catch (IOException e) {
                            log.warn("Failed to delete old package {}", path, e);
                        }
                    });
        }
    }

    private void deleteRecursively(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            try (Stream<Path> entries = Files.list(path)) {
                for (Path entry : (Iterable<Path>) entries::iterator) {
                    deleteRecursively(entry);
                }
            }
        }
        Files.delete(path);
    }
}
