package com.example.storageservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class FileStorageConfig {
    @Value("${file.storage.location}")
    private String fileStorageLocation;

    public Path getFileStorageLocation() {
        return Paths.get(fileStorageLocation).toAbsolutePath().normalize();
    }
}
