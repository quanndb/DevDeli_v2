package com.example.storageservice.util;

import com.example.storageservice.entity.File;
import com.example.storageservice.exception.AppExceptions;
import com.example.storageservice.exception.ErrorCode;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

@Component
public class FileStorageUtil {
    @Value("${file.storage.location}")
    private String ROOT_DIR;

    private Path rootStoragePath;

    @PostConstruct
    public void init() {
        this.rootStoragePath = Paths.get(ROOT_DIR).toAbsolutePath().normalize();
    }

    private final String[] BLACK_LIST_FILES = {
            "exe",
            "bat",
    };

    public Path getOrCreateDateBasedDirectory() throws IOException {
        LocalDate now = LocalDate.now();
        String datePath = String.format("%d-%02d-%02d", now.getYear(), now.getMonthValue(), now.getDayOfMonth());

        Path directoryPath = rootStoragePath.resolve(datePath);

        if (!Files.exists(directoryPath)) {
            Files.createDirectories(directoryPath);
        }

        return directoryPath;
    }

    public File storeFile(MultipartFile file, String ownerId, boolean isPublic) throws IOException {
        if(!isValidFile(file)) throw new AppExceptions(ErrorCode.UN_SUPPORT_FILE);

        String originalFileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        String fileExtension = getExtensionName(originalFileName);

        String newFileName = UUID.randomUUID() + fileExtension;

        Path targetLocation = getOrCreateDateBasedDirectory().resolve(newFileName);

        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        return File.builder()
                .ownerId(ownerId)
                .type(file.getContentType())
                .deleted(false)
                .name(originalFileName)
                .path(newFileName)
                .sharing(isPublic)
                .extension(fileExtension.substring(1).toLowerCase())
                .size(file.getSize())
                .build();
    }

    public Path getFilePath(LocalDateTime dateTime, String fileName) {
        String datePath = String.format("%d-%02d-%02d",
                dateTime.getYear(),
                dateTime.getMonthValue(),
                dateTime.getDayOfMonth()
        );

        return rootStoragePath.resolve(Paths.get(datePath, fileName));
    }

    public String getExtensionName(String originalFileName){
        if (originalFileName.contains("..")) {
            throw new AppExceptions(ErrorCode.INVALID_PATH);
        }

        String fileExtension = "";
        int dotIndex = originalFileName.lastIndexOf(".");
        if (dotIndex > 0) {
            fileExtension = originalFileName.substring(dotIndex);
        }

        return fileExtension;
    }

    public boolean isValidFile(MultipartFile file){
        String extensionName = getExtensionName(Objects.requireNonNull(file.getOriginalFilename()))
                .substring(1);
        for(String item : BLACK_LIST_FILES){
            if(item.equals(extensionName)) return false;
        }
        return true;
    }
}
