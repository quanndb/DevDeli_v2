package com.example.storageservice.service;

import com.devdeli.common.dto.request.FilePageRequest;
import com.devdeli.common.dto.response.FileResponse;
import com.devdeli.common.dto.response.PageResponse;
import com.example.storageservice.entity.File;
import com.example.storageservice.exception.AppExceptions;
import com.example.storageservice.exception.ErrorCode;
import com.example.storageservice.mapper.FileMapper;
import com.example.storageservice.repository.FileRepository;
import com.example.storageservice.util.FileStorageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileStorageService {
    private final FileRepository fileRepository;
    private final FileMapper fileMapper;
    private final FileStorageUtil fileStorageUtil;

    //  --------------------------------functions---------------------------------------------------
    public List<FileResponse> storeFiles(String ownerId, boolean isPublic, List<MultipartFile> files) throws IOException {
        List<File> saveFiles = new ArrayList<>();
        for(MultipartFile item : files){
            saveFiles.add(fileStorageUtil.storeFile(item, ownerId, isPublic));
        }
        return fileMapper.toListFileResponse(fileRepository.saveAllFiles(saveFiles));
    }

    public ResponseEntity<byte[]> loadFileAsResource(String fileId, boolean isPublic, Integer width, Integer height, Double ratio) throws IOException {
        File foundFile = findFile(fileId);

        if(foundFile.isSharing() != isPublic) throw new AppExceptions(ErrorCode.FILE_NOT_FOUND);

        Path filePath = fileStorageUtil.getFilePath(foundFile.getCreatedDate(), foundFile.getPath());
        Resource resource = new UrlResource(filePath.toUri());

        if (!resource.exists()) {
            throw new AppExceptions(ErrorCode.FILE_NOT_FOUND);
        }
        Resource returnResource = isImageFile(foundFile) ? resizeImage(filePath, width, height, ratio)
                : resource;

        String contentType;
        try {
            contentType = Files.probeContentType(returnResource.getFile().toPath());
        } catch (IOException e) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + returnResource.getFilename() + "\"")
                .body(resource.getContentAsByteArray());
    }

    public FileResponse getFileInfo(String fileId, boolean isPublic) {
        File foundFile = findFile(fileId);
        if(foundFile.isSharing() != isPublic) throw new AppExceptions(ErrorCode.FILE_NOT_FOUND);

        return fileMapper.toFileResponse(foundFile);
    }

    public boolean deleteFile(String fileId, boolean isPublic){
        File foundFile = findFile(fileId);
        if(foundFile.isSharing() != isPublic) throw new AppExceptions(ErrorCode.FILE_NOT_FOUND);
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
        return fileRepository.getFile(fileName)
                .orElseThrow(() -> new AppExceptions(ErrorCode.FILE_NOT_FOUND));
    }

    private boolean isImageFile(File foundFile) {
        return foundFile.getType().startsWith("image/");
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
