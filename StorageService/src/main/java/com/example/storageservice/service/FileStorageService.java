package com.example.storageservice.service;

import com.example.storageservice.DTO.request.FilePageRequest;
import com.example.storageservice.DTO.response.FileResponse;
import com.example.storageservice.DTO.response.PageResponse;
import com.example.storageservice.config.FileStorageConfig;
import com.example.storageservice.entity.File;
import com.example.storageservice.exception.AppExceptions;
import com.example.storageservice.exception.ErrorCode;
import com.example.storageservice.mapper.FileMapper;
import com.example.storageservice.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class FileStorageService {
    private final Path fileStorageLocation;

    @Value("${openapi.service.server}")
    private String APP_BASE_URL;

    @Autowired
    private FileRepository fileRepository;
    @Autowired
    private TokenVerifier tokenVerifier;
    @Autowired
    private FileMapper fileMapper;

    public FileStorageService(FileStorageConfig config, FileRepository fileRepository) {
        this.fileStorageLocation = config.getFileStorageLocation();
        this.fileRepository = fileRepository;
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new AppExceptions(ErrorCode.CANT_CREATE_DIR);
        }
    }

//  --------------------------------functions---------------------------------------------------
    public boolean storeFiles(String ownerId, boolean isPublic, List<MultipartFile> files) throws IOException {
        List<File> saveFiles = new ArrayList<>();
        for(MultipartFile item : files){
            // process file
            String originalFileName = StringUtils.cleanPath(Objects.requireNonNull(item.getOriginalFilename()));

            if (originalFileName.contains("..")) {
                throw new AppExceptions(ErrorCode.INVALID_PATH);
            }

            String fileExtension = "";
            int dotIndex = originalFileName.lastIndexOf(".");
            if (dotIndex > 0) {
                fileExtension = originalFileName.substring(dotIndex);
            }

            String newFileName = UUID.randomUUID() + fileExtension;

            Path targetLocation = this.fileStorageLocation.resolve(newFileName);
            Files.copy(item.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            // save file
            saveFiles.add(File.builder()
                    .ownerId(ownerId)
                    .type(fileExtension.substring(1).toLowerCase())
                    .deleted(false)
                    .name(originalFileName)
                    .path(newFileName)
                    .sharing(isPublic)
                    .build());
        }

        fileRepository.saveAll(saveFiles);
        return true;
    }

    public Resource loadFileAsResource(String fileName, String token, Integer width, Integer height, Double ratio) throws IOException {
        File foundFile = findFile(fileName);

        Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists()) {
            throw new AppExceptions(ErrorCode.FILE_NOT_FOUND);
        }

        if (isAuthorized(foundFile, token)) {
            return isImageFile(foundFile.getPath()) ? resizeImage(filePath, width, height, ratio) : resource;
        }

        throw new AppExceptions(ErrorCode.UNAUTHENTICATED);
    }

    public FileResponse getFileInfo(String fileName, String token) {
        File foundFile = findFile(fileName);

        if(foundFile.isSharing()){
            return fileMapper.toFileResponse(foundFile);
        }
        else if (token != null && tokenVerifier.verifyToken(token) != null){
            return fileMapper.toFileResponse(foundFile);
        }

        throw new AppExceptions(ErrorCode.UNAUTHENTICATED);
    }

    public boolean deleteFile(String fileName){
        File foundFile = findFile(fileName);
        foundFile.setDeleted(true);
        fileRepository.save(foundFile);
        return true;
    }

    public PageResponse<FileResponse> getFiles(FilePageRequest request) {
        long totalRecords = fileRepository.count(request);
        List<FileResponse> userResponseList = fileRepository.search(request);
        return PageResponse.<FileResponse>builder()
                .page(request.getPage())
                .size(request.getSize())
                .query(request.getQuery())
                .sortedBy(request.getSortedBy())
                .sortDirection(request.getSortDirection().name())
                .first(request.getPage() == 1)
                .last(request.getPage() % request.getSize() == request.getPage())
                .totalRecords(totalRecords)
                .totalPages(request.getPage() % request.getSize())
                .response(userResponseList)
                .build();
    }

//  --------------------------------utilities---------------------------------------------------
    private File findFile(String fileName) {
        return fileRepository.findByPathAndDeletedIsFalse(fileName)
                .orElseThrow(() -> new AppExceptions(ErrorCode.FILE_NOT_FOUND));
    }

    private boolean isAuthorized(File foundFile, String token) {
        return foundFile.isSharing() || (token != null && tokenVerifier.verifyToken(token) != null);
    }

    private boolean isImageFile(String fileName) {
        String lowerCaseFileName = fileName.toLowerCase();
        return lowerCaseFileName.endsWith(".jpg") || lowerCaseFileName.endsWith(".jpeg")
                || lowerCaseFileName.endsWith(".png") || lowerCaseFileName.endsWith(".gif");
    }

    private Resource resizeImage(Path filePath, Integer width, Integer height, Double ratio) throws IOException {
        BufferedImage originalImage = ImageIO.read(filePath.toFile());

        int newWidth = (width != null) ? width : originalImage.getWidth();
        int newHeight = (height != null) ? height : originalImage.getHeight();

        if (ratio != null) {
            newWidth = (int) (originalImage.getWidth() * ratio);
            newHeight = (int) (originalImage.getHeight() * ratio);
        }

        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, originalImage.getType());
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(originalImage, 0, 0, newWidth, newHeight, null);
        g2d.dispose();

        Path tempFilePath = Files.createTempFile("resized-", ".jpg");
        ImageIO.write(resizedImage, "jpg", tempFilePath.toFile());

        return new FileSystemResource(tempFilePath.toFile());
    }
}
